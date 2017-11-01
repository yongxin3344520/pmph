package com.bc.pmpheep.migration;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.bc.pmpheep.back.po.DecAcade;
import com.bc.pmpheep.back.po.DecCourseConstruction;
import com.bc.pmpheep.back.po.DecEduExp;
import com.bc.pmpheep.back.po.DecExtension;
import com.bc.pmpheep.back.po.DecLastPosition;
import com.bc.pmpheep.back.po.DecNationalPlan;
import com.bc.pmpheep.back.po.DecPosition;
import com.bc.pmpheep.back.po.DecResearch;
import com.bc.pmpheep.back.po.DecTeachExp;
import com.bc.pmpheep.back.po.DecTextbook;
import com.bc.pmpheep.back.po.DecWorkExp;
import com.bc.pmpheep.back.po.Declaration;
import com.bc.pmpheep.back.service.DecAcadeService;
import com.bc.pmpheep.back.service.DecCourseConstructionService;
import com.bc.pmpheep.back.service.DecEduExpService;
import com.bc.pmpheep.back.service.DecExtensionService;
import com.bc.pmpheep.back.service.DecLastPositionService;
import com.bc.pmpheep.back.service.DecNationalPlanService;
import com.bc.pmpheep.back.service.DecPositionService;
import com.bc.pmpheep.back.service.DecResearchService;
import com.bc.pmpheep.back.service.DecTeachExpService;
import com.bc.pmpheep.back.service.DecTextbookService;
import com.bc.pmpheep.back.service.DecWorkExpService;
import com.bc.pmpheep.back.service.DeclarationService;
import com.bc.pmpheep.general.bean.FileType;
import com.bc.pmpheep.general.service.FileService;
import com.bc.pmpheep.migration.common.JdbcHelper;
import com.bc.pmpheep.migration.common.SQLParameters;
import com.bc.pmpheep.utils.ExcelHelper;

/**
 * 作家申报与遴选迁移工具类
 * <p>
 * Description:作家申报与遴选模块数据迁移类，此为所有迁移工具的第六步<p>
 * @author tyc
 *
 */
@Component
public class MigrationStageSix {
	
	private final Logger logger = LoggerFactory.getLogger(MigrationStageSix.class);
	
	@Resource
	DeclarationService declarationService;
	@Resource
	DecPositionService decPositionService;
	@Resource
	DecCourseConstructionService decCourseConstructionService;
	@Resource
	DecEduExpService decEduExpService;
	@Resource
	DecWorkExpService decWorkExpService;
	@Resource
	DecNationalPlanService decNationalPlanService;
	@Resource
	DecTeachExpService decTeachExpService;
	@Resource
	DecTextbookService decTextbookService;
	@Resource
	DecAcadeService decAcadeService;
	@Resource
	DecLastPositionService decLastPositionService;
	@Resource
	DecResearchService decResearchService;
	@Resource
	DecExtensionService decExtensionService;
	@Resource
    FileService fileService;
	@Resource
    ExcelHelper excelHelper;
	
	public void start(){
		//declaration();
		decEduExp();
		decWorkExp();
		decTeachExp();
		decAcade();
		decLastPosition();
		decCourseConstruction();
		decNationalPlan();
		decTextbook();
		decResearch();
		decExtension();
		decPosition();
	}
	
