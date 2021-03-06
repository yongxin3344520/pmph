package com.bc.pmpheep.migration;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bc.pmpheep.back.po.CmsContent;
import com.bc.pmpheep.back.po.Material;
import com.bc.pmpheep.back.po.MaterialContact;
import com.bc.pmpheep.back.po.MaterialExtension;
import com.bc.pmpheep.back.po.MaterialExtra;
import com.bc.pmpheep.back.po.MaterialNoteAttachment;
import com.bc.pmpheep.back.po.MaterialNoticeAttachment;
import com.bc.pmpheep.back.po.MaterialOrg;
import com.bc.pmpheep.back.po.MaterialProjectEditor;
import com.bc.pmpheep.back.po.MaterialType;
import com.bc.pmpheep.back.po.Org;
import com.bc.pmpheep.back.po.PmphUser;
import com.bc.pmpheep.back.service.CmsContentService;
import com.bc.pmpheep.back.service.MaterialContactService;
import com.bc.pmpheep.back.service.MaterialExtensionService;
import com.bc.pmpheep.back.service.MaterialExtraService;
import com.bc.pmpheep.back.service.MaterialNoteAttachmentService;
import com.bc.pmpheep.back.service.MaterialNoticeAttachmentService;
import com.bc.pmpheep.back.service.MaterialOrgService;
import com.bc.pmpheep.back.service.MaterialProjectEditorService;
import com.bc.pmpheep.back.service.MaterialService;
import com.bc.pmpheep.back.service.MaterialTypeService;
import com.bc.pmpheep.back.service.OrgService;
import com.bc.pmpheep.back.service.PmphUserService;
import com.bc.pmpheep.back.service.common.SystemMessageService;
import com.bc.pmpheep.back.util.DateUtil;
import com.bc.pmpheep.back.util.ObjectUtil;
import com.bc.pmpheep.back.util.StringUtil;
import com.bc.pmpheep.general.bean.FileType;
import com.bc.pmpheep.general.po.Content;
import com.bc.pmpheep.general.service.ContentService;
import com.bc.pmpheep.general.service.FileService;
import com.bc.pmpheep.migration.common.JdbcHelper;
import com.bc.pmpheep.migration.common.SQLParameters;
import com.bc.pmpheep.utils.ExcelHelper;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Mryang
 *
 * @createDate 2017年11月2日 下午12:05:57
 *
 */
@Component
public class MigrationStageFour {

	Logger logger = LoggerFactory.getLogger(MigrationStageFour.class);
	// 用来储存MaterialExtra返回的主键，供后面使用 Map<教材id,教材通知备注表id>
	public static Map<Long, Long> mps = new HashMap<Long, Long>(12);

	@Resource
	private ExcelHelper excelHelper;

	@Autowired
	private MaterialTypeService materialTypeService;
	@Autowired
	private MaterialService materialService;
	@Autowired
	private MaterialExtensionService materialExtensionService;
	@Autowired
	private MaterialExtraService materialExtraService;
	@Autowired
	private MaterialNoticeAttachmentService materialNoticeAttachmentService;
	@Autowired
	private FileService fileService;
	@Autowired
	private MaterialNoteAttachmentService materialNoteAttachmentService;
	@Autowired
	private MaterialContactService materialContactService;
	@Autowired
	private MaterialOrgService materialOrgService;
	@Autowired
	private MaterialProjectEditorService materialProjectEditorService;
	@Autowired
	private OrgService orgService;
	@Autowired
	private PmphUserService pmphUserService;
	@Autowired
	private CmsContentService cmsContentService;
	@Autowired
	private ContentService contentService;

	// 用来装载向客户导出的信息
	private List<Object[]> excptionList = new ArrayList<Object[]>();
	// 用来装所有的教材
	private List<Material>  materials    = new ArrayList<Material>();

