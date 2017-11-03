package com.bc.pmpheep.migration;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
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
import com.bc.pmpheep.back.util.StringUtil;
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
		//decEduExp();
		//decWorkExp();
		//decTeachExp();
		//decAcade();
		//decLastPosition();
		//decCourseConstruction();
		//decNationalPlan();
		//decTextbook();
		//decResearch();
		decExtension();
		//decPosition();
	}
	
	/**
	 * 作家申报表
	 */
	protected void declaration(){
		String tableName = "writer_declaration";// 要迁移的旧库表名
		JdbcHelper.addColumn(tableName); // 增加new_pk字段
		String sql = "select wd.writerid,wd.materid,wd.writername,wd.sex,wd.birthdate,wd.duties,"
				+ "wd.positional,wd.address,wd.postcode,wd.handset,wd.email,wd.idcardtype,"
				+ "IFNULL(wd.idcardtype,0) idcardtype,"
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
				+ "when ta.isreceivedpaper is null or ta.editauditstate is null then 0 "
				+ "end offline_progress,ta.isreceivedpaper,ta.editauditstate,"
				+ "case when wd.submittype=10 then 0 "
				+ "when wd.submittype=11 then 1 "
				+ "end is_staging,wd.submittype,ta.editauditdate,wd.userid,s.sysflag "
				+ "from writer_declaration wd "
				+ "left join teach_material tm on tm.materid=wd.materid "
				+ "left join ba_organize bo on bo.orgid=wd.unitid "
				+ "left join sys_user s on s.userid=wd.userid "
				+ "left join sys_userext su on su.userid=wd.userid "
				+ "left join teach_applyposition ta on ta.writerid=wd.writerid "
				+ "where su.userid is not null "
				+ "group by wd.writerid";
		List<Map<String, Object>> maps = JdbcHelper.getJdbcTemplate().queryForList(sql);
		int count = 0;// 迁移成功的条目数
		List<Map<String, Object>> excel = new LinkedList<>();
        /* 开始遍历查询结果 */
        for (Map<String, Object> map : maps) {
        	String id = (String) map.get("writerid"); // 旧表主键值
        	String materialid = (String) map.get("materid"); // 教材id
        	String userid = (String) map.get("userid"); // 作家id
        	String sexJudge = (String) map.get("sex");
        	Long onlineProgressJudge = (Long) map.get("online_progress");
        	Long offlineProgressJudge = (Long) map.get("offline_progress");
        	Long isStagingJudge = (Long) map.get("is_staging");
        	Declaration declaration = new Declaration();
        	if (StringUtil.notEmpty(materialid)) {
    			Long materialId = JdbcHelper.getPrimaryKey("teach_material", "materid", materialid);
    			if (null == materialId) {
					map.put(SQLParameters.EXCEL_EX_HEADER, "未找到教材id的结果");
					excel.add(map);
					logger.error("未找到教材id的结果，此结果将被记录在Excel中");
					continue;
				}
    			declaration.setMaterialId(materialId);
            }
        	if (StringUtil.notEmpty(userid)) {
    			Long userId = JdbcHelper.getPrimaryKey("sys_user", "userid", userid);
				if (null == userId) {
					map.put(SQLParameters.EXCEL_EX_HEADER, "未找到作家id的结果");
					excel.add(map);
					logger.error("未找到作家id的结果，此结果将被记录在Excel中");
					continue;
				}
                declaration.setUserId(userId);
            }
        	String realName = (String) map.get("writername"); // 作家姓名
        	if (StringUtil.notEmpty(realName)) {
            	declaration.setRealname(realName);
            } else {
            	map.put(SQLParameters.EXCEL_EX_HEADER, "未找到作家姓名");
                excel.add(map);
                logger.error("未找到作家姓名，此结果将被记录在Excel中");
            }
        	if (null != sexJudge || "".equals(sexJudge)) {
        		Integer sex = Integer.parseInt((String) map.get("sex")); // 性别
            	declaration.setSex(sex);
        	}
        	declaration.setBirthday((Date) map.get("birthdate")); // 生日
        	String experienceNum = (String) map.get("seniority"); // 教龄
        	if ("".equals(experienceNum) || "无".equals(experienceNum) || "、".equals(experienceNum)){
        		experienceNum = null;
            	map.put(SQLParameters.EXCEL_EX_HEADER, "教龄为空字符串或无或顿号，导出Excel进行核准");
            	excel.add(map);
            	logger.info("教龄为空字符串或无或顿号，此结果将被记录在Excel中与专家平台进行核准");
            }
        	Integer experience = null;
            if (null != experienceNum ){
            	if (experienceNum.indexOf("年")!= -1){
            		experienceNum = experienceNum.substring(0, experienceNum.indexOf("年"))
            				.replaceAll("五", "5");
            		map.put(SQLParameters.EXCEL_EX_HEADER, "教龄有年等汉字，导出Excel进行核对");
            		excel.add(map);
            		logger.info("教龄有年等汉字，此结果将被记录在Excel中与专家平台进行核准");
            	}
            	if (experienceNum.indexOf("s")!= -1){
            		experienceNum = experienceNum.substring(0, experienceNum.indexOf("s"));
            		map.put(SQLParameters.EXCEL_EX_HEADER, "教龄有英文字母，导出Excel进行核对");
            		excel.add(map);
            		logger.info("教龄有英文字母，此结果将被记录在Excel中与专家平台进行核准");
            	}
            	if (experienceNum.indexOf(" ")!= -1){
            		experienceNum = experienceNum.substring(0, experienceNum.indexOf(" "));
            		map.put(SQLParameters.EXCEL_EX_HEADER, "教龄中包含空格，导出Excel进行核对");
            		excel.add(map);
            		logger.info("教龄中有空格，此结果将被记录在Excel中与专家平台进行核准");
            	}
            	if (experienceNum.indexOf("岁")!= -1){
            		experienceNum = "32";
            		map.put(SQLParameters.EXCEL_EX_HEADER, "教龄中数据疑似为年龄，导出Excel进行核对");
            		excel.add(map);
            		logger.info("教龄中数据疑似为年龄，此结果将被记录在Excel中与专家平台进行核对");
            	}
            	if (experienceNum.indexOf("年")== -1 && experienceNum.indexOf("s")== -1 && 
            			experienceNum.indexOf("岁")== -1 && experienceNum.length()>2){
            		experienceNum = experienceNum.substring(0,experienceNum.length()-1);
            		map.put(SQLParameters.EXCEL_EX_HEADER, "教龄为三位数数字，有误");
            		excel.add(map);
            		logger.info("教龄为三位数数字，有误，此结果将被记录在Excel进行核对");
            	}
            	experience = Integer.parseInt(experienceNum);
            }
            declaration.setExperience(experience);
        	declaration.setOrgName((String) map.get("workunit")); // 工作单位
        	declaration.setPosition((String) map.get("duties")); // 职务
        	declaration.setTitle((String) map.get("positional")); // 职称
        	declaration.setAddress((String) map.get("address")); // 联系地址
        	declaration.setPostcode((String) map.get("postcode")); // 邮编
        	declaration.setHandphone((String) map.get("handset")); // 手机
        	declaration.setEmail((String) map.get("email")); // 邮箱
        	declaration.setIdtype((Short) map.get("idcardtype1")); // 证件类型
        	declaration.setIdcard((String) map.get("idcard")); // 证件号码
        	declaration.setTelephone((String) map.get("linktel")); // 联系电话
        	declaration.setFax((String) map.get("fax")); // 传真
        	declaration.setOrgId((Long) map.get("org_id")); // 申报单位id
        	if (null != onlineProgressJudge) {
        		Integer onlineProgress = onlineProgressJudge.intValue(); // 审核进度
            	declaration.setOnlineProgress(onlineProgress);
        	} else {
        		map.put(SQLParameters.EXCEL_EX_HEADER, "未找到审核进度");
        		excel.add(map);
        		logger.error("未找到审核进度，此结果将被记录在Excel中");
                continue;
			}
        	String authUserid = (String) map.get("auth_user_id"); // 审核人id
        	if (StringUtil.notEmpty(authUserid)) {
    			Long authUserId = JdbcHelper.getPrimaryKey("sys_user", "userid", userid);
				if (null == authUserId) {
					map.put(SQLParameters.EXCEL_EX_HEADER, "未找到审核人id的结果");
					excel.add(map);
					logger.error("未找到审核人id的结果，此结果将被记录在Excel中");
					continue;
				}
				declaration.setAuthUserId(authUserId);
            }
        	declaration.setAuthDate((Timestamp) map.get("auditdate")); // 审核通过时间
        	if (null != offlineProgressJudge || "".equals(offlineProgressJudge)) {
            	Integer offlineProgress = offlineProgressJudge.intValue(); // 纸质表进度
            	declaration.setOfflineProgress(offlineProgress);
        	} else {
        		map.put(SQLParameters.EXCEL_EX_HEADER, "未找到纸质表进度");
        		excel.add(map);
        		logger.error("未找到纸质表进度，此结果将被记录在Excel中");
                continue;
			}
        	declaration.setPaperDate((Timestamp) map.get("editauditdate")); // 纸质表收到时间
        	String submitType = (String) map.get("submittype"); // 是否暂存
        	if (submitType.length()>2) {
        		map.put(SQLParameters.EXCEL_EX_HEADER, "是否暂存大于2位数");
        		excel.add(map);
        		logger.error("是否暂存大于2位数，此结果将被记录在Excel中");
                continue;
        	}
        	if (null != isStagingJudge) {
        		Integer isStaging = isStagingJudge.intValue(); // 是否暂存
        		declaration.setIsStaging(isStaging);
        	} else {
        		map.put(SQLParameters.EXCEL_EX_HEADER, "未找到是否暂存");
        		excel.add(map);
        		logger.error("未找到是否暂存，此结果将被记录在Excel中");
                continue;
        	}
        	try {
        		declaration = declarationService.addDeclaration(declaration);
			} catch (Exception e) {
				map.put(SQLParameters.EXCEL_EX_HEADER, "添加字段在关联表中无数据错误");
				excel.add(map);
				logger.error("添加字段在关联表中无数据错误，此结果将被记录在Excel中");
				continue;
			}
        	long pk = declaration.getId();
        	JdbcHelper.updateNewPrimaryKey(tableName, pk, "writerid", id); // 更新旧表中new_pk字段
        	count++;
        }
        if (excel.size() > 0) {
        	try {
        		excelHelper.exportFromMaps(excel, tableName, null);
        	} catch (IOException ex) {
        		logger.error("异常数据导出到Excel失败", ex);
        	}
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
        	String id = (String) map.get("leamid"); // 旧表主键值
        	String declarationid = (String) map.get("writerid"); // 申报表id
        	DecEduExp decEduExp = new DecEduExp();
        	if (StringUtil.notEmpty(declarationid)) {
                Long declarationId = JdbcHelper.getPrimaryKey("writer_declaration", "writerid", declarationid);
                if (null == declarationId || "".equals(declarationId)) {
                	map.put(SQLParameters.EXCEL_EX_HEADER, "未找到申报表id");
            		excel.add(map);
            		logger.error("未找到申报表id，此结果将被记录在Excel中");
                    continue;
                }
                decEduExp.setDeclarationId(declarationId);
            }
        	String schoolName = (String) map.get("schoolname"); // 学校名称
        	if (null == schoolName || ("无").equals(schoolName) || "".equals(schoolName)) {
        		map.put(SQLParameters.EXCEL_EX_HEADER, "未找到学校名称");
        		excel.add(map);
        		logger.error("未找到学校名称，此结果将被记录在Excel中");
                continue;
        	}
        	decEduExp.setSchoolName(schoolName);
        	String major = (String) map.get("speciality"); // 所学专业
        	if (null == major || ("无").equals(major) || "".equals(major)) {
        		map.put(SQLParameters.EXCEL_EX_HEADER, "未找到所学专业");
        		excel.add(map);
        		logger.error("未找到所学专业，此结果将被记录在Excel中");
                continue;
        	}
        	decEduExp.setMajor(major);
        	String degree = (String) map.get("record");  // 学历
        	if (null == degree || ("无").equals(degree) || "".equals(degree)) {
        		map.put(SQLParameters.EXCEL_EX_HEADER, "未找到学历");
        		excel.add(map);
        		logger.error("未找到学历，此结果将被记录在Excel中");
                continue;
        	}
        	decEduExp.setDegree(degree);
        	decEduExp.setNote((String) map.get("remark")); // 备注
        	SimpleDateFormat dateChange = new SimpleDateFormat("yyyy-MM"); //时间转换
        	Timestamp startstopDate = (Timestamp) map.get("startstopdate"); // 起始时间
        	if (null != startstopDate || "".equals(startstopDate)) {
        		String dateBegin = dateChange.format(startstopDate);
        		decEduExp.setDateBegin(dateBegin);
        	} else {
        		map.put(SQLParameters.EXCEL_EX_HEADER, "未找到起始时间");
        		excel.add(map);
        		logger.error("未找到起始时间，此结果将被记录在Excel中");
                continue;
			}
        	Timestamp createDate = (Timestamp) map.get("createdate"); // 获取对比时间
        	Timestamp endDate = (Timestamp) map.get("enddate"); // 终止时间
        	if (null != endDate || "".equals(endDate)) {
        		if (endDate.equals(createDate) || endDate.equals("2017-07-29 15:25:03.0")) {
            		decEduExp.setDateEnd("至今");
            	} else {
            		String dateEnd = dateChange.format(endDate);
            		decEduExp.setDateEnd(dateEnd);
    			}
			} else {
				map.put(SQLParameters.EXCEL_EX_HEADER, "未找到终止时间");
        		excel.add(map);
        		logger.error("未找到终止时间，此结果将被记录在Excel中");
                continue;
			}
        	decEduExp.setSort(999); // 显示顺序
        	try {
        		decEduExp = decEduExpService.addDecEduExp(decEduExp);
        	} catch (Exception e) {
				map.put(SQLParameters.EXCEL_EX_HEADER, "添加字段在关联表中无数据错误");
				excel.add(map);
				logger.error("添加字段在关联表中无数据错误，此结果将被记录在Excel中");
				continue;
			}
        	long pk = decEduExp.getId();
        	JdbcHelper.updateNewPrimaryKey(tableName, pk, "leamid", id);
        	count++;
        }
        if (excel.size() > 0) {
        	try {
                excelHelper.exportFromMaps(excel, tableName, null);
            } catch (IOException ex) {
                logger.error("异常数据导出到Excel失败", ex);
            }
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
        String sql = "select * from writer_work w "
        		+ "left join writer_declaration wd on wd.writerid=w.writerid "
        		+ "where w.enddate != '0000-00-00 00:00:00' or w.enddate is null ";
        // 此处保存maps里的数据有一条未查询，已单独导出
        List<Map<String, Object>> maps = JdbcHelper.getJdbcTemplate().queryForList(sql);
        int count = 0;//迁移成功的条目数
        List<Map<String, Object>> excel = new LinkedList<>();
        /* 开始遍历查询结果 */
        for (Map<String, Object> map : maps) {
        	String id = (String) map.get("workid"); // 旧表主键值
        	String declarationid = (String) map.get("writerid"); // 申报表id
        	DecWorkExp decWorkExp = new DecWorkExp();
        	if (StringUtil.notEmpty(declarationid)) {
                Long declarationId = JdbcHelper.getPrimaryKey("writer_declaration", "writerid", declarationid);
                if (null == declarationId) {
                	map.put(SQLParameters.EXCEL_EX_HEADER, "未找到申报表id");
            		excel.add(map);
            		logger.error("未找到申报表id，此结果将被记录在Excel中");
                    continue;
                }
                decWorkExp.setDeclarationId(declarationId);
            }
        	String orgName = (String) map.get("workunitname"); // 工作单位
        	if (null == orgName || ("无").equals(orgName) || "".equals(orgName)) {
        		map.put(SQLParameters.EXCEL_EX_HEADER, "未找到工作单位");
        		excel.add(map);
        		logger.error("未找到工作单位，此结果将被记录在Excel中");
                continue;
        	}
        	decWorkExp.setOrgName(orgName);
        	String position = (String) map.get("position"); // 职位
        	if (null == position || ("无").equals(position) || "".equals(position)) {
        		map.put(SQLParameters.EXCEL_EX_HEADER, "未找到职位");
        		excel.add(map);
        		logger.error("未找到职位，此结果将被记录在Excel中");
                continue;
        	}
        	decWorkExp.setPosition(position);
        	decWorkExp.setNote((String) map.get("remark")); // 备注
        	SimpleDateFormat dateChange = new SimpleDateFormat("yyyy-MM"); //时间转换
        	Timestamp startstopDate = (Timestamp) map.get("startstopdate"); // 起始时间
        	if (null != startstopDate || "".equals(startstopDate)) {
        		String dateBegin = dateChange.format(startstopDate);
        		decWorkExp.setDateBegin(dateBegin);
        	} else {
        		map.put(SQLParameters.EXCEL_EX_HEADER, "未找到起始时间");
        		excel.add(map);
        		logger.error("未找到起始时间，此结果将被记录在Excel中");
                continue;
			}
        	Timestamp createDate = (Timestamp) map.get("createdate"); // 获取对比时间
        	Timestamp endDate = (Timestamp) map.get("enddate"); // 终止时间
        	if (null != endDate || "".equals(endDate)) {
        		if (endDate.equals(createDate) || endDate.equals("2017-07-29 15:25:03.0")) {
        			decWorkExp.setDateEnd("至今");
            	} else {
            		String dateEnd = dateChange.format(endDate);
            		decWorkExp.setDateEnd(dateEnd);
    			}
			} else {
				map.put(SQLParameters.EXCEL_EX_HEADER, "未找到终止时间");
        		excel.add(map);
        		logger.error("未找到终止时间，此结果将被记录在Excel中");
                continue;
			}
        	decWorkExp.setSort(999); // 显示顺序
        	try {
        		decWorkExp = decWorkExpService.addDecWorkExp(decWorkExp);
			} catch (Exception e) {
				map.put(SQLParameters.EXCEL_EX_HEADER, "添加字段在关联表中无数据错误");
				excel.add(map);
				logger.error("添加字段在关联表中无数据错误，此结果将被记录在Excel中");
				continue;
			}
        	long pk = decWorkExp.getId();
        	JdbcHelper.updateNewPrimaryKey(tableName, pk, "workid", id);
        	count++;
        }
        if (excel.size() > 0) {
	        try {
	            excelHelper.exportFromMaps(excel, tableName, null);
	        } catch (IOException ex) {
	            logger.error("异常数据导出到Excel失败", ex);
	        }
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
        String sql = "select * from writer_teach w "
        		+ "left join writer_declaration wd on wd.writerid=w.writerid "
        		+ "where w.enddate != '0000-00-00 00:00:00' or w.enddate is null";
        // 此处保存maps里的数据有一条未查询，已单独导出
        List<Map<String, Object>> maps = JdbcHelper.getJdbcTemplate().queryForList(sql);
        int count = 0;//迁移成功的条目数
        List<Map<String, Object>> excel = new LinkedList<>();
        /* 开始遍历查询结果 */
        for (Map<String, Object> map : maps) {
        	String id = (String) map.get("teachid"); // 旧表主键值
        	String declarationid = (String) map.get("writerid"); // 申报表id
        	DecTeachExp decTeachExp = new DecTeachExp();
        	if (StringUtil.notEmpty(declarationid)) {
                Long declarationId = JdbcHelper.getPrimaryKey("writer_declaration", "writerid", declarationid);
                if (null == declarationId) {
                	map.put(SQLParameters.EXCEL_EX_HEADER, "未找到申报表id");
            		excel.add(map);
            		logger.error("未找到申报表id，此结果将被记录在Excel中");
                    continue;
                }
                decTeachExp.setDeclarationId(declarationId);
            }
        	String schoolName = (String) map.get("schoolname"); // 学校名称
        	if (null == schoolName || schoolName.indexOf("1")>=0 || "".equals(schoolName)) {
        		map.put(SQLParameters.EXCEL_EX_HEADER, "未找到学校名称");
        		excel.add(map);
        		logger.error("未找到学校名称，此结果将被记录在Excel中");
                continue;
        	}
        	decTeachExp.setSchoolName(schoolName);
        	String subject = (String) map.get("subjects"); // 教学科目
        	if (null == subject || subject.indexOf("1")>=0 || "".equals(schoolName)) {
        		map.put(SQLParameters.EXCEL_EX_HEADER, "未找到教学科目");
        		excel.add(map);
        		logger.error("未找到教学科目，此结果将被记录在Excel中");
                continue;
        	}
        	decTeachExp.setSubject(subject);
        	decTeachExp.setNote((String) map.get("remark")); // 备注
        	SimpleDateFormat dateChange = new SimpleDateFormat("yyyy-MM"); //时间转换
        	Timestamp startstopDate = (Timestamp) map.get("startstopdate"); // 起始时间
        	if (null != startstopDate || "".equals(startstopDate)) {
        		String dateBegin = dateChange.format(startstopDate);
        		decTeachExp.setDateBegin(dateBegin);
        	} else {
        		map.put(SQLParameters.EXCEL_EX_HEADER, "未找到起始时间");
        		excel.add(map);
        		logger.error("未找到起始时间，此结果将被记录在Excel中");
                continue;
			}
        	Timestamp createDate = (Timestamp) map.get("createdate"); // 获取对比时间
        	Timestamp endDate = (Timestamp) map.get("enddate"); // 终止时间
        	if (null != endDate || "".equals(endDate)) {
        		if (endDate.equals(createDate) || endDate.equals("2017-07-29 15:25:03.0")) {
        			decTeachExp.setDateEnd("至今");
            	} else {
            		String dateEnd = dateChange.format(endDate);
            		decTeachExp.setDateEnd(dateEnd);
    			}
			} else {
				map.put(SQLParameters.EXCEL_EX_HEADER, "未找到终止时间");
        		excel.add(map);
        		logger.error("未找到终止时间，此结果将被记录在Excel中");
                continue;
			}
        	decTeachExp.setSort(999); // 显示顺序
        	try {
        		decTeachExp = decTeachExpService.addDecTeachExp(decTeachExp);
			} catch (Exception e) {
				map.put(SQLParameters.EXCEL_EX_HEADER, "添加字段在关联表中无数据错误");
				excel.add(map);
				logger.error("添加字段在关联表中无数据错误，此结果将被记录在Excel中");
				continue;
			}
        	long pk = decTeachExp.getId();
        	JdbcHelper.updateNewPrimaryKey(tableName, pk, "teachid", id);
        	count++;
        }
        if (excel.size() > 0) {
	        try {
	            excelHelper.exportFromMaps(excel, tableName, null);
	        } catch (IOException ex) {
	            logger.error("异常数据导出到Excel失败", ex);
	        }
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
        	String id = (String) map.get("acadeid"); // 旧表主键值
        	String declarationid = (String) map.get("writerid"); // 申报表id
        	String rankJudge = (String) map.get("level");
        	DecAcade decAcade = new DecAcade();
        	if (StringUtil.notEmpty(declarationid)) {
                Long declarationId = JdbcHelper.getPrimaryKey("writer_declaration", "writerid", declarationid);
                if (null == declarationId) {
                	map.put(SQLParameters.EXCEL_EX_HEADER, "未找到申报表id");
            		excel.add(map);
            		logger.error("未找到申报表id，此结果将被记录在Excel中");
                    continue;
                }
                decAcade.setDeclarationId(declarationId);
            }
        	String orgName = (String) map.get("organization"); // 兼职学术组织
        	if (null == orgName || ("无").equals(orgName) || "".equals(orgName) 
        			|| "1".equals(orgName)) {
        		map.put(SQLParameters.EXCEL_EX_HEADER, "未找到兼职学术组织");
        		excel.add(map);
        		logger.error("未找到兼职学术组织，此结果将被记录在Excel中");
                continue;
        	}
        	decAcade.setOrgName(orgName);
        	if ("nu".equals(rankJudge)) {
        		map.put(SQLParameters.EXCEL_EX_HEADER, "未找到级别并包含nu");
        		excel.add(map);
        		logger.error("未找到级别并包含nu，此结果将被记录在Excel中");
                continue;
        	}
        	if (null != rankJudge || "".equals(rankJudge)) {
        		Integer rank = Integer.parseInt(rankJudge); // 级别
                decAcade.setRank(rank);
        	} else {
        		map.put(SQLParameters.EXCEL_EX_HEADER, "未找到级别和数据是nu");
        		excel.add(map);
        		logger.error("未找到级别和数据是nu，此结果将被记录在Excel中");
                continue;
			}
        	String position = (String) map.get("duties"); // 职务
        	if (null == position || ("无").equals(position) || "".equals(position)) {
        		map.put(SQLParameters.EXCEL_EX_HEADER, "未找到职务");
        		excel.add(map);
        		logger.error("未找到职务，此结果将被记录在Excel中");
                continue;
        	}
        	decAcade.setPosition(position);
        	decAcade.setNote((String) map.get("remark")); // 备注
        	decAcade.setSort(999);
        	try {
        		decAcade = decAcadeService.addDecAcade(decAcade);
			} catch (Exception e) {
				map.put(SQLParameters.EXCEL_EX_HEADER, "添加字段在关联表中无数据错误");
				excel.add(map);
				logger.error("添加字段在关联表中无数据错误，此结果将被记录在Excel中");
				continue;
			}
        	long pk = decAcade.getId();
        	JdbcHelper.updateNewPrimaryKey(tableName, pk, "acadeid", id);
        	count++;
        }
        if (excel.size() > 0) {
	        try {
	            excelHelper.exportFromMaps(excel, tableName, null);
	        } catch (IOException ex) {
	            logger.error("异常数据导出到Excel失败", ex);
	        }
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
        String sql = "select wm.materpatid,wm.writerid,wm.matername,"
        		+ "case when wm.duties like '%1%' then 1 "
        		+ "when wm.duties like '%2%' then 2 "
        		+ "when wm.duties like '%3%' then 3 "
        		+ "else 0 end position,wm.remark "
        		+ "from writer_materpat wm "
        		+ "left join writer_declaration wd on wd.writerid=wm.writerid ";
        List<Map<String, Object>> maps = JdbcHelper.getJdbcTemplate().queryForList(sql);
        int count = 0;//迁移成功的条目数
        List<Map<String, Object>> excel = new LinkedList<>();
        /* 开始遍历查询结果 */
        for (Map<String, Object> map : maps) {
        	String id = (String) map.get("materpatid"); // 旧表主键值
        	String declarationid = (String) map.get("writerid"); // 申报表id
        	Long positionJudge = (Long) map.get("position");
        	DecLastPosition decLastPosition = new DecLastPosition();
        	if (StringUtil.notEmpty(declarationid)) {
                Long declarationId = JdbcHelper.getPrimaryKey("writer_declaration", "writerid", declarationid);
                if (null == declarationId) {
                	map.put(SQLParameters.EXCEL_EX_HEADER, "未找到申报表id");
            		excel.add(map);
            		logger.error("未找到申报表id，此结果将被记录在Excel中");
                    continue;
                }
                decLastPosition.setDeclarationId(declarationId);
            }
        	String materialName = (String) map.get("matername"); // 教材名称
        	if (null == materialName || ("无").equals(materialName) || "".equals(materialName)) {
        		map.put(SQLParameters.EXCEL_EX_HEADER, "未找到教材名称");
        		excel.add(map);
        		logger.error("未找到教材名称，此结果将被记录在Excel中");
                continue;
        	}
        	decLastPosition.setMaterialName(materialName);
        	if (null != positionJudge || "".equals(positionJudge)) {
        		Integer position = positionJudge.intValue(); // 编写职务
        		decLastPosition.setPosition(position);
        	} else {
        		map.put(SQLParameters.EXCEL_EX_HEADER, "未找到编写职务");
        		excel.add(map);
        		logger.error("未找到编写职务，此结果将被记录在Excel中");
                continue;
			}
        	decLastPosition.setNote((String) map.get("remark")); // 备注
        	decLastPosition.setSort(999); // 显示顺序
        	try {
        		decLastPosition = decLastPositionService.addDecLastPosition(decLastPosition);
			} catch (Exception e) {
				map.put(SQLParameters.EXCEL_EX_HEADER, "添加字段在关联表中无数据错误");
				excel.add(map);
				logger.error("添加字段在关联表中无数据错误，此结果将被记录在Excel中");
				continue;
			}
        	long pk = decLastPosition.getId();
        	JdbcHelper.updateNewPrimaryKey(tableName, pk, "materpatid", id);
        	count++;
        }
        if (excel.size() > 0) {
	        try {
	            excelHelper.exportFromMaps(excel, tableName, null);
	        } catch (IOException ex) {
	            logger.error("异常数据导出到Excel失败", ex);
	        }
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
        	String id = (String) map.get("constructionid"); // 旧表主键值
        	String declarationid = (String) map.get("writerid"); // 申报表id
        	String typeJudge = (String) map.get("type");
        	DecCourseConstruction decCourseConstruction = new DecCourseConstruction();
        	if (StringUtil.notEmpty(declarationid)) {
                Long declarationId = JdbcHelper.getPrimaryKey("writer_declaration", "writerid", declarationid);
                if (null == declarationId) {
                	map.put(SQLParameters.EXCEL_EX_HEADER, "未找到申报表id");
            		excel.add(map);
            		logger.error("未找到申报表id，此结果将被记录在Excel中");
                    continue;
                }
                decCourseConstruction.setDeclarationId(declarationId);
            }
        	String courseName = (String) map.get("curriculumname"); // 课程名称
        	if (null == courseName || ("无").equals(courseName) || "".equals(courseName)) {
        		map.put(SQLParameters.EXCEL_EX_HEADER, "未找到课程名称");
        		excel.add(map);
        		logger.error("未找到课程名称，此结果将被记录在Excel中");
                continue;
        	}
        	decCourseConstruction.setCourseName(courseName);
        	String classHour = (String) map.get("classhour"); // 课程全年课时数
        	if (null == classHour || ("无").equals(classHour)) {
        		map.put(SQLParameters.EXCEL_EX_HEADER, "未找到课程全年课时数");
        		excel.add(map);
        		logger.error("未找到课程全年课时数，此结果将被记录在Excel中");
                continue;
        	}
        	decCourseConstruction.setClassHour(classHour);
        	if (null != typeJudge) {
        		Integer type = Integer.parseInt((String) map.get("type")); // 职务
        		decCourseConstruction.setType(type);
        	} else {
        		map.put(SQLParameters.EXCEL_EX_HEADER, "未找到职务");
        		excel.add(map);
        		logger.error("未找到职务，此结果将被记录在Excel中");
                continue;
			}
        	decCourseConstruction.setNote((String) map.get("remark")); // 备注
        	decCourseConstruction.setSort(999); // 显示顺序
        	try {
        		decCourseConstruction = decCourseConstructionService.addDecCourseConstruction(decCourseConstruction);
			} catch (Exception e) {
				map.put(SQLParameters.EXCEL_EX_HEADER, "添加字段在关联表中无数据错误");
				excel.add(map);
				logger.error("添加字段在关联表中无数据错误，此结果将被记录在Excel中");
				continue;
			}
        	long pk = decCourseConstruction.getId();
        	JdbcHelper.updateNewPrimaryKey(tableName, pk, "constructionid", id);
        	count++;
        }
        if (excel.size() > 0) {
	        try {
	            excelHelper.exportFromMaps(excel, tableName, null);
	        } catch (IOException ex) {
	            logger.error("异常数据导出到Excel失败", ex);
	        }
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
        String sql = "select wa.editorbookid,wa.writerid,wa.matername,wa.booknumber,"
        		+ "case when materlevel like '%1%,%2%' then 3 "
        		+ "when materlevel like '%1%' then 1 "
        		+ "when materlevel like '%2%' then 2 "
        		+ "else 2 end rank,wa.remark "
        		+ "from writer_editorbook wa "
        		+ "left join writer_declaration wd on wd.writerid=wa.writerid ";
        List<Map<String, Object>> maps = JdbcHelper.getJdbcTemplate().queryForList(sql);
        int count = 0;//迁移成功的条目数
        List<Map<String, Object>> excel = new LinkedList<>();
        /* 开始遍历查询结果 */
        for (Map<String, Object> map : maps) {
        	String id = (String) map.get("editorbookid"); // 旧表主键值
        	String declarationid = (String) map.get("writerid"); // 申报表id
        	Long rankJudge = (Long) map.get("rank");
        	DecNationalPlan decNationalPlan = new DecNationalPlan();
        	if (StringUtil.notEmpty(declarationid)) {
                Long declarationId = JdbcHelper.getPrimaryKey("writer_declaration", "writerid", declarationid);
                if (null == declarationId) {
                	map.put(SQLParameters.EXCEL_EX_HEADER, "未找到申报表id");
            		excel.add(map);
            		logger.error("未找到申报表id，此结果将被记录在Excel中");
                    continue;
                }
                decNationalPlan.setDeclarationId(declarationId);
            }
        	String materialName = (String) map.get("matername"); // 教材名称
        	if (null == materialName || ("无").equals(materialName) || "".equals(materialName)) {
        		map.put(SQLParameters.EXCEL_EX_HEADER, "未找到教材名称");
        		excel.add(map);
        		logger.error("未找到教材名称，此结果将被记录在Excel中");
                continue;
        	}
        	decNationalPlan.setMaterialName(materialName);
        	String isbn = (String) map.get("booknumber"); // 标准书号
        	if (("无").equals(isbn)) {
        		map.put(SQLParameters.EXCEL_EX_HEADER, "未找到标准书号");
        		excel.add(map);
        		logger.error("未找到标准书号，此结果将被记录在Excel中");
                continue;
        	}
        	decNationalPlan.setIsbn(isbn);
        	if ("".equals(rankJudge)) {
        		map.put(SQLParameters.EXCEL_EX_HEADER, "未找到教材级别");
        		excel.add(map);
        		logger.error("未找到教材级别，此结果将被记录在Excel中");
                continue;
        	}
        	if (null != rankJudge) {
        		Integer rank = rankJudge.intValue(); // 教材级别
        		decNationalPlan.setRank(rank);
        	} else {
        		map.put(SQLParameters.EXCEL_EX_HEADER, "未找到教材级别");
        		excel.add(map);
        		logger.error("未找到教材级别，此结果将被记录在Excel中");
                continue;
			}
        	decNationalPlan.setNote((String) map.get("remark")); // 备注
        	decNationalPlan.setSort(999); // 显示顺序
        	try {
        		decNationalPlan = decNationalPlanService.addDecNationalPlan(decNationalPlan);
			} catch (Exception e) {
				map.put(SQLParameters.EXCEL_EX_HEADER, "添加字段在关联表中无数据错误");
				excel.add(map);
				logger.error("添加字段在关联表中无数据错误，此结果将被记录在Excel中");
				continue;
			}
        	long pk = decNationalPlan.getId();
        	JdbcHelper.updateNewPrimaryKey(tableName, pk, "editorbookid", id);
        	count++;
        }
        if (excel.size() > 0) {
	        try {
	            excelHelper.exportFromMaps(excel, tableName, null);
	        } catch (IOException ex) {
	            logger.error("异常数据导出到Excel失败", ex);
	        }
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
        String sql = "select wm.materwriteid,wm.writerid,wm.matername,wm.publishing,"
        		+ "case when level like '%1%' then 1 when level like '%2%' then 2 "
        		+ "when level like '%3%' then 3 when level like '%4%' then 4 "
        		+ "else 5 end rank,"
        		+ "case when wm.duty like '%1%' then 1 when wm.duty like '%2%' then 2 "
        		+ "else 3 end position,wm.booknumber,wm.remark,wm.publisdate "
        		+ "from writer_materwrite wm "
        		+ "left join writer_declaration wd on wd.writerid=wm.writerid "
        		+ "where wm.publisdate != '0000-00-00 00:00:00' or wm.publisdate is null ";
        // 此处保存maps里的数据有35条未查询，已单独导出
        List<Map<String, Object>> maps = JdbcHelper.getJdbcTemplate().queryForList(sql);
        int count = 0;//迁移成功的条目数
        List<Map<String, Object>> excel = new LinkedList<>();
        /* 开始遍历查询结果 */
        for (Map<String, Object> map : maps) {
        	String id = (String) map.get("materwriteid"); // 旧表主键值
        	String declarationid = (String) map.get("writerid"); // 申报表id
        	Long rankJudge = (Long) map.get("rank");
        	Long positionJudge = (Long) map.get("position");
        	DecTextbook decTextbook = new DecTextbook();
        	if (StringUtil.notEmpty(declarationid)) {
                Long declarationId = JdbcHelper.getPrimaryKey("writer_declaration", "writerid", declarationid);
                if (null == declarationId) {
                	map.put(SQLParameters.EXCEL_EX_HEADER, "未找到申报表id");
            		excel.add(map);
            		logger.error("未找到申报表id，此结果将被记录在Excel中");
                    continue;
                }
                decTextbook.setDeclarationId(declarationId);
            }
        	String materialName = (String) map.get("matername"); // 教材名称
        	if (null == materialName || ("无").equals(materialName) || "".equals(materialName)) {
        		map.put(SQLParameters.EXCEL_EX_HEADER, "未找到教材名称");
        		excel.add(map);
        		logger.error("未找到教材名称，此结果将被记录在Excel中");
                continue;
        	}
        	decTextbook.setMaterialName(materialName);
        	if (null != rankJudge) {
        		Integer rank = rankJudge.intValue(); // 教材级别
        		decTextbook.setRank(rank);
        	} else {
        		map.put(SQLParameters.EXCEL_EX_HEADER, "未找到教材级别");
        		excel.add(map);
        		logger.error("未找到教材级别，此结果将被记录在Excel中");
                continue;
			}
        	if (null != positionJudge) {
        		Integer position = positionJudge.intValue(); // 编写职务
        		decTextbook.setPosition(position);
        	} else {
        		map.put(SQLParameters.EXCEL_EX_HEADER, "未找到编写职务");
        		excel.add(map);
        		logger.error("未找到编写职务，此结果将被记录在Excel中");
                continue;
			}
        	String publisher = (String) map.get("publishing"); // 出版社
        	if (null == publisher || ("无").equals(publisher) || "".equals(publisher)) {
        		map.put(SQLParameters.EXCEL_EX_HEADER, "未找到出版社");
        		excel.add(map);
        		logger.error("未找到出版社，此结果将被记录在Excel中");
                continue;
        	}
        	decTextbook.setPublisher(publisher);
        	Date publishDate = (Date) map.get("publisdate"); // 出版时间
        	if (null == publishDate) {
        		map.put(SQLParameters.EXCEL_EX_HEADER, "未找到出版时间");
        		excel.add(map);
        		logger.error("未找到出版时间，此结果将被记录在Excel中");
                continue;
        	}
        	decTextbook.setPublishDate(publishDate);
        	String isbn = (String) map.get("booknumber"); // 标准书号
        	if (("无").equals(isbn)) {
        		map.put(SQLParameters.EXCEL_EX_HEADER, "未找到标准书号");
        		excel.add(map);
        		logger.error("未找到标准书号，此结果将被记录在Excel中");
                continue;
        	}
        	decTextbook.setIsbn(isbn);
        	decTextbook.setNote((String) map.get("remark")); // 备注
        	decTextbook.setSort(999); // 显示顺序
        	try {
        		decTextbook = decTextbookService.addDecTextbook(decTextbook);
			} catch (Exception e) {
				map.put(SQLParameters.EXCEL_EX_HEADER, "添加字段在关联表中无数据错误");
				excel.add(map);
				logger.error("添加字段在关联表中无数据错误，此结果将被记录在Excel中");
				continue;
			}
        	long pk = decTextbook.getId();
        	JdbcHelper.updateNewPrimaryKey(tableName, pk, "materwriteid", id);
        	count++;
        }
        if (excel.size() > 0) {
	        try {
	            excelHelper.exportFromMaps(excel, tableName, null);
	        } catch (IOException ex) {
	            logger.error("异常数据导出到Excel失败", ex);
	        }
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
        	String id = (String) map.get("scientresearchid"); // 旧表主键值
        	String declarationid = (String) map.get("writerid"); // 申报表id
        	DecResearch decResearch = new DecResearch();
        	if (StringUtil.notEmpty(declarationid)) {
                Long declarationId = JdbcHelper.getPrimaryKey("writer_declaration", "writerid", declarationid);
                if (null == declarationId) {
                	map.put(SQLParameters.EXCEL_EX_HEADER, "未找到申报表id");
            		excel.add(map);
            		logger.error("未找到申报表id，此结果将被记录在Excel中");
                    continue;
                }
                decResearch.setDeclarationId(declarationId);
            }
        	String researchName = (String) map.get("topicname"); // 课题名称
        	if (null == researchName || ("无").equals(researchName)) {
        		map.put(SQLParameters.EXCEL_EX_HEADER, "未找到课题名称");
        		excel.add(map);
        		logger.error("未找到课题名称，此结果将被记录在Excel中");
                continue;
        	}
        	decResearch.setResearchName(researchName);
        	String approvalUnit = (String) map.get("approvaluntiname"); // 审批单位
        	if (null == approvalUnit || ("无").equals(approvalUnit)) {
        		map.put(SQLParameters.EXCEL_EX_HEADER, "未找到审批单位");
        		excel.add(map);
        		logger.error("未找到审批单位，此结果将被记录在Excel中");
                continue;
        	}
        	decResearch.setApprovalUnit(approvalUnit);
        	String award = (String) map.get("award"); // 获奖情况
        	if (("无").equals(award)) {
        		map.put(SQLParameters.EXCEL_EX_HEADER, "未找到获奖情况");
        		excel.add(map);
        		logger.error("未找到获奖情况，此结果将被记录在Excel中");
                continue;
        	}
        	decResearch.setAward(award);
        	decResearch.setNote((String) map.get("remark")); // 备注
        	decResearch.setSort(999); // 显示顺序
        	try {
        		decResearch = decResearchService.addDecResearch(decResearch);
			} catch (Exception e) {
				map.put(SQLParameters.EXCEL_EX_HEADER, "添加字段在关联表中无数据错误");
				excel.add(map);
				logger.error("添加字段在关联表中无数据错误，此结果将被记录在Excel中");
				continue;
			}
        	long pk = decResearch.getId();
        	JdbcHelper.updateNewPrimaryKey(tableName, pk, "scientresearchid", id);
        	count++;
        }
        if (excel.size() > 0) {
	        try {
	            excelHelper.exportFromMaps(excel, tableName, null);
	        } catch (IOException ex) {
	            logger.error("异常数据导出到Excel失败", ex);
	        }
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
		String sql = "SELECT a.extvalueid,a.expendid,a.writerid,a.content from teach_material_extvalue a "
				+ "LEFT JOIN writer_declaration b on b.writerid=a.writerid "
				+ "LEFT JOIN teach_material_extend c on c.expendid=a.expendid";
		List<Map<String, Object>> maps = JdbcHelper.getJdbcTemplate().queryForList(sql);
		int count = 0; // 迁移成功的条目数
		List<Map<String, Object>> excel = new LinkedList<>();
		String regular = "^[0-9a-zA-Z]{8,10}$"; // 正则表达式判断
		String expenIdsql = "select expendid from teach_material_extend where expendid=?";
        /* 开始遍历查询结果 */
        for (Map<String, Object> map : maps) {
        	Double id = (Double) map.get("extvalueid"); // 旧表主键值
        	String extensionid = (String) map.get("expendid"); // 教材扩展项id
        	String declarationid = (String) map.get("writerid"); // 申报表id
        	DecExtension decExtension = new DecExtension();
        	if (StringUtil.notEmpty(extensionid)) {
                Long extensionId = JdbcHelper.getPrimaryKey("teach_material_extend", "expendid", extensionid);
                if (null == extensionId) {
                	map.put(SQLParameters.EXCEL_EX_HEADER, "未找到教材扩展项id");
            		excel.add(map);
            		logger.error("未找到教材扩展项id，此结果将被记录在Excel中");
                    continue;
                }
                decExtension.setDeclarationId(extensionId);
            }
        	if (StringUtil.notEmpty(declarationid)) {
                Long declarationId = JdbcHelper.getPrimaryKey("writer_declaration", "writerid", declarationid);
                if (null == declarationId) {
                	map.put(SQLParameters.EXCEL_EX_HEADER, "未找到申报表id");
            		excel.add(map);
            		logger.error("未找到申报表id，此结果将被记录在Excel中");
                    continue;
                }
                decExtension.setDeclarationId(declarationId);
            }
        	String content = (String) map.get("content"); // 扩展项内容
        	if (null == content || ("无").equals(content) || ("").equals(content) 
        			|| regular.equals(content)) {
        		map.put(SQLParameters.EXCEL_EX_HEADER, "未找到教材扩展项id");
        		excel.add(map);
        		logger.error("未找到教材扩展项id，此结果将被记录在Excel中");
                continue;
        	}
        	decExtension.setContent(content);
        	try {
        		decExtension = decExtensionService.addDecExtension(decExtension);
			} catch (Exception e) {
				map.put(SQLParameters.EXCEL_EX_HEADER, "添加字段在关联表中无数据错误");
				excel.add(map);
				logger.error("添加字段在关联表中无数据错误，此结果将被记录在Excel中");
				continue;
			}
        	long pk = decExtension.getId();
        	JdbcHelper.updateNewPrimaryKey(tableName, pk, "extvalueid", id);
        	count++;
        }
        if (excel.size() > 0) {
	        try {
	            excelHelper.exportFromMaps(excel, tableName, null);
	        } catch (IOException ex) {
	            logger.error("异常数据导出到Excel失败", ex);
	        }
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
        	String id = (String) map.get("materid"); // 旧表主键值
        	DecPosition decPosition = new DecPosition();
        	String declarationid = (String) map.get("writerid"); // 申报表id
        	if (StringUtil.notEmpty(declarationid)) {
                Long declarationId = JdbcHelper.getPrimaryKey("writer_declaration", "writerid", declarationid);
                if (null == declarationId) {
                	map.put(SQLParameters.EXCEL_EX_HEADER, "未找到申报表id");
            		excel.add(map);
            		logger.error("未找到申报表id，此结果将被记录在Excel中");
                    continue;
                }
                decPosition.setDeclarationId(declarationId);
            }
        	String textbookid = (String) map.get("bookid"); // 书籍id
        	if (StringUtil.notEmpty(textbookid)) {
                Long textbookId = JdbcHelper.getPrimaryKey("teach_bookinfo", "bookid", textbookid);
                if (null == textbookId) {
                	map.put(SQLParameters.EXCEL_EX_HEADER, "未找到书籍id");
            		excel.add(map);
            		logger.error("未找到书籍id，此结果将被记录在Excel中");
                    continue;
                }
                decPosition.setTextbookId(textbookId);
            }
        	Long presetPosition = (Long) map.get("preset_position"); // 申报职务
        	if (null == presetPosition) {
        		map.put(SQLParameters.EXCEL_EX_HEADER, "未找到申报职务");
        		excel.add(map);
        		logger.error("未找到申报职务，此结果将被记录在Excel中");
                continue;
        	}
        	Integer preset = (Integer) presetPosition.intValue();
        	decPosition.setPresetPosition(preset);
        	Long isOnList = (Long) map.get("is_on_list"); // 是否进入预选名单
        	if (null == isOnList) {
        		map.put(SQLParameters.EXCEL_EX_HEADER, "未找到是否进入预选名单");
        		excel.add(map);
        		logger.error("未找到是否进入预选名单，此结果将被记录在Excel中");
                continue;
        	}
        	Integer isOn = isOnList.intValue();
        	decPosition.setIsOnList(isOn);
        	Long chosenPosition = (Long) map.get("chosen_position"); // 遴选职务
        	if (null == chosenPosition) {
        		map.put(SQLParameters.EXCEL_EX_HEADER, "未找到遴选职务");
        		excel.add(map);
        		logger.error("未找到遴选职务，此结果将被记录在Excel中");
                continue;
        	}
        	Integer chosen = chosenPosition.intValue();
        	decPosition.setChosenPosition(chosen);
        	Integer mastersort = (Integer) map.get("mastersort"); // 排位
            decPosition.setRank(mastersort);
        	decPosition.setSyllabusName((String) map.get("syllabus_name")); // 教学大纲名称
        	decPosition.setGmtCreate((Timestamp) map.get("gmt_create")); // 创建时间
        	String outLineUrl = (String) map.get("outlineurl"); // 教学大纲id
        	try {
        		decPosition = decPositionService.addDecPosition(decPosition);
			} catch (Exception e) {
				map.put(SQLParameters.EXCEL_EX_HEADER, "添加字段在关联表中无数据错误");
				excel.add(map);
				logger.error("添加字段在关联表中无数据错误，此结果将被记录在Excel中");
				continue;
			}
        	long pk = decPosition.getId();
        	JdbcHelper.updateNewPrimaryKey(tableName, pk, "materid", id);
        	count++;
        	/* 以下读取教学大纲id并保存在mongoDB中，读取失败时导出到Excel中 */
        	String mongoId = "";
        	if (null != outLineUrl) {
	            try {
	                mongoId = fileService.migrateFile(outLineUrl, FileType.SYLLABUS, pk);
	            } catch (IOException ex) {
	                logger.error("文件读取异常，路径<{}>，异常信息：{}", outLineUrl, ex.getMessage());
	                map.put(SQLParameters.EXCEL_EX_HEADER, "文件读取异常");
	                excel.add(map);
	                continue;
	            }
        	} else {
        		mongoId = outLineUrl;
			}
            decPosition.setSyllabusId(mongoId);
            try {
            	decPositionService.updateDecPosition(decPosition);
			} catch (Exception e) {
				map.put(SQLParameters.EXCEL_EX_HEADER, "更新mongoDB的id错误");
				excel.add(map);
				logger.error("更新mongoDB的id错误，此结果将被记录在Excel中");
				continue;
			}
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