	/**
	 * 作家申报表
	 */
	protected void declaration(){
		String tableName = "writer_declaration";// 要迁移的旧库表名
		JdbcHelper.addColumn(tableName); // 增加new_pk字段
		String sql = "select wd.writerid,wd.materid,wd.writername,wd.sex,wd.birthdate,wd.duties,"
				+ "wd.positional,wd.address,wd.postcode,wd.handset,wd.email,wd.idcardtype,"
				+ "wd.idcard,wd.linktel,wd.fax,"
				+ "case when wd.unitid=bo.orgid then 0 "
				+ "end org_id,wd.unitid,"
				+ "case when wd.submittype=10 then 0 "
				+ "when wd.submittype=11 or ta.auditstate=10 then 1 "
				+ "when ta.auditstate=12 or wd.submittype is null then 2 "
				+ "when ta.auditstate=11 and wd.submittype=11 then 3 "
				+ "else 1 end online_progress,wd.submittype,ta.auditstate,"
				+ "case when ta.auditid is null and ta.editauditid is null then null "
				+ "when ta.auditid is not null and ta.editauditid is not null then ta.auditid "
				+ "when ta.auditid is null and ta.editauditid is not null then ta.editauditid "
				+ "when ta.auditid is not null and ta.editauditid is null then ta.auditid "
				+ "end auth_user_id,ta.auditid,ta.editauditid,ta.auditdate,"
				+ "case when ta.isreceivedpaper=0 or ta.editauditstate=10 then 0 "
				+ "when ta.editauditstate=12 then 1 "
				+ "when ta.isreceivedpaper=1 or ta.editauditstate=11 then 2 "
				+ "end offline_progress,ta.isreceivedpaper,ta.editauditstate,"
				+ "case when wd.submittype=10 then 0 "
				+ "when wd.submittype=11 then 1 "
				+ "end is_staging,wd.submittype,ta.editauditdate "
				+ "from writer_declaration wd "
				+ "left join teach_material tm on tm.materid=wd.materid "
				+ "left join ba_organize bo on bo.orgid=wd.unitid "
				+ "left join sys_userext su on su.userid=wd.userid "
				+ "left join teach_applyposition ta on ta.writerid=wd.writerid "
				+ "where su.userid is not null "
				+ "group by wd.writerid";
		List<Map<String, Object>> maps = JdbcHelper.getJdbcTemplate().queryForList(sql);
		int count = 0;// 迁移成功的条目数
		List<Map<String, Object>> excel = new LinkedList<>();
        /* 开始遍历查询结果 */
        for (Map<String, Object> map : maps) {
        	String id = map.get("writerid").toString(); // 旧表主键值
        	Declaration declaration = new Declaration();
        	declaration.setId(0L); // 主键
        	declaration.setMaterialId((Long) map.get("materid")); // 教材id
        	if (null != map.get("writerid") || "" != map.get("writerid")) {
        		declaration.setUserId((Long) map.get("writerid")); // 作家id
        	} else {
        		map.put(SQLParameters.EXCEL_EX_HEADER, "未找到作家id");
        		excel.add(map);
        		logger.error("未找到作家id，此结果将被记录在Excel中");
                continue;
        	}
        	declaration.setRealname((String) map.get("writername")); // 作家姓名
        	declaration.setSex((Short) map.get("sex")); // 性别
        	declaration.setBirthday((Date) map.get("birthdate")); // 生日
        	declaration.setExperience((Short) map.get("seniority")); // 教龄
        	declaration.setOrgName((String) map.get("workunit")); // 工作单位
        	declaration.setPosition((String) map.get("duties")); // 职务
        	declaration.setTitle((String) map.get("positional")); // 职称
        	declaration.setAddress((String) map.get("address")); // 联系地址
        	declaration.setPostcode((String) map.get("postcode")); // 邮编
        	declaration.setHandphone((String) map.get("handset")); // 手机
        	declaration.setEmail((String) map.get("email")); // 邮箱
        	declaration.setIdtype((Short) map.get("idcardtype")); // 证件类型
        	declaration.setIdcard((String) map.get("idcard")); // 证件号码
        	declaration.setTelephone((String) map.get("linktel")); // 联系电话
        	declaration.setFax((String) map.get("fax")); // 传真
        	declaration.setAuthDate((Timestamp) map.get("auditdate")); // 审核通过时间
        	declaration.setPaperDate((Timestamp) map.get("editauditdate")); // 纸质表收到时间
        	
        	Long orgId = (Long) map.get("org_id"); // 申报单位id
        	Short onlineProgress = (Short) map.get("online_progress"); // 审核进度
        	Long authUserId = (Long) map.get("auth_user_id"); // 审核人id
        	
        	Short offlineProgress = (Short) map.get("offline_progress"); // 纸质表进度
        	if (null == offlineProgress) {
        		map.put(SQLParameters.EXCEL_EX_HEADER, "未找到纸质表进度");
        		excel.add(map);
        		logger.error("未找到纸质表进度，此结果将被记录在Excel中");
                continue;
        	}
        	Boolean isStaging = (Boolean) map.get("is_staging"); // 是否暂存
        	if (null == isStaging) {
        		map.put(SQLParameters.EXCEL_EX_HEADER, "未找到是否暂存");
        		excel.add(map);
        		logger.error("未找到是否暂存，此结果将被记录在Excel中");
                continue;
        	}
        	declaration = declarationService.addDeclaration(declaration);
        	long pk = declaration.getId();
        	JdbcHelper.updateNewPrimaryKey(tableName, pk, "writerid", id); // 更新旧表中new_pk字段
        	count++;
        }
        try {
            excelHelper.exportFromMaps(excel, tableName, null);
        } catch (IOException ex) {
            logger.error("异常数据导出到Excel失败", ex);
        }
        logger.info("writer_declaration表迁移完成，异常条目数量：{}", excel.size());
        logger.info("原数据库中共有{}条数据，迁移了{}条数据", maps.size(), count);
	}
	