	public void start() {
		Date begin = new Date();
		materialType();
		material();
		materialExtension();
		materialExtra();
		transferMaterialNoticeAttachment();
		transferMaterialNoteAttachment();
		transferMaterialContact();
		materialOrg();
		materialPprojectEeditor();
		//新增教材社区数据
		addMaterialCommunity();
		try {
			excelHelper.exportFromList(excptionList, "教材块问题数据导出", "For客户");
		} catch (IOException e) {
			logger.error("异常数据导出到Excel失败", e);
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("迁移第四步运行结束，用时：{}", JdbcHelper.getPastTime(begin));
	}
	
	

	protected void materialType() {
		String tableName = "sys_booktypes";
		JdbcHelper.addColumn(tableName); // 增加new_pk字段
		List<Map<String, Object>> maps = JdbcHelper.queryForList(tableName);// 取得该表中所有数据
		List<Map<String, Object>> excel = new LinkedList<>();
		Map<String, Object> result = new LinkedHashMap<>();
		int count = 0;
		int correctCount = 0;//统计正常数据的数量
		int[] state = {0,0,0};//判断该数据是否有相应异常状况的标识
		StringBuilder reason = new StringBuilder();
		StringBuilder dealWith = new StringBuilder();
		// 模块名称
		excptionList.add(new Object[] { "教材类别" });
		// 模块标题
		excptionList.add(new Object[] { "类别名称", "问题", "原因分析", "处理方式" });
		int excptionListOldSize = excptionList.size();
		// 插入除parentid和path的字段；
		for (Map<String, Object> map : maps) {
			/*
			 * 因此表有主要级字段和次要级字段，次要级字段插入新表同时也需导出Excel，因此异常信息不止一条， 用StringBulider进行拼接成最终的异常信息
			 */
			StringBuilder exception = new StringBuilder();
			BigDecimal bookTypesID = (BigDecimal) map.get("BookTypesID");
			BigDecimal parentTypesId = (BigDecimal) map.get("ParentTypesID");
			String typeName = (String) map.get("TypeName");
			if (StringUtil.isEmpty(typeName)) {
				map.put(SQLParameters.EXCEL_EX_HEADER, exception.append("类型名称为空。"));
				excptionList.add(new Object[] { "", "类型名称为空", "导入的数据错误", "不导入该条数据" });
				excel.add(map);
				if (state[0] == 0){
					reason.append("找不到教材类型名称。");
					dealWith.append("放弃迁移。");
					state[0] = 1;
				}
				continue;
			}
			/*
			 * 原数据库系统表的数据是按照父-子顺序排列的，因此不许循环可以直接调用获取父节点和path的方法
			 */
			Long parentId = 0L;
			if (parentTypesId.intValue() != 0) {
				parentId = JdbcHelper.getPrimaryKey(tableName, "BookTypesID", parentTypesId);
			}
			String path = JdbcHelper.getPath(tableName, "BookTypesID", "ParentTypesID", parentTypesId);
			Integer sort = (Integer) map.get("Sortno");
			if (ObjectUtil.notNull(sort) && sort < 0) {
				map.put(SQLParameters.EXCEL_EX_HEADER, exception.append("排序为负数。"));
				excptionList.add(new Object[] { typeName, "排序不正确", "导入的数据错误", "设置为'999'导入新库表" });
				sort = 999;
				excel.add(map);
				if (state[1] == 0){
					reason.append("排序码为负数。");
					dealWith.append("设为默认值999迁入数据库。");
					state[1] = 1;
				}
			}
			String note = (String) map.get("Remark");
			// 书籍分类备注信息比较重要，虽然有默认值，但仍认为是次要级字段
			if (StringUtil.isEmpty(note)) {
				map.put(SQLParameters.EXCEL_EX_HEADER, exception.append("备注为空。"));
				excptionList.add(new Object[] { typeName, "备注为空", "导入的数据错误", "设置为和类型名称一样导入新库表" });
				note = typeName;
				excel.add(map);
				if (state[2] == 0){
					reason.append("无备注信息。");
					dealWith.append("备注可以为空，但教材分类备注信息较重要，因此记录下来，照常迁入数据库。");
					state[2] = 1;
				}
			}
			MaterialType materialType = new MaterialType();
			materialType.setParentId(parentId);
			materialType.setPath(path);
			materialType.setTypeName(typeName);
			materialType.setSort(sort);
			materialType.setNote(note);
			materialType = materialTypeService.addMaterialType(materialType);
			count++;
			long pk = materialType.getId();
			// 更新旧表中new_pk字段
			JdbcHelper.updateNewPrimaryKey(tableName, pk, "BookTypesID", bookTypesID);
			if (null == map.get("exception")){
				correctCount++;
			}
		}
		// 没有错误数据
		if (excptionList.size() == excptionListOldSize) {
			excptionList.remove(excptionList.size() - 1);
			excptionList.remove(excptionList.size() - 1);
		} else {
			// 插入一个空行
			excptionList.add(new String[] { "" });
		}
		if (excel.size() > 0) {
			try {
				excelHelper.exportFromMaps(excel, "教材类型表", "material_type");
			} catch (IOException ex) {
				logger.error("异常数据导出到Excel失败", ex);
			}
		}
		if (correctCount != maps.size()){
			result.put(SQLParameters.EXCEL_HEADER_TABLENAME, "material_type");
        	result.put(SQLParameters.EXCEL_HEADER_DESCRIPTION, "教材类型表");
        	result.put(SQLParameters.EXCEL_HEADER_SUM_DATA, maps.size());
        	result.put(SQLParameters.EXCEL_HEADER_MIGRATED_DATA, count);
        	result.put(SQLParameters.EXCEL_HEADER_CORECT_DATA, correctCount);
        	result.put(SQLParameters.EXCEL_HEADER_TRANSFERED_DATA, count - correctCount);
        	result.put(SQLParameters.EXCEL_HEADER_NO_MIGRATED_DATA, maps.size() - count);
        	result.put(SQLParameters.EXCEL_HEADER_EXCEPTION_REASON, reason.toString());
        	result.put(SQLParameters.EXCEL_HEADER_DEAL_WITH, dealWith.toString());
        	SQLParameters.STATISTICS_RESULT.add(result);
		}
		logger.info("'{}'表迁移完成，异常条目数量：{}", tableName, excel.size());
		logger.info("原数据库中共有{}条数据，迁移了{}条数据", maps.size(), count);
		// 记录信息
		Map<String, Object> msg = new HashMap<String, Object>();
		msg.put("result", "" + tableName + " 表迁移完成" + count + "/" + maps.size());
		SQLParameters.STATISTICS.add(msg);
	}

	protected void material() {
		String tableName = "teach_material";
		String sql = "select " + "a.materid, " + "a.matername, " + "a.teachround round, " + "i.new_pk  booktypesid, "
				+ "a.showenddate, " + "a.enddate, " + "a.agedeaddate, " + "a.mailaddress, "
				+ "CASE WHEN a.flowtype=10 THEN '0' ELSE '1' END is_published,"
				+ "CASE WHEN a.flowtype=12 THEN '1' ELSE '0' END is_all_textbook_published, "
				+ "j.new_pk DepartmentId , " + "d.newuserid director, " + "a.isbookmulti, " + "a.ispositionmulti, "
				+ "a.isuselearnexp, " + "a.isfilllearnexp, " + "a.isuseworkexp, " + "a.isfillworkexp, "
				+ "a.isuseteachexp, " + "a.isfillteachexp, " + "a.isuseacadeexp, " + "a.isfillacadeexp, "
				+ "a.isusematerpartexp, " + "a.isfillmaterpartexp, " + "a.isusecountry, " + "a.isfillcountry, "
				+ "a.isuseprovexce, " + "a.isfillprovexce, " + "a.isuseschoolconstr, " + "a.isfillschoolconstr, "
				+ "a.isuseeditormater, " + "a.isfilleditormater, " + "a.isusematerwrite, " + "a.isfillmaterwrite, "
				+ "a.isuseothermaterwrite, " + "a.isfillothermaterwrite, " + "a.isusescientresearch, "
				+ "a.isfillscientresearch, " + "if(a.isdelete =1 ,true,false) isdelete, " + "a.createdate, "
				+ "g.new_pk founder_id, " + "a.updatedate, " + "ifnull(h.new_pk,g.new_pk) mender_id  " + // 如果更新者为空那么默认创建者
				"from teach_material a  " + "LEFT JOIN ( "
				+ "select DISTINCT e.materid,d.rolename,a.userid,a.new_pk newuserid,f.new_pk neworgid from sys_user  a "
				+ "LEFT JOIN  sys_userorganize b on b.userid=a.userid "
				+ "LEFT JOIN  sys_userrole   c on   c.userid =a.userid  "
				+ "LEFT JOIN  sys_role   d  on d.roleid =c.roleid "
				+ "LEFT JOIN teach_material e on e.createorgid = b.orgid  "
				+ "LEFT JOIN ba_organize f on f.orgid = b.orgid "
				+ "where d.rolename like '%主任%' and e.materid is not null  GROUP BY e.materid "
				+ ") d on d.materid = a.materid " + "LEFT JOIN sys_user g on g.userid = a.createuserid "
				+ "LEFT JOIN sys_user h on h.userid = a.updateuserid  "
				+ "LEFT JOIN sys_booktypes i on i.BookTypesID = a. booktypesid  "
				+ "LEFT JOIN ba_organize j on a.createorgid = j.orgid "
				+ "LEFT JOIN site_article k ON a.materid = k.materid " + "WHERE true GROUP BY a.materid ;";
		JdbcHelper.addColumn(tableName); // 增加new_pk字段
		List<Map<String, Object>> maps = JdbcHelper.getJdbcTemplate().queryForList(sql);
		List<Map<String, Object>> excel = new LinkedList<>();
		Map<String, Object> result = new LinkedHashMap<>();
		int count = 0;
		int correctCount = 0;
		int[] state = {0,0,0};
		StringBuilder reason = new StringBuilder();
		StringBuilder dealWith = new StringBuilder();
		// 模块名称
		excptionList.add(new Object[] { "教材" });
		// 模块标题
		excptionList.add(new Object[] { "教材名称", "问题", "原因分析", "处理方式" });
		int excptionListOldSize = excptionList.size();
		for (Map<String, Object> oldMaterial : maps) {
			StringBuilder exception = new StringBuilder();
			String materialId = String.valueOf(oldMaterial.get("materid"));
			String matername = (String) oldMaterial.get("matername");
			if (StringUtil.isEmpty(matername)) {
				oldMaterial.put(SQLParameters.EXCEL_EX_HEADER, exception.append("教材名称为空。"));
				excel.add(oldMaterial);
				if (state[0] == 0){
					reason.append("找不到教材名称。");
					dealWith.append("放弃迁移。");
					state[0] = 1;
				}
				continue;
			}
			String mailaddress = (String) oldMaterial.get("mailaddress");
			if (StringUtil.isEmpty(mailaddress)) {
				oldMaterial.put(SQLParameters.EXCEL_EX_HEADER, exception.append("邮寄地址为空。"));
				excel.add(oldMaterial);
				if (state[1] == 0){
					reason.append("找不到邮寄地址。");
					dealWith.append("放弃迁移。");
					state[1] =1;
				}
				continue;
			}
			Long departmentId = (Long) oldMaterial.get("DepartmentId");
			if (ObjectUtil.isNull(departmentId)) {
				// oldMaterial.put(SQLParameters.EXCEL_EX_HEADER, exception.append("创建部门为空。"));
				// excel.add(oldMaterial);
				// continue;
				departmentId = 0L;
			}
			Long director = (Long) oldMaterial.get("director");
			if (ObjectUtil.isNull(director)) {
				// oldMaterial.put(SQLParameters.EXCEL_EX_HEADER, exception.append("主任为空。"));
				// excel.add(oldMaterial);
				// continue;
				director = 0L;
			}
			Long founder_id = (Long) oldMaterial.get("founder_id");
			if (ObjectUtil.isNull(founder_id)) {
				// oldMaterial.put(SQLParameters.EXCEL_EX_HEADER, exception.append("创建人为空。"));
				// excel.add(oldMaterial);
				// continue;
				founder_id = 0L;
				// 设置成wuz
				PmphUser pmphUser = pmphUserService.getPmphUser("wuz");
				if (null != pmphUser && null != pmphUser.getId()) {
					founder_id = pmphUser.getId();
				}
				if (null != pmphUser.getDepartmentId()) {
					departmentId = pmphUser.getDepartmentId();
				}
			}
			Integer round = (Integer) oldMaterial.get("round");
			if (ObjectUtil.isNull(round)) {// 没有轮次的设置默认值为0。
				// oldMaterial.put(SQLParameters.EXCEL_EX_HEADER,
				// exception.append("轮次为空,设默认值1。"));
				// excel.add(oldMaterial);
				// ------------------------------------------------------------------------------------------
				Matcher m = Pattern.compile("第(.*?)轮").matcher(matername);
				round = m.find() ? SystemMessageService.rank(m.group(1)) : 0;
				if (null == round) {
					round = 0;
					excptionList.add(new Object[] { matername, "教材没有对应的轮次", "原专家库没有轮次信息", "导入新库表,设默认值0" });
				}
			}
			Long booktypesid = (Long) oldMaterial.get("booktypesid");
			if (ObjectUtil.isNull(booktypesid)) {
				oldMaterial.put(SQLParameters.EXCEL_EX_HEADER, exception.append("架构为空，设为默认0。"));
				excel.add(oldMaterial);
				booktypesid = 0L;
				if (state[2] == 0){
					reason.append("找不到教材架构。");
					dealWith.append("设为默认值0迁入数据库。");
					state[2] = 1;
				}
				excptionList.add(new Object[] { matername, "教材没有分类", "原专家库没有分类信息", "导入新库表,设为默认0" });
			}
			Long mender_id = (Long) oldMaterial.get("mender_id");
			if (ObjectUtil.isNull(mender_id)) {
				// oldMaterial.put(SQLParameters.EXCEL_EX_HEADER,
				// exception.append("修改人为空,设置默认为创建者。"));
				// excel.add(oldMaterial);
				mender_id = founder_id;
			}
			Material material = new Material();
			material.setMaterialName(matername);
			material.setMaterialRound(round);
			material.setMaterialType(booktypesid);
			material.setDeadline((Timestamp) oldMaterial.get("showenddate"));
			material.setActualDeadline((Timestamp) oldMaterial.get("enddate"));
			material.setAgeDeadline((Timestamp) oldMaterial.get("agedeaddate"));
			material.setMailAddress(mailaddress);
			material.setDepartmentId(departmentId);
			material.setDirector(director);// director,
			material.setIsMultiBooks("1".equals(String.valueOf(oldMaterial.get("isbookmulti")))); // is_multi_books,
			material.setIsMultiPosition("1".equals(String.valueOf(oldMaterial.get("ispositionmulti"))));// is_multi_position,
			material.setIsEduExpUsed("1".equals(String.valueOf(oldMaterial.get("isuselearnexp"))));// is_edu_exp_used,
			material.setIsEduExpRequired("1".equals(String.valueOf(oldMaterial.get("isfilllearnexp"))));// is_edu_exp_required,
			material.setIsWorkExpUsed("1".equals(String.valueOf(oldMaterial.get("isuseworkexp"))));// is_work_exp_used,
			material.setIsWorkExpRequired("1".equals(String.valueOf(oldMaterial.get("isfillworkexp"))));// is_work_exp_required,
			material.setIsTeachExpUsed("1".equals(String.valueOf(oldMaterial.get("isuseteachexp"))));// is_teach_exp_used,
			material.setIsTeachExpRequired("1".equals(String.valueOf(oldMaterial.get("isfillteachexp"))));// is_teach_exp_required,
			material.setIsAcadeUsed("1".equals(String.valueOf(oldMaterial.get("isuseacadeexp"))));// is_acade_used,
			material.setIsAcadeRequired("1".equals(String.valueOf(oldMaterial.get("isfillacadeexp"))));// is_acade_required,
			material.setIsLastPositionUsed("1".equals(String.valueOf(oldMaterial.get("isusematerpartexp"))));// is_last_position_used,
			material.setIsLastPositionRequired("1".equals(String.valueOf(oldMaterial.get("isfillmaterpartexp"))));// is_last_position_required,
			material.setIsCourseUsed("1".equals(String.valueOf(oldMaterial.get("isusecountry")))
					|| "1".equals(String.valueOf(oldMaterial.get("isuseprovexce")))
					|| "1".equals(String.valueOf(oldMaterial.get("isuseschoolconstr"))));// is_course_used,
			material.setIsCourseRequired("1".equals(String.valueOf(oldMaterial.get("isfillcountry")))
					|| "1".equals(String.valueOf(oldMaterial.get("isfillprovexce")))
					|| "1".equals(String.valueOf(oldMaterial.get("isfillschoolconstr"))));// is_course_required,
			material.setIsNationalPlanUsed("1".equals(String.valueOf(oldMaterial.get("isuseeditormater"))));// is_national_plan_used,
			material.setIsNationalPlanRequired("1".equals(String.valueOf(oldMaterial.get("isfilleditormater"))));// is_national_plan_required,
			material.setIsTextbookUsed("1".equals(String.valueOf(oldMaterial.get("isusematerwrite"))));// is_textbook_writer_used,
			material.setIsTextbookRequired("1".equals(String.valueOf(oldMaterial.get("isfillmaterwrite"))));// is_textbook_writer_required,
			material.setIsPmphTextbookUsed("1".equals(String.valueOf(oldMaterial.get("isuseothermaterwrite"))));// is_other_textbook_used,
			material.setIsPmphTextbookRequired("1".equals(String.valueOf(oldMaterial.get("isfillothermaterwrite"))));// is_other_textbook_required,
			material.setIsResearchUsed("1".equals(String.valueOf(oldMaterial.get("isusescientresearch"))));// is_research_used,
			material.setIsResearchRequired("1".equals(String.valueOf(oldMaterial.get("isfillscientresearch"))));// is_research_required,
			material.setIsPublished("1".equals(String.valueOf(oldMaterial.get("is_published"))));// is_published,
			material.setIsPublic(true);// is_public
			material.setIsAllTextbookPublished(
					"1".equals(String.valueOf(oldMaterial.get("is_all_textbook_published"))));// is_all_textbook_published
			material.setIsForceEnd(false);// is_force_end
			material.setIsDeleted("1".equals(String.valueOf(oldMaterial.get("isdelete"))));// is_deleted,
			material.setGmtCreate((Timestamp) oldMaterial.get("createdate"));// gmt_create,
			material.setFounderId(founder_id);// founder_id,
			material.setGmtUpdate((Timestamp) oldMaterial.get("updatedate"));// gmt_update,
			// 修改者
			Long mender_Id = (Long) oldMaterial.get("mender_id");
			mender_Id = mender_Id == null ? material.getFounderId() : mender_Id;
			material.setMenderId(mender_Id);// mender_id
			// 主编学术专著情况
			material.setIsMonographUsed(false);
			// 主编学术专著情况必填
			material.setIsMonographRequired(false);
			// 出版行业获奖情况
			material.setIsPublishRewardUsed(false);
			// 出版行业获奖情况必填
			material.setIsPublishRewardRequired(false);
			// SCI论文投稿及影响因子
			material.setIsSciUsed(false);
			// SCI论文投稿及影响因子必填
			material.setIsSciRequired(false);
			// 临床医学获奖情况
			material.setIsClinicalRewardUsed(false);
			// 临床医学获奖情况必填
			material.setIsClinicalRewardRequired(false);
			// 学术荣誉授予情况
			material.setIsAcadeRewardUsed(false);
			// 学术荣誉授予情况必填
			material.setIsAcadeRewardRequired(false);
			// 设置权限
			material.setProjectPermission(Integer.valueOf("11110011", 2));
			material.setPlanPermission(   Integer.valueOf("00010000", 2));
			/***
			 * 把教材放在教材分类下面
			 */
			material.setMaterialType(1L);
			material = materialService.addMaterial(material);
			count++;
			long pk = material.getId();
			JdbcHelper.updateNewPrimaryKey(tableName, pk, "materid", materialId);// 更新旧表中new_pk字段
			if (null == oldMaterial.get("exception")){
				correctCount++;
			}
			//保存教材，为下面保存教材社区做准备
			materials.add(material);
		}
		// 没有错误数据
		if (excptionList.size() == excptionListOldSize) {
			excptionList.remove(excptionList.size() - 1);
			excptionList.remove(excptionList.size() - 1);
		} else {
			// 插入一个空行
			excptionList.add(new String[] { "" });
		}
		if (excel.size() > 0) {
			try {
				excelHelper.exportFromMaps(excel, "教材信息表", "material");
			} catch (IOException ex) {
				logger.error("异常数据导出到Excel失败", ex);
			}
		}
		if (correctCount != maps.size()){
			result.put(SQLParameters.EXCEL_HEADER_TABLENAME, "material");
        	result.put(SQLParameters.EXCEL_HEADER_DESCRIPTION, "教材信息表");
        	result.put(SQLParameters.EXCEL_HEADER_SUM_DATA, maps.size());
        	result.put(SQLParameters.EXCEL_HEADER_MIGRATED_DATA, count);
        	result.put(SQLParameters.EXCEL_HEADER_CORECT_DATA, correctCount);
        	result.put(SQLParameters.EXCEL_HEADER_TRANSFERED_DATA, count - correctCount);
        	result.put(SQLParameters.EXCEL_HEADER_NO_MIGRATED_DATA, maps.size() - count);
        	result.put(SQLParameters.EXCEL_HEADER_EXCEPTION_REASON, reason.toString());
        	result.put(SQLParameters.EXCEL_HEADER_DEAL_WITH, dealWith.toString());
        	SQLParameters.STATISTICS_RESULT.add(result);
		}
		logger.info("'{}'表迁移完成，异常条目数量：{}", tableName, excel.size());
		logger.info("原数据库中共有{}条数据，迁移了{}条数据", maps.size(), count);
		// 记录信息
		Map<String, Object> msg = new HashMap<String, Object>();
		msg.put("result", "" + tableName + " 表迁移完成" + count + "/" + maps.size());
		SQLParameters.STATISTICS.add(msg);
	}

	protected void materialExtension() {
		String tableName = "teach_material_extend";
		String sql = "select " + "a.expendid, " + "b.new_pk materid, " + "a.expendname, " + "a.isfill from  "
				+ "teach_material_extend a  " + "LEFT JOIN teach_material b on b.materid=a.materid  "
				+ "where b.materid is not null ";
		JdbcHelper.addColumn(tableName); // 增加new_pk字段
		List<Map<String, Object>> maps = JdbcHelper.getJdbcTemplate().queryForList(sql);
		List<Map<String, Object>> excel = new LinkedList<>();
		Map<String, Object> result = new LinkedHashMap<>();
		int count = 0;
		int correctCount = 0;//统计正常数据的数量
		int[] state= {0,0};//判断该数据是否有相应异常情况的标识
		StringBuilder reason = new StringBuilder();
		StringBuilder dealWith = new StringBuilder();
		// 模块名称
		excptionList.add(new Object[] { "教材扩展项" });
		// 模块标题
		excptionList.add(new Object[] { "教材名称", "扩展项名称", "问题", "原因分析", "处理方式" });
		int excptionListOldSize = excptionList.size();
		for (Map<String, Object> materialExtension : maps) {
			String oldExpendid = (String) materialExtension.get("expendid");
			StringBuilder exception = new StringBuilder();
			Long materid = (Long) materialExtension.get("materid");
			String matername = (materid == null ? "" : materialService.getMaterialNameById(materid));
			if (ObjectUtil.isNull(materid)) {
				materialExtension.put(SQLParameters.EXCEL_EX_HEADER, exception.append("教材id为空。"));
				excel.add(materialExtension);
				excptionList.add(new Object[] { matername, (String) materialExtension.get("expendname"), "找不到对应教材",
						"新建的教材被删除", "不导入该条数据" });
				if (state[0] == 0){
					reason.append("找不到教材的唯一标识。");
					dealWith.append("放弃迁移。");
					state[0] = 1;
				}
				continue;
			}
			String expendname = (String) materialExtension.get("expendname");
			if (StringUtil.isEmpty(expendname)) {
				materialExtension.put(SQLParameters.EXCEL_EX_HEADER, exception.append("扩展名称为空。设定默认值\"-\""));
				excel.add(materialExtension);
				expendname = "-";
				excptionList.add(new Object[] { matername, expendname, "扩展项名称为空", "专家平台为空", "设置为'-'导入新库表" });
				if (state[1] == 0){
					reason.append("找不到教材的扩展项名称。");
					dealWith.append("设为默认值迁入数据库。");
					state[1] = 1;
				}
			}
			MaterialExtension newMaterialExtension = new MaterialExtension();
			newMaterialExtension.setMaterialId(materid);
			newMaterialExtension.setExtensionName(expendname);
			newMaterialExtension.setIsRequired("1".equals(String.valueOf(materialExtension.get("isfill"))));
			newMaterialExtension = materialExtensionService.addMaterialExtension(newMaterialExtension);
			count++;
			long pk = newMaterialExtension.getId();
			JdbcHelper.updateNewPrimaryKey(tableName, pk, "expendid", oldExpendid);// 更新旧表中new_pk字段
			if (null == materialExtension.get("exception")){
				correctCount++;
			}
		}
		// 没有错误数据
		if (excptionList.size() == excptionListOldSize) {
			excptionList.remove(excptionList.size() - 1);
			excptionList.remove(excptionList.size() - 1);
		} else {
			// 插入一个空行
			excptionList.add(new String[] { "" });
		}
		if (excel.size() > 0) {
			try {
				excelHelper.exportFromMaps(excel, "教材信息扩展项表", "material_extension");
			} catch (IOException ex) {
				logger.error("异常数据导出到Excel失败", ex);
			}
		}
		if (correctCount != maps.size()){
			result.put(SQLParameters.EXCEL_HEADER_TABLENAME, "material_extension");
        	result.put(SQLParameters.EXCEL_HEADER_DESCRIPTION, "教材信息扩展项表");
        	result.put(SQLParameters.EXCEL_HEADER_SUM_DATA, maps.size());
        	result.put(SQLParameters.EXCEL_HEADER_MIGRATED_DATA, count);
        	result.put(SQLParameters.EXCEL_HEADER_CORECT_DATA, correctCount);
        	result.put(SQLParameters.EXCEL_HEADER_TRANSFERED_DATA, count - correctCount);
        	result.put(SQLParameters.EXCEL_HEADER_NO_MIGRATED_DATA, maps.size() - count);
        	result.put(SQLParameters.EXCEL_HEADER_EXCEPTION_REASON, reason.toString());
        	result.put(SQLParameters.EXCEL_HEADER_DEAL_WITH, dealWith.toString());
        	SQLParameters.STATISTICS_RESULT.add(result);
		}
		logger.info("'{}'表迁移完成，异常条目数量：{}", tableName, excel.size());
		logger.info("原数据库中共有{}条数据，迁移了{}条数据", maps.size(), count);
		// 记录信息
		Map<String, Object> msg = new HashMap<String, Object>();
		msg.put("result", "" + tableName + " 表迁移完成" + count + "/" + maps.size());
		SQLParameters.STATISTICS.add(msg);
	}

	protected void materialExtra() {
		String sql = "select " + "new_pk, " + "introduction, " + "remark " + "from teach_material  ";
		// 获取到所有数据表
		List<Map<String, Object>> materialExtraList = JdbcHelper.getJdbcTemplate().queryForList(sql);
		int count = 0;
		List<Map<String, Object>> excel = new LinkedList<>();
		List<Long> materids = new ArrayList<>();
		Map<String, Object> result = new LinkedHashMap<>();
		int correctCount = 0;//统计正常数据的数量
		int[] state = {0,0,0,0};//判断该数据是否有相应异常状况的标识;
		StringBuilder reason = new StringBuilder();
		StringBuilder dealWith = new StringBuilder();
		// 模块名称
		excptionList.add(new Object[] { "教材通知备注" });
		// 模块标题
		excptionList.add(new Object[] { "教材名称", "问题", "原因分析", "处理方式" });
		int excptionListOldSize = excptionList.size();
		for (Map<String, Object> object : materialExtraList) {
			StringBuilder exception = new StringBuilder();
			Long materid = (Long) object.get("new_pk");
			String matername = (materid == null ? "" : materialService.getMaterialNameById(materid));
			if (ObjectUtil.isNull(materid)) {
				object.put(SQLParameters.EXCEL_EX_HEADER, exception.append("教材id为空。"));
				excel.add(object);
				excptionList.add(new Object[] { matername, "找不到对应的教材", "新建的教材已删除", "不导入该条数据" });
				if (state[0] == 0){
					reason.append("找不到教材的唯一标识。");
					dealWith.append("放弃迁移。");
					state[0] = 1;
				}
				continue;
			}
			if (materids.contains(materid)) {
				object.put(SQLParameters.EXCEL_EX_HEADER, exception.append("教材id重复。"));
				excel.add(object);
				if (state[1] == 0){
					reason.append("教材唯一标识重复。");
					dealWith.append("放弃迁移。");
					state[1] = 1;
				}
				continue;
			}
			String notice = (String) object.get("introduction");
			if (StringUtil.isEmpty(notice)) {
				object.put(SQLParameters.EXCEL_EX_HEADER, exception.append("通知内容为空。"));
				excel.add(object);
				notice = "-";
				excptionList.add(new Object[] { matername, "教材通知内容为空", "原专家平台或者在运平台没有填写通知内容", "设置为'-'导入新库表" });
				if (state[2] == 0){
					reason.append("找不到教材的通知内容。");
					dealWith.append("设为默认值迁入数据库。");
					state[2] = 1;
				}
			}
			String note = (String) object.get("remark");
			if (StringUtil.isEmpty(note)) {
				object.put(SQLParameters.EXCEL_EX_HEADER, exception.append("备注。"));
				excel.add(object);
				if (state[3] == 0){
					reason.append("找不到教材的备注内容。");
					dealWith.append("备注可以为空，照常迁入数据路。");
					state[3] = 1;
				}
				// note="-";
				// excptionList.add(new
				// Object[]{matername,"教材备注内容为空","原专家平台或者在运平台没有填写通知备注","设置为'-'导入新库表"});
			}
			MaterialExtra materialExtra = new MaterialExtra();
			materialExtra.setMaterialId(materid);
			materialExtra.setNotice(notice);
			materialExtra.setNote(note);
			materialExtra = materialExtraService.addMaterialExtra(materialExtra);
			count++;
			mps.put(materialExtra.getMaterialId(), materialExtra.getId());
			if (null == object.get("exception")){
				correctCount++;
			}
		}
		// 没有错误数据
		if (excptionList.size() == excptionListOldSize) {
			excptionList.remove(excptionList.size() - 1);
			excptionList.remove(excptionList.size() - 1);
		} else {
			// 插入一个空行
			excptionList.add(new String[] { "" });
		}
		if (excel.size() > 0) {
			try {
				excelHelper.exportFromMaps(excel, "教材通知备注表", "material_extra");
			} catch (IOException ex) {
				logger.error("异常数据导出到Excel失败", ex);
			}
		}
		if (correctCount != materialExtraList.size()){
			result.put(SQLParameters.EXCEL_HEADER_TABLENAME, "material_extra");
        	result.put(SQLParameters.EXCEL_HEADER_DESCRIPTION, "教材通知备注表");
        	result.put(SQLParameters.EXCEL_HEADER_SUM_DATA, materialExtraList.size());
        	result.put(SQLParameters.EXCEL_HEADER_MIGRATED_DATA, count);
        	result.put(SQLParameters.EXCEL_HEADER_CORECT_DATA, correctCount);
        	result.put(SQLParameters.EXCEL_HEADER_TRANSFERED_DATA, count - correctCount);
        	result.put(SQLParameters.EXCEL_HEADER_NO_MIGRATED_DATA, materialExtraList.size() - count);
        	result.put(SQLParameters.EXCEL_HEADER_EXCEPTION_REASON, reason.toString());
        	result.put(SQLParameters.EXCEL_HEADER_DEAL_WITH, dealWith.toString());
        	SQLParameters.STATISTICS_RESULT.add(result);
		}
		logger.info("'{}'表迁移完成，异常条目数量：{}", "material_extra", count);
		// 记录信息
		Map<String, Object> msg = new HashMap<String, Object>();
		msg.put("result", "material_extra 表迁移完成" + count + "/" + materialExtraList.size());
		SQLParameters.STATISTICS.add(msg);
	}

	protected void transferMaterialNoticeAttachment() {
		String sql = "select " + "a.new_pk, " + "b.filedir, " + "b.filename, " + "1 " + "from teach_material a "
				+ "LEFT JOIN pub_addfileinfo b on b.tablekeyid = a.materid "
				+ "where   b.childsystemname='notice_introduction' and tablename='TEACH_MATERIAL_INTRODUCTION' ";
		List<Map<String, Object>> materialNoticeAttachmentList = JdbcHelper.getJdbcTemplate().queryForList(sql);
		List<Map<String, Object>> excel = new LinkedList<>();
		Map<String, Object> result = new LinkedHashMap<>();
		int count = 0;
		int correctCount = 0;//统计正常数据的数量
		int[] state= {0,0,0,0};//判断该数据是否有相应异常状况的标识
		StringBuilder reason = new StringBuilder();
		StringBuilder dealWith = new StringBuilder();
		// 模块名称
		excptionList.add(new Object[] { "教材通知附件" });
		// 模块标题
		excptionList.add(new Object[] { "所属教材名称", "通知附件名称", "问题", "原因分析", "处理方式" });
		int excptionListOldSize = excptionList.size();
		for (Map<String, Object> map : materialNoticeAttachmentList) {
			StringBuilder exception = new StringBuilder();
			Long newMaterid = (Long) map.get("new_pk");
			String matername = (newMaterid == null ? "" : materialService.getMaterialNameById(newMaterid));
			if (ObjectUtil.isNull(newMaterid)) {
				map.put(SQLParameters.EXCEL_EX_HEADER, exception.append("教材id为空。"));
				excel.add(map);
				excptionList.add(
						new Object[] { matername, (String) map.get("filename"), "找不到对应教材", "新建的教材被删除", "不导入该条信息" });
				if (state[0] == 0){
					reason.append("找不到教材的唯一标识。");
					dealWith.append("放弃迁移。");
					state[0] = 1;
				}
				continue;
			}

			// 文件名
			String fileName = (String) map.get("filename");
			if (StringUtil.isEmpty(fileName)) {
				map.put(SQLParameters.EXCEL_EX_HEADER, exception.append("文件名称为空。"));
				excel.add(map);
				fileName = "-";
				excptionList.add(new Object[] { matername, fileName, "文件名称为空", "", "设置为'-'导入新库表" });
				if (state[1] == 0){
					reason.append("找不到相应文件名称。");
					dealWith.append("设为默认值迁入数据库。");
					state[1] = 1;
				}
			}

			Long materialExtraId = mps.get(newMaterid);
			if (ObjectUtil.isNull(materialExtraId)) {
				map.put(SQLParameters.EXCEL_EX_HEADER, exception.append("教材通知备注id。"));
				excptionList.add(new Object[] { matername, fileName, "找不到关联的通知", "新建的教材被删除", "不导入该条信息" });
				excel.add(map);
				if (state[2] == 0){
					reason.append("找不到教材通知备注的唯一标识。");
					dealWith.append("放弃迁移。");
					state[2] = 1;
				}
				continue;
			}
			MaterialNoticeAttachment materialNoticeAttachment = new MaterialNoticeAttachment();
			materialNoticeAttachment.setMaterialExtraId(materialExtraId);
			// 先用一个临时的"-"占位，不然会报错;
			materialNoticeAttachment.setAttachment("-");
			materialNoticeAttachment.setAttachmentName(fileName);
			materialNoticeAttachment.setDownload(1L);
			materialNoticeAttachment = materialNoticeAttachmentService
					.addMaterialNoticeAttachment(materialNoticeAttachment);
			if (ObjectUtil.notNull(materialNoticeAttachment.getId())) {
				String mongoId = null;
				try {
					mongoId = fileService.migrateFile((String) map.get("filedir"), FileType.MATERIAL_NOTICE_ATTACHMENT,
							materialNoticeAttachment.getId());
				} catch (IOException ex) {
					logger.error("文件读取异常，路径<{}>，异常信息：{}", (String) map.get("filedir"), ex.getMessage());
					map.put(SQLParameters.EXCEL_EX_HEADER, "文件读取异常。");
					excel.add(map);
					if (state[3] == 0){
						reason.append("找不到通知文件。");
						dealWith.append("放弃迁移。");
						state[3] = 1;
					}
				} 
				if (null != mongoId) {
					materialNoticeAttachment.setAttachment(mongoId);
					materialNoticeAttachmentService.updateMaterialNoticeAttachment(materialNoticeAttachment);
					count++;
				} else {
					materialNoticeAttachmentService
							.deleteMaterialNoticeAttachmentById(materialNoticeAttachment.getId());
					excptionList.add(new Object[] { matername, fileName, "找不到通知文件", "文件丢失", "不导入该条信息" });
				}
			}
			if (null == map.get("exception")){
				correctCount++;
			}
		}
		// 没有错误数据
		if (excptionList.size() == excptionListOldSize) {
			excptionList.remove(excptionList.size() - 1);
			excptionList.remove(excptionList.size() - 1);
		} else {
			// 插入一个空行
			excptionList.add(new String[] { "" });
		}
		if (excel.size() > 0) {
			try {
				excelHelper.exportFromMaps(excel, "教材通知附件表", "material_notice_attachment");
			} catch (IOException ex) {
				logger.error("异常数据导出到Excel失败", ex);
			}
		}
		if (correctCount != materialNoticeAttachmentList.size()){
			result.put(SQLParameters.EXCEL_HEADER_TABLENAME, "material_notice_attachment");
        	result.put(SQLParameters.EXCEL_HEADER_DESCRIPTION, "教材通知附件表");
        	result.put(SQLParameters.EXCEL_HEADER_SUM_DATA, materialNoticeAttachmentList.size());
        	result.put(SQLParameters.EXCEL_HEADER_MIGRATED_DATA, count);
        	result.put(SQLParameters.EXCEL_HEADER_CORECT_DATA, correctCount);
        	result.put(SQLParameters.EXCEL_HEADER_TRANSFERED_DATA, count - correctCount);
        	result.put(SQLParameters.EXCEL_HEADER_NO_MIGRATED_DATA, materialNoticeAttachmentList.size() - count);
        	result.put(SQLParameters.EXCEL_HEADER_EXCEPTION_REASON, reason.toString());
        	result.put(SQLParameters.EXCEL_HEADER_DEAL_WITH, dealWith.toString());
        	SQLParameters.STATISTICS_RESULT.add(result);
		}
		logger.info("教材通知附件表迁移了{}条数据", count);
		// 记录信息
		Map<String, Object> msg = new HashMap<String, Object>();
		msg.put("result", "教材通知附件  表迁移完成" + count + "/" + materialNoticeAttachmentList.size());
		SQLParameters.STATISTICS.add(msg);
	}

	protected void transferMaterialNoteAttachment() {
		String sql = "select " + "a.new_pk materid, " + "b.filedir, " + "b.filename " + "from teach_material a  "
				+ "LEFT JOIN pub_addfileinfo b on b.tablekeyid = a.materid "
				+ "where b.childsystemname='notice' and tablename='TEACH_MATERIAL' ";
		List<Map<String, Object>> materialMaterialNoteAttachmentList = JdbcHelper.getJdbcTemplate().queryForList(sql);
		List<Map<String, Object>> excel = new LinkedList<>();
		Map<String, Object> result = new LinkedHashMap<>();
		int count = 0;
		int correctCount = 0;
		int[] state = {0,0,0,0};
		StringBuilder reason = new StringBuilder();
		StringBuilder dealWith = new StringBuilder();
		// 模块名称
		excptionList.add(new Object[] { "教材备注附件" });
		// 模块标题
		excptionList.add(new Object[] { "所属教材名称", "备注附件名称", "问题", "原因分析", "处理方式" });
		int excptionListOldSize = excptionList.size();
		for (Map<String, Object> map : materialMaterialNoteAttachmentList) {
			StringBuilder exception = new StringBuilder();
			Long newMaterid = (Long) map.get("materid");
			String matername = (newMaterid == null ? "" : materialService.getMaterialNameById(newMaterid));
			if (ObjectUtil.isNull(newMaterid)) {
				map.put(SQLParameters.EXCEL_EX_HEADER, exception.append("教材id为空。"));
				excel.add(map);
				excptionList.add(
						new Object[] { matername, (String) map.get("filename"), "找不到对应教材", "新建的教材被删除", "不导入该条信息" });
				if (state[0] == 0){
					reason.append("找不到教材的唯一标识。");
					dealWith.append("放弃迁移。");
					state[0] = 1;
				}
				continue;
			}

			// 文件名
			String fileName = (String) map.get("filename");
			if (StringUtil.isEmpty(fileName)) {
				fileName = "-";
				excptionList.add(new Object[] { matername, fileName, "文件名称为空", "", "设置为'-'导入新库表" });
				map.put(SQLParameters.EXCEL_EX_HEADER, exception.append("文件名称为空。设置为'-'导入新库表"));
				if (state[1] == 0){
					reason.append("找不到相关的文件名称。");
					dealWith.append("设为默认值迁入数据库。");
					state[1] = 1;
				}
				excel.add(map);
			}

			Long materialExtraId = mps.get(newMaterid);
			if (ObjectUtil.isNull(materialExtraId)) {
				map.put(SQLParameters.EXCEL_EX_HEADER, exception.append("找不到相关的通知内容。"));
				excptionList.add(new Object[] { matername, fileName, "找不到关联的通知", "新建的教材被删除", "不导入该条信息" });
				excel.add(map);
				if (state[2] == 0){
					reason.append("找不到相关的通知内容。");
					dealWith.append("放弃迁移。");
					state[2] = 1;
				}
				continue;
			}
			MaterialNoteAttachment materialNoteAttachment = new MaterialNoteAttachment();
			materialNoteAttachment.setMaterialExtraId(materialExtraId);
			// 先用一个临时的"-"占位，不然会报错;
			materialNoteAttachment.setAttachment("-");
			materialNoteAttachment.setAttachmentName(fileName);
			materialNoteAttachment.setDownload(1L);
			materialNoteAttachment = materialNoteAttachmentService.addMaterialNoteAttachment(materialNoteAttachment);
			count++;
			if (ObjectUtil.notNull(materialNoteAttachment.getId())) {
				String mongoId = null;
				try {
					mongoId = fileService.migrateFile((String) map.get("filedir"), FileType.MATERIAL_NOTE_ATTACHMENT,
							materialNoteAttachment.getId());
				} catch (IOException ex) {
					logger.error("文件读取异常，路径<{}>，异常信息：{}", (String) map.get("filedir"), ex.getMessage());
					map.put(SQLParameters.EXCEL_EX_HEADER, "文件读取异常。");
					excel.add(map);
					if (state[3] == 0){
						reason.append("找不到相关的备注文件。");
						dealWith.append("放弃迁移。");
						state[3] = 1;
					}
				}
				if (null != mongoId) {
					materialNoteAttachment.setAttachment(mongoId);
					materialNoteAttachmentService.updateMaterialNoteAttachment(materialNoteAttachment);
				} else {
					materialNoteAttachmentService.deleteMaterialNoteAttachmentById(materialNoteAttachment.getId());
					excptionList.add(new Object[] { matername, fileName, "找不到备注文件", "文件丢失", "不导入该条信息" });
				}
			}
			if (null == map.get("exception")){
				correctCount++;
			}
		}
		// 没有错误数据
		if (excptionList.size() == excptionListOldSize) {
			excptionList.remove(excptionList.size() - 1);
			excptionList.remove(excptionList.size() - 1);
		} else {
			// 插入一个空行
			excptionList.add(new String[] { "" });
		}
		if (excel.size() > 0) {
			try {
				excelHelper.exportFromMaps(excel, "教材备注附件表", "material_note_attachment");
			} catch (IOException ex) {
				logger.error("异常数据导出到Excel失败", ex);
			}
		}
		if (correctCount != materialMaterialNoteAttachmentList.size()){
			result.put(SQLParameters.EXCEL_HEADER_TABLENAME, "material_note_attachment");
        	result.put(SQLParameters.EXCEL_HEADER_DESCRIPTION, "教材备注附件表");
        	result.put(SQLParameters.EXCEL_HEADER_SUM_DATA, materialMaterialNoteAttachmentList.size());
        	result.put(SQLParameters.EXCEL_HEADER_MIGRATED_DATA, count);
        	result.put(SQLParameters.EXCEL_HEADER_CORECT_DATA, correctCount);
        	result.put(SQLParameters.EXCEL_HEADER_TRANSFERED_DATA, count - correctCount);
        	result.put(SQLParameters.EXCEL_HEADER_NO_MIGRATED_DATA, materialMaterialNoteAttachmentList.size() - count);
        	result.put(SQLParameters.EXCEL_HEADER_EXCEPTION_REASON, reason.toString());
        	result.put(SQLParameters.EXCEL_HEADER_DEAL_WITH, dealWith.toString());
        	SQLParameters.STATISTICS_RESULT.add(result);
		}
		logger.info("教材通知附件表迁移了{}条数据", count);
		// 记录信息
		Map<String, Object> msg = new HashMap<String, Object>();
		msg.put("result", "教材备注附件表  表迁移完成" + count + "/" + materialMaterialNoteAttachmentList.size());
		SQLParameters.STATISTICS.add(msg);
	}

	protected void transferMaterialContact() {
		String sql = "select " + "a.linkerid linkerid, " + "b.new_pk   materid, " + "c.new_pk   userid, "
				+ "c.username username, " + "a.linkphone linkphone, " + "a.email    email, " + "a.orderno  orderno "
				+ "from teach_material_linker  a  " + "LEFT JOIN teach_material  b on b.materid = a.materid "
				+ "LEFT JOIN sys_user  c on c.userid = a.userid " + "where 1=1 ";
		// 获取到所有数据表
		String tableName = "teach_material_linker";
		JdbcHelper.addColumn(tableName); // 增加new_pk字段
		List<Map<String, Object>> materialContactList = JdbcHelper.getJdbcTemplate().queryForList(sql);
		List<Map<String, Object>> excel = new LinkedList<>();
		Map<String, Object> result = new LinkedHashMap<>();
		int count = 0;
		int correctCount = 0;
		int[] state = {0,0,0,0,0,0};
		StringBuilder reason = new StringBuilder();
		StringBuilder dealWith = new StringBuilder();
		// 模块名称
		excptionList.add(new Object[] { "教材联系人" });
		// 模块标题
		excptionList.add(new Object[] { "所属教材名称", "问题", "原因分析", "处理方式" });
		int excptionListOldSize = excptionList.size();
		for (Map<String, Object> object : materialContactList) {
			String linkId = (String) object.get("linkerid");
			StringBuilder exception = new StringBuilder();
			Long materid = (Long) object.get("materid");
			String matername = (materid == null ? "" : materialService.getMaterialNameById(materid));
			if (ObjectUtil.isNull(materid)) {
				object.put(SQLParameters.EXCEL_EX_HEADER, exception.append("教材id为空。"));
				excel.add(object);
				excptionList.add(new Object[] { matername, "找不到对应的教材", "新建的教材被删除", "不导入该条数据" });
				if (state[0] == 0){
					reason.append("找不到教材的唯一标识。");
					dealWith.append("放弃迁移。");
					state[0] = 1;
				}
				continue;
			}
			Long userid = (Long) object.get("userid");
			if (ObjectUtil.isNull(userid)) {
				object.put(SQLParameters.EXCEL_EX_HEADER, exception.append("联系人id为空。"));
				excel.add(object);
				excptionList.add(new Object[] { matername, "找不到对应的联系人", " ", "不导入该条数据" });
				if (state[1] == 0){
					reason.append("找不到教材的联系人的唯一标识。");
					dealWith.append("放弃迁移。");
					state[1] = 1;
				}
				continue;
			}
			String username = (String) object.get("username");
			if (StringUtil.isEmpty(username)) {
				object.put(SQLParameters.EXCEL_EX_HEADER, exception.append("联系人姓名为空。"));
				excel.add(object);
				excptionList.add(new Object[] { matername, "联系人姓名为空", "用户没有填写姓名", "不导入该条数据" });
				if (state[2] == 0){
					reason.append("找不到教材的联系人姓名。");
					dealWith.append("放弃迁移。");
					state[2] = 1;
				}
				continue;
			}
			String linkphone = (String) object.get("linkphone");
			if (StringUtil.isEmpty(linkphone)) {
				object.put(SQLParameters.EXCEL_EX_HEADER, exception.append("联系人电话为空。"));
				excel.add(object);
				excptionList.add(new Object[] { matername, "联系人电话为空", "源数据没有填写电话", "不导入该条数据" });
				if (state[3] == 0){
					reason.append("找不到联系人的电话。");
					dealWith.append("放弃迁移。");
					state[3] = 1;
				}
				continue;
			}
			String email = (String) object.get("email");
			if (StringUtil.isEmpty(email)) {
				object.put(SQLParameters.EXCEL_EX_HEADER, exception.append("联系人邮箱为空。"));
				excel.add(object);
				excptionList.add(new Object[] { matername, "联系人邮箱为空", "源数据没有填写邮箱", "不导入该条数据" });
				if (state[4] == 0){
					reason.append("找不到联系人的邮箱。");
					dealWith.append("放弃迁移。");
					state[4] = 1;
				}
				continue;
			}
			Integer orderno = null;
			orderno = (Integer) object.get("orderno");
			if (ObjectUtil.notNull(orderno) && orderno < 0) {
				object.put(SQLParameters.EXCEL_EX_HEADER, exception.append("联系人排序不符合规范,默认999。"));
				orderno = 999;
				excel.add(object);
				if (state[5] == 0){
					reason.append("联系人排序码为负数。");
					dealWith.append("设为默认值迁入数据库。");
					state[5] = 1;
				}
			}
			MaterialContact materialContact = new MaterialContact();
			materialContact.setMaterialId(materid);
			materialContact.setContactUserId(userid);
			materialContact.setContactUserName(username);
			materialContact.setContactPhone(linkphone);
			materialContact.setContactEmail(email);
			materialContact.setSort(orderno);
			materialContact = materialContactService.addMaterialContact(materialContact);
			count++;
			long pk = materialContact.getId();
			if (ObjectUtil.notNull(pk)) {
				JdbcHelper.updateNewPrimaryKey(tableName, pk, "linkerid", linkId);// 更新旧表中new_pk字段
			}
			if (null == object.get("exception")){
				correctCount++;
			}
		}
		// 没有错误数据
		if (excptionList.size() == excptionListOldSize) {
			excptionList.remove(excptionList.size() - 1);
			excptionList.remove(excptionList.size() - 1);
		} else {
			// 插入一个空行
			excptionList.add(new String[] { "" });
		}
		if (excel.size() > 0) {
			try {
				excelHelper.exportFromMaps(excel, "教材联系人表（一对多）", "material_contact");
			} catch (IOException ex) {
				logger.error("异常数据导出到Excel失败", ex);
			}
		}
		if (correctCount != materialContactList.size()){
			result.put(SQLParameters.EXCEL_HEADER_TABLENAME, "material_contact");
        	result.put(SQLParameters.EXCEL_HEADER_DESCRIPTION, "教材联系人表（一对多）");
        	result.put(SQLParameters.EXCEL_HEADER_SUM_DATA, materialContactList.size());
        	result.put(SQLParameters.EXCEL_HEADER_MIGRATED_DATA, count);
        	result.put(SQLParameters.EXCEL_HEADER_CORECT_DATA, correctCount);
        	result.put(SQLParameters.EXCEL_HEADER_TRANSFERED_DATA, count - correctCount);
        	result.put(SQLParameters.EXCEL_HEADER_NO_MIGRATED_DATA, materialContactList.size() - count);
        	result.put(SQLParameters.EXCEL_HEADER_EXCEPTION_REASON, reason.toString());
        	result.put(SQLParameters.EXCEL_HEADER_DEAL_WITH, dealWith.toString());
        	SQLParameters.STATISTICS_RESULT.add(result);
		}
		logger.info("'{}'表迁移完成，异常条目数量：{}", tableName, excel.size());
		logger.info("原数据库中共有{}条数据，迁移了{}条数据", materialContactList.size(), count);
		// 记录信息
		Map<String, Object> msg = new HashMap<String, Object>();
		msg.put("result", "" + tableName + "  表迁移完成" + count + "/" + materialContactList.size());
		SQLParameters.STATISTICS.add(msg);
	}

	protected void materialOrg() {
		String sql = "select * from ( " + "select   " + "a.pushschoolid , " + "b.new_pk materid, " + "c.new_pk orgid, "
				+ "a.orgid  oldorgid " + "from teach_pushschool  a  "
				+ "LEFT JOIN teach_material  b on b.materid = a.materid "
				+ "LEFT JOIN ba_organize c on c.orgid = a.orgid " + "where 1=1 " + ") temp  ";
		// 获取到所有数据表
		String tableName = "teach_pushschool";
		JdbcHelper.addColumn(tableName); // 增加new_pk字段
		List<Map<String, Object>> pubList = JdbcHelper.getJdbcTemplate()
				.queryForList(sql + " where materid is not null  GROUP BY CONCAT(materid,'_',orgid) UNION  " + sql
						+ " where materid is  null ");
		List<Map<String, Object>> excel = new LinkedList<>();
		Map<String, Object> result = new LinkedHashMap<>();
		int count = 0;
		int correctCount = 0;
		int[] state = {0,0};
		StringBuilder reason = new StringBuilder();
		StringBuilder dealWith = new StringBuilder();
		// 模块名称
		excptionList.add(new Object[] { "教材发布学校" });
		// 模块标题
		excptionList.add(new Object[] { "机构名称", "问题", "原因分析", "处理方式" });
		int excptionListOldSize = excptionList.size();
		for (Map<String, Object> object : pubList) {
			String pushschoolId = (String) object.get("pushschoolid");
			StringBuilder exception = new StringBuilder();
			Long materid = (Long) object.get("materid");
			Long orgid = (Long) object.get("orgid");
			if (ObjectUtil.isNull(materid)) {
				// 查询机构名称
				Org org = (orgid == null ? null : orgService.getOrgById(orgid));
				String orgName = (org == null ? "" : org.getOrgName());

				object.put(SQLParameters.EXCEL_EX_HEADER, exception.append("教材id为空。"));
				excptionList.add(new Object[] { orgName, "找不到对应教材", "新建的教材被删除", "不导入该条数据" });
				excel.add(object);
				if (state[0] == 0){
					reason.append("找不到对应的教材。");
					dealWith.append("放弃迁移。");
					state[0] = 1;
				}
				continue;
			}
			if (ObjectUtil.isNull(orgid)) {
				// 查询机构名称
				Org org = (orgid == null ? null : orgService.getOrgById(orgid));
				String orgName = (org == null ? "" : org.getOrgName());
				object.put(SQLParameters.EXCEL_EX_HEADER, exception.append("机构id为空。"));
				excel.add(object);
				excptionList.add(new Object[] { orgName, "找不到对应的发布学校", "", "不导入该条数据" });
				if (state[1] == 0){
					reason.append("找不到教材对应的发布学校。");
					dealWith.append("放弃迁移。");
					state[1] = 1;
				}
				continue;
			}
			MaterialOrg materialOrg = new MaterialOrg();
			materialOrg.setMaterialId(materid);
			materialOrg.setOrgId(orgid);
			materialOrg = materialOrgService.addMaterialOrg(materialOrg);
			count++;
			long pk = materialOrg.getId();
			if (ObjectUtil.notNull(pk)) {
				JdbcHelper.updateNewPrimaryKey(tableName, pk, "pushschoolid", pushschoolId);// 更新旧表中new_pk字段
			}
			if (null == object.get("exception")){
				correctCount++;
			}
		}
		// 没有错误数据
		if (excptionList.size() == excptionListOldSize) {
			excptionList.remove(excptionList.size() - 1);
			excptionList.remove(excptionList.size() - 1);
		} else {
			// 插入一个空行
			excptionList.add(new String[] { "" });
		}
		if (excel.size() > 0) {
			try {
				excelHelper.exportFromMaps(excel, "教材-机构关联表", "material_org");
			} catch (IOException ex) {
				logger.error("异常数据导出到Excel失败", ex);
			}
		}
		if (correctCount != pubList.size()){
			result.put(SQLParameters.EXCEL_HEADER_TABLENAME, "material_org");
        	result.put(SQLParameters.EXCEL_HEADER_DESCRIPTION, "教材—机构关联表");
        	result.put(SQLParameters.EXCEL_HEADER_SUM_DATA, pubList.size());
        	result.put(SQLParameters.EXCEL_HEADER_MIGRATED_DATA, count);
        	result.put(SQLParameters.EXCEL_HEADER_CORECT_DATA, correctCount);
        	result.put(SQLParameters.EXCEL_HEADER_TRANSFERED_DATA, count - correctCount);
        	result.put(SQLParameters.EXCEL_HEADER_NO_MIGRATED_DATA, pubList.size() - count);
        	result.put(SQLParameters.EXCEL_HEADER_EXCEPTION_REASON, reason.toString());
        	result.put(SQLParameters.EXCEL_HEADER_DEAL_WITH, dealWith.toString());
        	SQLParameters.STATISTICS_RESULT.add(result);
		}
		logger.info("'{}'表迁移完成，异常条目数量：{}", tableName, excel.size());
		logger.info("原数据库中共有{}条数据，迁移了{}条数据", pubList.size(), count);
		// 记录信息
		Map<String, Object> msg = new HashMap<String, Object>();
		msg.put("result", "" + tableName + "  表迁移完成" + count + "/" + pubList.size());
		SQLParameters.STATISTICS.add(msg);
	}

	protected void materialPprojectEeditor() {
		String sql = "select DISTINCT * from( " + "select " + "b.new_pk   materid, " + "c.new_pk   userid  "
				+ "from teach_material_linker  a  " + "LEFT JOIN teach_material  b on b.materid = a.materid "
				+ "LEFT JOIN sys_user  c on c.userid = a.userid " + "where true  "
				+ "UNION select DISTINCT e.new_pk materid ,a.new_pk userid from sys_user  a  "
				+ "LEFT JOIN  sys_userorganize b on b.userid=a.userid  "
				+ "LEFT JOIN  sys_userrole   c on   c.userid =a.userid   "
				+ "lEFT JOIN  sys_role   d  on d.roleid =c.roleid    "
				+ "LEFT JOIN teach_material e on e.createorgid = b.orgid   "
				+ "where d.rolename like '%主任%' and e.materid is not null   GROUP BY e.materid " + ")temp ";
		List<Map<String, Object>> materialProjectEditorList = JdbcHelper.getJdbcTemplate().queryForList(sql);
		List<Map<String, Object>> excel = new LinkedList<>();
		Map<String, Object> result = new LinkedHashMap<>();
		int count = 0;
		int correctCount = 0;
		int[] state = {0,0};
		StringBuilder reason = new StringBuilder();
		StringBuilder dealWith = new StringBuilder();
		for (Map<String, Object> object : materialProjectEditorList) {
			StringBuilder exception = new StringBuilder();
			Long materid = (Long) object.get("materid");
			if (ObjectUtil.isNull(materid)) {
				object.put(SQLParameters.EXCEL_EX_HEADER, exception.append("教材id为空。"));
				excel.add(object);
				if (state[0] == 0){
					reason.append("找不到对应的教材。");
					dealWith.append("放弃迁移。");
					state[0] = 1;
				}
				continue;
			}
			Long userid = (Long) object.get("userid");
			if (ObjectUtil.isNull(userid)) {
				object.put(SQLParameters.EXCEL_EX_HEADER, exception.append("用户id为空。"));
				excel.add(object);
				if (state[1] == 0){
					reason.append("找不到教材对应的项目编辑。");
					dealWith.append("放弃迁移。");
					state[1] = 1;
				}
				continue;
			}
			MaterialProjectEditor materialProjectEditor = new MaterialProjectEditor();
			materialProjectEditor.setMaterialId(materid);
			materialProjectEditor.setEditorId(userid);
			materialProjectEditor = materialProjectEditorService.addMaterialProjectEditor(materialProjectEditor);
			count++;
			if (null == object.get("exception")){
				correctCount++;
			}
		}
		if (excel.size() > 0) {
			try {
				excelHelper.exportFromMaps(excel, "教材-项目编辑关联表（多对多）", "material_project_editor");
			} catch (IOException ex) {
				logger.error("异常数据导出到Excel失败", ex);
			}
		}
		if (correctCount != materialProjectEditorList.size()){
			result.put(SQLParameters.EXCEL_HEADER_TABLENAME, "material_project_editor");
        	result.put(SQLParameters.EXCEL_HEADER_DESCRIPTION, "教材-项目编辑关联表（多对多）");
        	result.put(SQLParameters.EXCEL_HEADER_SUM_DATA, materialProjectEditorList.size());
        	result.put(SQLParameters.EXCEL_HEADER_MIGRATED_DATA, count);
        	result.put(SQLParameters.EXCEL_HEADER_CORECT_DATA, correctCount);
        	result.put(SQLParameters.EXCEL_HEADER_TRANSFERED_DATA, count - correctCount);
        	result.put(SQLParameters.EXCEL_HEADER_NO_MIGRATED_DATA, materialProjectEditorList.size() - count);
        	result.put(SQLParameters.EXCEL_HEADER_EXCEPTION_REASON, reason.toString());
        	result.put(SQLParameters.EXCEL_HEADER_DEAL_WITH, dealWith.toString());
        	SQLParameters.STATISTICS_RESULT.add(result);
		}
		logger.info("'{}'表迁移完成，异常条目数量：{}", "MaterialProjectEditor", excel.size());
		logger.info("原数据库中共有{}条数据，迁移了{}条数据", materialProjectEditorList.size(), count);
		// 记录信息
		Map<String, Object> msg = new HashMap<String, Object>();
		msg.put("result", "MaterialProjectEditor  表迁移完成" + count + "/" + materialProjectEditorList.size());
		SQLParameters.STATISTICS.add(msg);
	}
	
	protected  void  addMaterialCommunity (){
		//保存教材社区相关的信息
		for(Material  material:  materials){
			MaterialExtra materialExtra=  materialExtraService.getMaterialExtraByMaterialId(material.getId());
			if(null == materialExtra){
				materialExtra = new MaterialExtra();
			}
			String detail = "<p>简介:驱蚊器</p>"
					      + "<p><br/></p>"
					      + "<p>邮寄地址：驱蚊器翁</p>"
					      + "<p><br/></p>"
					      + "<p>联&nbsp;系&nbsp;人：陈慧 (电话：18610032992，Emali：147258369@qq.com)</p>"
					      + "<p><br/>/p>";
			detail = detail+"<p>简介:"+null == materialExtra.getNotice()?"":materialExtra.getNotice()+"</p>";
			detail = detail+"<p><br/></p><p>邮寄地址:"+material.getMailAddress()+"</p>";
			List<MaterialContact> materialContacts =materialContactService.listMaterialContactByMaterialId(material.getId()) ;
			for(MaterialContact materialContact : materialContacts){
				detail = detail+"<p><br/></p><p>联&nbsp;系&nbsp;人："+materialContact.getContactUserName()+" (电话："+materialContact.getContactPhone()+"，Emali："+materialContact.getContactEmail()+")</p>";
			}
			Content content  = new Content( detail);
			content  = contentService.add(content);
			CmsContent cmsContent  = new CmsContent();
			cmsContent.setParentId(0L);
			cmsContent.setPath("0");
			cmsContent.setMid(content.getId());
			cmsContent.setCategoryId(3L);
			cmsContent.setTitle(material.getMaterialName());
			cmsContent.setAuthorType(new Short("0"));
			cmsContent.setAuthorId(material.getFounderId());
			cmsContent.setIsPublished(material.getIsPublished());
			cmsContent.setAuthStatus(material.getIsPublished()?new Short("2"):new Short("0"));
			cmsContent.setAuthDate(DateUtil.date2Str(material.getGmtUpdate() == null ? material.getGmtCreate():material.getGmtUpdate()));
			cmsContent.setAuthUserId(material.getFounderId());
			cmsContent.setGmtCreate(material.getGmtCreate()  );
			cmsContent.setGmtUpdate(material.getGmtUpdate() == null ? material.getGmtCreate():material.getGmtUpdate());
			cmsContent.setIsMaterialEntry(true);
			cmsContent.setMaterialId(material.getId());
			cmsContentService.addCmsContent(cmsContent);
		}
		
		
	}

	/***********************************
	 * 下面是辅助方法
	 ******************************************************/

	private List<Map<TitleEnum, Object>> excptionMap = new LinkedList<Map<TitleEnum, Object>>();

	public List<Map<String, Object>> trans() {
		List<Map<String, Object>> temp = new LinkedList<Map<String, Object>>();
		for (Map<TitleEnum, Object> map : excptionMap) {
			Map<String, Object> tempMap = new HashMap<>();
			for (TitleEnum in : map.keySet()) { // map.keySet()返回的是所有key的值
				Object obj = map.get(in); // 得到每个key多对用value的值
				tempMap.put(in.getName(), obj);
			}
			temp.add(tempMap);
		}
		return temp;
	}

	public void saveProblem(Object matername, Object problem, Object dealWay) {
		Map<TitleEnum, Object> tempMap = new HashMap<TitleEnum, Object>();
		tempMap.put(TitleEnum.MATERIAL, matername);
		tempMap.put(TitleEnum.PROBLEM, problem);
		tempMap.put(TitleEnum.DEALWAY, dealWay);
		excptionMap.add(tempMap);
	}

}

enum TitleEnum {
	MATERIAL("教材名称"), PROBLEM("问题"), DEALWAY("处理方式");
	// 成员变量
	private String name;

	// 构造方法
	private TitleEnum(String name) {
		this.name = name;
	}

	// get 方法
	public String getName() {
		return name;
	}
}