	/**
	 * 作家学习经历表
	 */
	protected void decEduExp(){
		String tableName = "writer_learn"; //要迁移的旧库表名
        JdbcHelper.addColumn(tableName); //增加new_pk字段
        List<Map<String, Object>> maps = JdbcHelper.queryForList(tableName);//取得该表中所有数据
        int count = 0;//迁移成功的条目数
        List<Map<String, Object>> excel = new LinkedList<>();
        /* 开始遍历查询结果 */
        for (Map<String, Object> map : maps) {
        	String id = map.get("learnid").toString(); // 旧表主键值
        	DecEduExp decEduExp = new DecEduExp();
        	decEduExp.setId(0L); // 主键
        	decEduExp.setDeclarationId((Long) map.get("writerid")); // 申报表id
        	String schoolName = (String) map.get("schoolname"); // 学校名称
        	if (null == schoolName || ("无").equals(schoolName)) {
        		map.put(SQLParameters.EXCEL_EX_HEADER, "未找到学校名称");
        		excel.add(map);
        		logger.error("未找到学校名称，此结果将被记录在Excel中");
                continue;
        	}
        	String major = (String) map.get("speciality"); // 所学专业
        	if (null == major || ("无").equals(major)) {
        		map.put(SQLParameters.EXCEL_EX_HEADER, "未找到所学专业");
        		excel.add(map);
        		logger.error("未找到所学专业，此结果将被记录在Excel中");
                continue;
        	}
        	String degree = (String) map.get("record");  // 学历
        	if (null == degree || ("无").equals(degree)) {
        		map.put(SQLParameters.EXCEL_EX_HEADER, "未找到学历");
        		excel.add(map);
        		logger.error("未找到学历，此结果将被记录在Excel中");
                continue;
        	}
        	decEduExp.setNote((String) map.get("remark")); // 备注
        	decEduExp.setDateBegin((String) map.get("startstopdate")); // 起始时间
        	decEduExp.setDateEnd((String) map.get("enddate")); // 终止时间
        	decEduExp = decEduExpService.addDecEduExp(decEduExp);
        	long pk = decEduExp.getId();
        	JdbcHelper.updateNewPrimaryKey(tableName, pk, "learnid", id);
        	count++;
        }
        try {
            excelHelper.exportFromMaps(excel, tableName, null);
        } catch (IOException ex) {
            logger.error("异常数据导出到Excel失败", ex);
        }
        logger.info("writer_learn表迁移完成，异常条目数量：{}", excel.size());
        logger.info("原数据库中共有{}条数据，迁移了{}条数据", maps.size(), count);
	}
	
	/**
	 * 作家工作经历表
	 */
	protected void decWorkExp(){
		String tableName = "writer_work"; //要迁移的旧库表名
        JdbcHelper.addColumn(tableName); //增加new_pk字段
        List<Map<String, Object>> maps = JdbcHelper.queryForList(tableName);//取得该表中所有数据
        int count = 0;//迁移成功的条目数
        List<Map<String, Object>> excel = new LinkedList<>();
        /* 开始遍历查询结果 */
        for (Map<String, Object> map : maps) {
        	String id = map.get("workid").toString(); // 旧表主键值
        	DecWorkExp decWorkExp = new DecWorkExp();
        	decWorkExp.setId(0L); // 主键
        	decWorkExp.setDeclarationId((Long) map.get("writerid")); // 申报表id
        	String orgName = (String) map.get("workunitname"); // 工作单位
        	if (null == orgName || ("无").equals(orgName)) {
        		map.put(SQLParameters.EXCEL_EX_HEADER, "未找到工作单位");
        		excel.add(map);
        		logger.error("未找到工作单位，此结果将被记录在Excel中");
                continue;
        	}
        	String position = (String) map.get("position"); // 职位
        	if (null == position || ("无").equals(position)) {
        		map.put(SQLParameters.EXCEL_EX_HEADER, "未找到职位");
        		excel.add(map);
        		logger.error("未找到职位，此结果将被记录在Excel中");
                continue;
        	}
        	decWorkExp.setNote((String) map.get("remark")); // 备注
        	decWorkExp.setDateBegin((String) map.get("startstopdate")); // 起始时间
        	decWorkExp.setDateEnd((String) map.get("enddate")); // 终止时间
        	decWorkExp = decWorkExpService.addDecWorkExp(decWorkExp);
        	long pk = decWorkExp.getId();
        	JdbcHelper.updateNewPrimaryKey(tableName, pk, "workid", id);
        	count++;
        }
        try {
            excelHelper.exportFromMaps(excel, tableName, null);
        } catch (IOException ex) {
            logger.error("异常数据导出到Excel失败", ex);
        }
        logger.info("writer_work表迁移完成，异常条目数量：{}", excel.size());
        logger.info("原数据库中共有{}条数据，迁移了{}条数据", maps.size(), count);
	}
	
	/**
	 * 作家教学经历表
	 */
	protected void decTeachExp(){
		String tableName = "writer_teach"; //要迁移的旧库表名
        JdbcHelper.addColumn(tableName); //增加new_pk字段
        List<Map<String, Object>> maps = JdbcHelper.queryForList(tableName);//取得该表中所有数据
        int count = 0;//迁移成功的条目数
        List<Map<String, Object>> excel = new LinkedList<>();
        /* 开始遍历查询结果 */
        for (Map<String, Object> map : maps) {
        	String id = map.get("teachid").toString(); // 旧表主键值
        	DecTeachExp decTeachExp = new DecTeachExp();
        	decTeachExp.setId(0L); // 主键
        	decTeachExp.setDeclarationId((Long) map.get("writerid")); // 申报表id
        	String schoolName = (String) map.get("schoolname"); // 学校名称
        	if (null == schoolName || schoolName.indexOf("1")>=0) {
        		map.put(SQLParameters.EXCEL_EX_HEADER, "未找到学校名称");
        		excel.add(map);
        		logger.error("未找到学校名称，此结果将被记录在Excel中");
                continue;
        	}
        	String subject = (String) map.get("subjects"); // 教学科目
        	if (null == subject || subject.indexOf("1")>=0) {
        		map.put(SQLParameters.EXCEL_EX_HEADER, "未找到教学科目");
        		excel.add(map);
        		logger.error("未找到教学科目，此结果将被记录在Excel中");
                continue;
        	}
        	decTeachExp.setNote((String) map.get("remark")); // 备注
        	decTeachExp.setDateBegin((String) map.get("startstopdate")); // 起始时间
        	decTeachExp.setDateEnd((String) map.get("enddate")); // 终止时间
        	decTeachExp = decTeachExpService.addDecTeachExp(decTeachExp);
        	long pk = decTeachExp.getId();
        	JdbcHelper.updateNewPrimaryKey(tableName, pk, "teachid", id);
        	count++;
        }
        try {
            excelHelper.exportFromMaps(excel, tableName, null);
        } catch (IOException ex) {
            logger.error("异常数据导出到Excel失败", ex);
        }
        logger.info("writer_teach表迁移完成，异常条目数量：{}", excel.size());
        logger.info("原数据库中共有{}条数据，迁移了{}条数据", maps.size(), count);
	}
	
	/**
	 * 作家兼职学术表
	 */
	protected void decAcade(){
		String tableName = "writer_acade"; //要迁移的旧库表名
        JdbcHelper.addColumn(tableName); //增加new_pk字段
        List<Map<String, Object>> maps = JdbcHelper.queryForList(tableName);//取得该表中所有数据
        int count = 0;//迁移成功的条目数
        List<Map<String, Object>> excel = new LinkedList<>();
        /* 开始遍历查询结果 */
        for (Map<String, Object> map : maps) {
        	String id = map.get("acadeid").toString(); // 旧表主键值 
        	DecAcade decAcade = new DecAcade();
        	decAcade.setId(0L); // 主键
        	decAcade.setDeclarationId((Long) map.get("writerid")); // 申报表id
        	String orgName = (String) map.get("organization"); // 兼职学术组织
        	if (null == orgName || ("无").equals(orgName)) {
        		map.put(SQLParameters.EXCEL_EX_HEADER, "未找到兼职学术组织");
        		excel.add(map);
        		logger.error("未找到兼职学术组织，此结果将被记录在Excel中");
                continue;
        	}
        	Integer rank = (Integer) map.get("level"); // 级别
        	if (null == rank) {
        		map.put(SQLParameters.EXCEL_EX_HEADER, "未找到级别");
        		excel.add(map);
        		logger.error("未找到级别，此结果将被记录在Excel中");
                continue;
        	}
        	String position = (String) map.get("duties"); // 职务
        	if (null == position || ("无").equals(position)) {
        		map.put(SQLParameters.EXCEL_EX_HEADER, "未找到职务");
        		excel.add(map);
        		logger.error("未找到职务，此结果将被记录在Excel中");
                continue;
        	}
        	decAcade.setNote((String) map.get("remark")); // 备注
        	decAcade = decAcadeService.addDecAcade(decAcade);
        	long pk = decAcade.getId();
        	JdbcHelper.updateNewPrimaryKey(tableName, pk, "acadeid", id);
        	count++;
        }
        try {
            excelHelper.exportFromMaps(excel, tableName, null);
        } catch (IOException ex) {
            logger.error("异常数据导出到Excel失败", ex);
        }
        logger.info("writer_acade表迁移完成，异常条目数量：{}", excel.size());
        logger.info("原数据库中共有{}条数据，迁移了{}条数据", maps.size(), count);
	}
	
	/**
	 * 作家上套教材参编情况表
	 */
	protected void decLastPosition(){
		String tableName = "writer_materpat"; //要迁移的旧库表名
        JdbcHelper.addColumn(tableName); //增加new_pk字段
        List<Map<String, Object>> maps = JdbcHelper.queryForList(tableName);//取得该表中所有数据
        int count = 0;//迁移成功的条目数
        List<Map<String, Object>> excel = new LinkedList<>();
        /* 开始遍历查询结果 */
        for (Map<String, Object> map : maps) {
        	String id = map.get("materpatid").toString(); // 旧表主键值
        	DecLastPosition decLastPosition = new DecLastPosition();
        	decLastPosition.setId(0L); // 主键
        	decLastPosition.setDeclarationId((Long) map.get("writerid")); // 申报表id
        	String materialName = (String) map.get("matername"); // 教材名称
        	if (null == materialName || ("无").equals(materialName)) {
        		map.put(SQLParameters.EXCEL_EX_HEADER, "未找到教材名称");
        		excel.add(map);
        		logger.error("未找到教材名称，此结果将被记录在Excel中");
                continue;
        	}
        	String position = (String) map.get("duties"); // 编写职务
        	if (null == position) {
        		map.put(SQLParameters.EXCEL_EX_HEADER, "未找到编写职务");
        		excel.add(map);
        		logger.error("未找到编写职务，此结果将被记录在Excel中");
                continue;
        	}
        	decLastPosition.setNote((String) map.get("remark")); // 备注
        	decLastPosition = decLastPositionService.addDecLastPosition(decLastPosition);
        	long pk = decLastPosition.getId();
        	JdbcHelper.updateNewPrimaryKey(tableName, pk, "materpatid", id);
        	count++;
        }
        try {
            excelHelper.exportFromMaps(excel, tableName, null);
        } catch (IOException ex) {
            logger.error("异常数据导出到Excel失败", ex);
        }
        logger.info("writer_materpat表迁移完成，异常条目数量：{}", excel.size());
        logger.info("原数据库中共有{}条数据，迁移了{}条数据", maps.size(), count);
	}
	
	/**
	 * 作家精品课程建设情况表
	 */
	protected void decCourseConstruction(){
		String tableName = "writer_construction "; //要迁移的旧库表名
        JdbcHelper.addColumn(tableName); //增加new_pk字段
        List<Map<String, Object>> maps = JdbcHelper.queryForList(tableName);//取得该表中所有数据
        int count = 0;//迁移成功的条目数
        List<Map<String, Object>> excel = new LinkedList<>();
        /* 开始遍历查询结果 */
        for (Map<String, Object> map : maps) {
        	String id = map.get("constructionid").toString(); // 旧表主键值
        	DecCourseConstruction decCourseConstruction = new DecCourseConstruction();
        	decCourseConstruction.setId(0L); // 主键
        	decCourseConstruction.setDeclarationId((Long) map.get("writerid")); // 申报表id
        	String courseName = (String) map.get("curriculumname"); // 课程名称
        	if (null == courseName || ("无").equals(courseName)) {
        		map.put(SQLParameters.EXCEL_EX_HEADER, "未找到课程名称");
        		excel.add(map);
        		logger.error("未找到课程名称，此结果将被记录在Excel中");
                continue;
        	}
        	String classHour = (String) map.get("classhour"); // 课程全年课时数
        	if (null == classHour || ("无").equals(classHour)) {
        		map.put(SQLParameters.EXCEL_EX_HEADER, "未找到课程全年课时数");
        		excel.add(map);
        		logger.error("未找到课程全年课时数，此结果将被记录在Excel中");
                continue;
        	}
        	decCourseConstruction.setType((Short) map.get("type")); // 职务
        	decCourseConstruction.setNote((String) map.get("remark")); // 备注
        	decCourseConstruction = decCourseConstructionService.addDecCourseConstruction(decCourseConstruction);
        	long pk = decCourseConstruction.getId();
        	JdbcHelper.updateNewPrimaryKey(tableName, pk, "constructionid", id);
        	count++;
        }
        try {
            excelHelper.exportFromMaps(excel, tableName, null);
        } catch (IOException ex) {
            logger.error("异常数据导出到Excel失败", ex);
        }
        logger.info("writer_construction表迁移完成，异常条目数量：{}", excel.size());
        logger.info("原数据库中共有{}条数据，迁移了{}条数据", maps.size(), count);
	}
	
	/**
	 * 作家主编国家级规划教材情况表
	 */
	protected void decNationalPlan(){
		String tableName = "writer_editorbook "; //要迁移的旧库表名
        JdbcHelper.addColumn(tableName); //增加new_pk字段
        List<Map<String, Object>> maps = JdbcHelper.queryForList(tableName);//取得该表中所有数据
        int count = 0;//迁移成功的条目数
        List<Map<String, Object>> excel = new LinkedList<>();
        /* 开始遍历查询结果 */
        for (Map<String, Object> map : maps) {
        	String id = map.get("editorbookid").toString(); // 旧表主键值
        	DecNationalPlan decNationalPlan = new DecNationalPlan();
        	decNationalPlan.setId(0L); // 主键
        	decNationalPlan.setDeclarationId((Long) map.get("writerid")); // 申报表id
        	String materialName = (String) map.get("matername"); // 教材名称
        	if (null == materialName || ("无").equals(materialName)) {
        		map.put(SQLParameters.EXCEL_EX_HEADER, "未找到教材名称");
        		excel.add(map);
        		logger.error("未找到教材名称，此结果将被记录在Excel中");
                continue;
        	}
        	String isbn = (String) map.get("booknumber"); // 标准书号
        	if (null == isbn || ("无").equals(isbn)) {
        		map.put(SQLParameters.EXCEL_EX_HEADER, "未找到标准书号");
        		excel.add(map);
        		logger.error("未找到标准书号，此结果将被记录在Excel中");
                continue;
        	}
        	String rank = (String) map.get("materlevel"); // 教材级别
        	if (null == rank) {
        		map.put(SQLParameters.EXCEL_EX_HEADER, "未找到教材级别");
        		excel.add(map);
        		logger.error("未找到教材级别，此结果将被记录在Excel中");
                continue;
        	}
        	decNationalPlan.setNote((String) map.get("remark")); // 备注
        	decNationalPlan = decNationalPlanService.addDecNationalPlan(decNationalPlan);
        	long pk = decNationalPlan.getId();
        	JdbcHelper.updateNewPrimaryKey(tableName, pk, "editorbookid", id);
        	count++;
        }
        try {
            excelHelper.exportFromMaps(excel, tableName, null);
        } catch (IOException ex) {
            logger.error("异常数据导出到Excel失败", ex);
        }
        logger.info("writer_editorbook表迁移完成，异常条目数量：{}", excel.size());
        logger.info("原数据库中共有{}条数据，迁移了{}条数据", maps.size(), count);
	}
	
	/**
	 * 作家教材编写情况表
	 */
	protected void decTextbook(){
		String tableName = "writer_materwrite "; //要迁移的旧库表名
        JdbcHelper.addColumn(tableName); //增加new_pk字段
        List<Map<String, Object>> maps = JdbcHelper.queryForList(tableName);//取得该表中所有数据
        int count = 0;//迁移成功的条目数
        List<Map<String, Object>> excel = new LinkedList<>();
        /* 开始遍历查询结果 */
        for (Map<String, Object> map : maps) {
        	String id = map.get("materwriteid").toString(); // 旧表主键值
        	DecTextbook decTextbook = new DecTextbook();
        	decTextbook.setId(0L); // 主键
        	decTextbook.setDeclarationId((Long) map.get("writerid")); // 申报表id
        	String materialName = (String) map.get("matername"); // 教材名称
        	if (null == materialName || ("无").equals(materialName)) {
        		map.put(SQLParameters.EXCEL_EX_HEADER, "未找到教材名称");
        		excel.add(map);
        		logger.error("未找到教材名称，此结果将被记录在Excel中");
                continue;
        	}
        	String rank = (String) map.get("level"); // 教材级别
        	if (null == rank || ("无").equals(rank)) {
        		map.put(SQLParameters.EXCEL_EX_HEADER, "未找到教材级别");
        		excel.add(map);
        		logger.error("未找到教材级别，此结果将被记录在Excel中");
                continue;
        	}
        	String position = (String) map.get("duty"); // 编写职务
        	if (null == position || ("无").equals(position)) {
        		map.put(SQLParameters.EXCEL_EX_HEADER, "未找到编写职务");
        		excel.add(map);
        		logger.error("未找到编写职务，此结果将被记录在Excel中");
                continue;
        	}
        	String publisher = (String) map.get("publishing"); // 出版社
        	if (null == publisher || ("无").equals(publisher)) {
        		map.put(SQLParameters.EXCEL_EX_HEADER, "未找到出版社");
        		excel.add(map);
        		logger.error("未找到出版社，此结果将被记录在Excel中");
                continue;
        	}
        	Date publishDate = (Date) map.get("publisdate"); // 出版时间
        	if (null == publishDate) {
        		map.put(SQLParameters.EXCEL_EX_HEADER, "未找到出版时间");
        		excel.add(map);
        		logger.error("未找到出版时间，此结果将被记录在Excel中");
                continue;
        	}
        	String isbn = (String) map.get("booknumber"); // 标准书号
        	if (null == isbn || ("无").equals(isbn)) {
        		map.put(SQLParameters.EXCEL_EX_HEADER, "未找到标准书号");
        		excel.add(map);
        		logger.error("未找到标准书号，此结果将被记录在Excel中");
                continue;
        	}
        	decTextbook.setNote((String) map.get("remark")); // 备注
        	decTextbook = decTextbookService.addDecTextbook(decTextbook);
        	long pk = decTextbook.getId();
        	JdbcHelper.updateNewPrimaryKey(tableName, pk, "materwriteid", id);
        	count++;
        }
        try {
            excelHelper.exportFromMaps(excel, tableName, null);
        } catch (IOException ex) {
            logger.error("异常数据导出到Excel失败", ex);
        }
        logger.info("writer_materwrite表迁移完成，异常条目数量：{}", excel.size());
        logger.info("原数据库中共有{}条数据，迁移了{}条数据", maps.size(), count);
	}
	
	/**
	 * 作家科研情况表
	 */
	protected void decResearch(){
		String tableName = "writer_scientresearch "; //要迁移的旧库表名
        JdbcHelper.addColumn(tableName); //增加new_pk字段
        List<Map<String, Object>> maps = JdbcHelper.queryForList(tableName);//取得该表中所有数据
        int count = 0;//迁移成功的条目数
        List<Map<String, Object>> excel = new LinkedList<>();
        /* 开始遍历查询结果 */
        for (Map<String, Object> map : maps) {
        	String id = map.get("scientresearchid").toString(); // 旧表主键值
        	DecResearch decResearch = new DecResearch();
        	decResearch.setId(0L); // 主键
        	decResearch.setDeclarationId((Long) map.get("writerid")); // 申报表id
        	String researchName = (String) map.get("topicname"); // 课题名称
        	if (null == researchName || ("无").equals(researchName)) {
        		map.put(SQLParameters.EXCEL_EX_HEADER, "未找到课题名称");
        		excel.add(map);
        		logger.error("未找到课题名称，此结果将被记录在Excel中");
                continue;
        	}
        	String approvalUnit = (String) map.get("approvaluntiname"); // 审批单位
        	if (null == approvalUnit || ("无").equals(approvalUnit)) {
        		map.put(SQLParameters.EXCEL_EX_HEADER, "未找到审批单位");
        		excel.add(map);
        		logger.error("未找到审批单位，此结果将被记录在Excel中");
                continue;
        	}
        	String award = (String) map.get("award"); // 获奖情况
        	if (null == award || ("无").equals(award)) {
        		map.put(SQLParameters.EXCEL_EX_HEADER, "未找到获奖情况");
        		excel.add(map);
        		logger.error("未找到获奖情况，此结果将被记录在Excel中");
                continue;
        	}
        	decResearch.setNote((String) map.get("remark")); // 备注
        	decResearch = decResearchService.addDecResearch(decResearch);
        	long pk = decResearch.getId();
        	JdbcHelper.updateNewPrimaryKey(tableName, pk, "scientresearchid", id);
        	count++;
        }
        try {
            excelHelper.exportFromMaps(excel, tableName, null);
        } catch (IOException ex) {
            logger.error("异常数据导出到Excel失败", ex);
        }
        logger.info("writer_scientresearch表迁移完成，异常条目数量：{}", excel.size());
        logger.info("原数据库中共有{}条数据，迁移了{}条数据", maps.size(), count);
	}
	
	/**
	 * 作家扩展项填报表
	 */
	protected void decExtension(){
		String tableName = "teach_material_extvalue"; // 要迁移的旧库表名
		JdbcHelper.addColumn(tableName); // 增加new_pk字段
		String sql = "SELECT a.extvalueid,c.expendid,b.writerid,a.content from teach_material_extvalue a "
				+ "LEFT JOIN writer_declaration b on b.writerid=a.writerid "
				+ "LEFT JOIN teach_material_extend c on c.expendid=a.expendid";
		List<Map<String, Object>> maps = JdbcHelper.getJdbcTemplate().queryForList(sql);
		int count = 0; // 迁移成功的条目数
		List<Map<String, Object>> excel = new LinkedList<>();
		String regular = "^[0-9a-zA-Z]{8,10}$"; // 正则表达式判断
        /* 开始遍历查询结果 */
        for (Map<String, Object> map : maps) {
        	String id = map.get("extvalueid").toString(); // 旧表主键值
        	DecExtension decExtension = new DecExtension();
        	decExtension.setId(0L); // 主键
        	String extensionId = (String) map.get("expendid"); // 教材扩展项id
        	if (null == extensionId || ("").equals(extensionId)) {
        		map.put(SQLParameters.EXCEL_EX_HEADER, "未找到教材扩展项id");
        		excel.add(map);
        		logger.error("未找到教材扩展项id，此结果将被记录在Excel中");
                continue;
        	}
        	String declarationId = (String) map.get("writerid"); // 申报表id
        	if (null == declarationId || ("").equals(declarationId)) {
        		map.put(SQLParameters.EXCEL_EX_HEADER, "未找到教材扩展项id");
        		excel.add(map);
        		logger.error("未找到教材扩展项id，此结果将被记录在Excel中");
                continue;
        	}
        	String content = (String) map.get("content"); // 扩展项内容
        	if (("无").equals(content) || ("").equals(content) || regular.equals(content)) {
        		map.put(SQLParameters.EXCEL_EX_HEADER, "未找到教材扩展项id");
        		excel.add(map);
        		logger.error("未找到教材扩展项id，此结果将被记录在Excel中");
                continue;
        	}
        	decExtension = decExtensionService.addDecExtension(decExtension);
        	long pk = decExtension.getId();
        	JdbcHelper.updateNewPrimaryKey(tableName, pk, "extvalueid", id);
        	count++;
        }
        try {
            excelHelper.exportFromMaps(excel, tableName, null);
        } catch (IOException ex) {
            logger.error("异常数据导出到Excel失败", ex);
        }
        logger.info("teach_material_extvalue表迁移完成，异常条目数量：{}", excel.size());
        logger.info("原数据库中共有{}条数据，迁移了{}条数据", maps.size(), count);
	}
	
	/**
	 * 作家申报职位表（多对多）
	 */
	protected void decPosition(){
		String tableName = "teach_applyposition"; // 要迁移的旧库表名
		JdbcHelper.addColumn(tableName); // 增加new_pk字段
		String sql = "select ta.materid,ta.writerid,ta.bookid,"
				+ "case when ta.positiontype=1 then 1 when ta.positiontype=2 then 2 "
				+ "when ta.positiontype=3 then 3 "
				+ "else 0 end preset_position,"
				+ "if(tp.appposiid is not null,true,false) is_on_list,"
				+ "case when tp.positiontype=1 then 1 when tp.positiontype=2 then 2 "
				+ "when tp.positiontype=3 then 3 "
				+ "else 0 end chosen_position,tp.mastersort,ta.outlineurl,ta.outlinename,"
				+ "ifnull(wd.updatedate,wd.createdate) gmt_create "
				+ "from teach_applyposition ta "
				+ "left join teach_positionset tp on tp.appposiid=ta.appposiid "
				+ "left join writer_declaration wd on wd.writerid=ta.writerid "
				+ "left join teach_bookinfo tb on tb.bookid=ta.bookid ";
		List<Map<String, Object>> maps = JdbcHelper.getJdbcTemplate().queryForList(sql);
		int count = 0; // 迁移成功的条目数
		List<Map<String, Object>> excel = new LinkedList<>();
		 /* 开始遍历查询结果 */
        for (Map<String, Object> map : maps) {
        	String id = map.get("materid").toString(); // 旧表主键值
        	DecPosition decPosition = new DecPosition();
        	decPosition.setId(0L); // 主键
        	String declarationId = (String) map.get("writerid"); // 申报表id
        	if (("").equals(declarationId)) {
        		map.put(SQLParameters.EXCEL_EX_HEADER, "未找到申报表id");
        		excel.add(map);
        		logger.error("未找到申报表id，此结果将被记录在Excel中");
                continue;
        	}
        	String textbookId = (String) map.get("bookid"); // 书籍id
        	if (null == textbookId || ("").equals(textbookId)) {
        		map.put(SQLParameters.EXCEL_EX_HEADER, "未找到书籍id");
        		excel.add(map);
        		logger.error("未找到书籍id，此结果将被记录在Excel中");
                continue;
        	}
        	decPosition.setPresetPosition((Short) map.get("preset_position")); // 申报职务
        	decPosition.setIsOnList((Boolean) map.get("is_on_list")); // 是否进入预选名单
        	decPosition.setChosenPosition((Short) map.get("chosen_position")); // 遴选职务
        	decPosition.setRank((Short) map.get("mastersort")); // 排位
        	decPosition.setSyllabusName((String) map.get("syllabus_name")); // 教学大纲名称
        	decPosition.setGmtCreate((Timestamp) map.get("gmt_create")); // 创建时间
        	String outLineUrl = (String) map.get("outlineurl"); // 教学大纲id
        	long pk = decPosition.getId();
        	JdbcHelper.updateNewPrimaryKey(tableName, pk, "materid", id);
        	count++;
        	decPosition = decPositionService.addDecPosition(decPosition);
        	/* 以下读取教学大纲id并保存在mongoDB中，读取失败时导出到Excel中 */
        	String mongoId = "";
            try {
                mongoId = fileService.migrateFile(outLineUrl, FileType.SYLLABUS, pk);
            } catch (IOException ex) {
                logger.error("文件读取异常，路径<{}>，异常信息：{}", outLineUrl, ex.getMessage());
                map.put(SQLParameters.EXCEL_EX_HEADER, "文件读取异常");
                excel.add(map);
                continue;
            }
            decPosition.setSyllabusId(mongoId);
            decPositionService.updateDecPosition(decPosition);
        }
        if (excel.size() > 0) {
        	try {
                excelHelper.exportFromMaps(excel, tableName, null);
            } catch (IOException ex) {
                logger.error("异常数据导出到Excel失败", ex);
            }
        }
        logger.info("teach_applyposition表迁移完成，异常条目数量：{}", excel.size());
        logger.info("原数据库中共有{}条数据，迁移了{}条数据", maps.size(), count);
	}
}