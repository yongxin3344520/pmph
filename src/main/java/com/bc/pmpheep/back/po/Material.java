package com.bc.pmpheep.back.po;

import java.sql.Timestamp;
import java.util.Date;
import org.apache.ibatis.type.Alias;

/**
 * Material  教材信息表    实体类 
 * @author mryang 
 *
 */
@SuppressWarnings("serial")
@Alias("Material")
public class Material implements java.io.Serializable {
	//主键
	private Long id;
	//教材名称
	private String materialName;
	//教材轮次
	private Integer materialRound;
	//教材类型
	private Long materialType;
	//显示报名截止日期
	private Date deadline;
	//实际报名截止日期
	private Date actualDeadline;
	//年龄计算截止日期
	private Date ageDeadline;
	//邮寄地址
	private String mailAddress;
	//当前进度
	private Short progress;
	//教材所属部门
	private Long departmentId;
	//主任id
	private Long director;
	//项目编辑id
	private Long projectEditor;
	//是否书籍多选
	private Boolean isMultiBooks;
	//是否职位多选
	private Boolean isMultiPosition;
	//学习经历
	private Boolean isEduExpUsed;
	//学习经历必填
	private Boolean isEduExpRequired;
	//工作经历
	private Boolean isWorkExpUsed;
	//工作经历必填
	private Boolean isWorkExpRequired;
	//教学经历
	private Boolean isTeachExpUsed;
	//教学经历必填
	private Boolean isTeachExpRequired;
	//主要学术兼职
	private Boolean isAcadeUsed;
	//主要学术兼职必填
	private Boolean isAcadeRequired;
	//上版教材参编情况
	private Boolean isLastPositionUsed;
	//上版教材参编情况必填
	private Boolean isLastPositionRequired;
	//国家级课程建设情况
	private Boolean isNationalCourseUsed;
	//国家级课程建设情况必填
	private Boolean isNationalCourseRequired;
	//省部级课程建设情况
	private Boolean isProvincialCourseUsed;
	//省部级课程建设情况必填
	private Boolean isProvincialCourseRequired;
	//学校课程建设情况
	private Boolean isSchoolCourseUsed;
	//学校课程建设情况必填
	private Boolean isSchoolCourseRequired;
	//主编国家规划教材情况
	private Boolean isNationalPlanUsed;
	//主编国家规划教材情况必填
	private Boolean isNationalPlanRequired;
	//教材编写情况
	private Boolean isTextbookWriterUsed;
	//教材编写情况必填
	private Boolean isTextbookWriterRequired;
	//其他教材编写情况
	private Boolean isOtherTextbookUsed;
	//其他教材编写情况必填
	private Boolean isOtherTextbookRequired;
	//科研情况
	private Boolean isResearchUsed;
	//科研情况必填
	private Boolean isResearchRequired;
	//是否已发布到前台
	private Boolean isPublished;
	//是否被逻辑删除
	private Boolean isDeleted;
	//创建时间
	private Timestamp gmtCreate;
	//创建人id
	private Long founderId;
	//修改时间
	private Timestamp gmtUpdate;
	//修改人id
	private Long menderId;

	// Constructors

	/** default constructor */
	public Material() {
	}

	/** full constructor */
	public Material(String materialName, Integer materialRound,
			Long materialType, Date deadline, Date actualDeadline,
			Date ageDeadline, String mailAddress, Short progress,
			Long departmentId, Long director, Long projectEditor,
			Boolean isMultiBooks, Boolean isMultiPosition,
			Boolean isEduExpUsed, Boolean isEduExpRequired,
			Boolean isWorkExpUsed, Boolean isWorkExpRequired,
			Boolean isTeachExpUsed, Boolean isTeachExpRequired,
			Boolean isAcadeUsed, Boolean isAcadeRequired,
			Boolean isLastPositionUsed, Boolean isLastPositionRequired,
			Boolean isNationalCourseUsed, Boolean isNationalCourseRequired,
			Boolean isProvincialCourseUsed, Boolean isProvincialCourseRequired,
			Boolean isSchoolCourseUsed, Boolean isSchoolCourseRequired,
			Boolean isNationalPlanUsed, Boolean isNationalPlanRequired,
			Boolean isTextbookWriterUsed, Boolean isTextbookWriterRequired,
			Boolean isOtherTextbookUsed, Boolean isOtherTextbookRequired,
			Boolean isResearchUsed, Boolean isResearchRequired,
			Boolean isPublished, Boolean isDeleted, Timestamp gmtCreate,
			Long founderId, Timestamp gmtUpdate, Long menderId) {
		this.materialName = materialName;
		this.materialRound = materialRound;
		this.materialType = materialType;
		this.deadline = deadline;
		this.actualDeadline = actualDeadline;
		this.ageDeadline = ageDeadline;
		this.mailAddress = mailAddress;
		this.progress = progress;
		this.departmentId = departmentId;
		this.director = director;
		this.projectEditor = projectEditor;
		this.isMultiBooks = isMultiBooks;
		this.isMultiPosition = isMultiPosition;
		this.isEduExpUsed = isEduExpUsed;
		this.isEduExpRequired = isEduExpRequired;
		this.isWorkExpUsed = isWorkExpUsed;
		this.isWorkExpRequired = isWorkExpRequired;
		this.isTeachExpUsed = isTeachExpUsed;
		this.isTeachExpRequired = isTeachExpRequired;
		this.isAcadeUsed = isAcadeUsed;
		this.isAcadeRequired = isAcadeRequired;
		this.isLastPositionUsed = isLastPositionUsed;
		this.isLastPositionRequired = isLastPositionRequired;
		this.isNationalCourseUsed = isNationalCourseUsed;
		this.isNationalCourseRequired = isNationalCourseRequired;
		this.isProvincialCourseUsed = isProvincialCourseUsed;
		this.isProvincialCourseRequired = isProvincialCourseRequired;
		this.isSchoolCourseUsed = isSchoolCourseUsed;
		this.isSchoolCourseRequired = isSchoolCourseRequired;
		this.isNationalPlanUsed = isNationalPlanUsed;
		this.isNationalPlanRequired = isNationalPlanRequired;
		this.isTextbookWriterUsed = isTextbookWriterUsed;
		this.isTextbookWriterRequired = isTextbookWriterRequired;
		this.isOtherTextbookUsed = isOtherTextbookUsed;
		this.isOtherTextbookRequired = isOtherTextbookRequired;
		this.isResearchUsed = isResearchUsed;
		this.isResearchRequired = isResearchRequired;
		this.isPublished = isPublished;
		this.isDeleted = isDeleted;
		this.gmtCreate = gmtCreate;
		this.founderId = founderId;
		this.gmtUpdate = gmtUpdate;
		this.menderId = menderId;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMaterialName() {
		return this.materialName;
	}

	public void setMaterialName(String materialName) {
		this.materialName = materialName;
	}

	public Integer getMaterialRound() {
		return this.materialRound;
	}

	public void setMaterialRound(Integer materialRound) {
		this.materialRound = materialRound;
	}

	public Long getMaterialType() {
		return this.materialType;
	}

	public void setMaterialType(Long materialType) {
		this.materialType = materialType;
	}

	public Date getDeadline() {
		return this.deadline;
	}

	public void setDeadline(Date deadline) {
		this.deadline = deadline;
	}

	public Date getActualDeadline() {
		return this.actualDeadline;
	}

	public void setActualDeadline(Date actualDeadline) {
		this.actualDeadline = actualDeadline;
	}

	public Date getAgeDeadline() {
		return this.ageDeadline;
	}

	public void setAgeDeadline(Date ageDeadline) {
		this.ageDeadline = ageDeadline;
	}
	
	public String getMailAddress() {
		return this.mailAddress;
	}

	public void setMailAddress(String mailAddress) {
		this.mailAddress = mailAddress;
	}

	public Short getProgress() {
		return this.progress;
	}

	public void setProgress(Short progress) {
		this.progress = progress;
	}

	public Long getDepartmentId() {
		return this.departmentId;
	}

	public void setDepartmentId(Long departmentId) {
		this.departmentId = departmentId;
	}

	public Long getDirector() {
		return this.director;
	}

	public void setDirector(Long director) {
		this.director = director;
	}

	public Long getProjectEditor() {
		return this.projectEditor;
	}

	public void setProjectEditor(Long projectEditor) {
		this.projectEditor = projectEditor;
	}

	public Boolean getIsMultiBooks() {
		return this.isMultiBooks;
	}

	public void setIsMultiBooks(Boolean isMultiBooks) {
		this.isMultiBooks = isMultiBooks;
	}

	public Boolean getIsMultiPosition() {
		return this.isMultiPosition;
	}

	public void setIsMultiPosition(Boolean isMultiPosition) {
		this.isMultiPosition = isMultiPosition;
	}

	public Boolean getIsEduExpUsed() {
		return this.isEduExpUsed;
	}

	public void setIsEduExpUsed(Boolean isEduExpUsed) {
		this.isEduExpUsed = isEduExpUsed;
	}

	public Boolean getIsEduExpRequired() {
		return this.isEduExpRequired;
	}

	public void setIsEduExpRequired(Boolean isEduExpRequired) {
		this.isEduExpRequired = isEduExpRequired;
	}

	public Boolean getIsWorkExpUsed() {
		return this.isWorkExpUsed;
	}

	public void setIsWorkExpUsed(Boolean isWorkExpUsed) {
		this.isWorkExpUsed = isWorkExpUsed;
	}

	public Boolean getIsWorkExpRequired() {
		return this.isWorkExpRequired;
	}

	public void setIsWorkExpRequired(Boolean isWorkExpRequired) {
		this.isWorkExpRequired = isWorkExpRequired;
	}

	public Boolean getIsTeachExpUsed() {
		return this.isTeachExpUsed;
	}

	public void setIsTeachExpUsed(Boolean isTeachExpUsed) {
		this.isTeachExpUsed = isTeachExpUsed;
	}

	public Boolean getIsTeachExpRequired() {
		return this.isTeachExpRequired;
	}

	public void setIsTeachExpRequired(Boolean isTeachExpRequired) {
		this.isTeachExpRequired = isTeachExpRequired;
	}

	public Boolean getIsAcadeUsed() {
		return this.isAcadeUsed;
	}

	public void setIsAcadeUsed(Boolean isAcadeUsed) {
		this.isAcadeUsed = isAcadeUsed;
	}

	public Boolean getIsAcadeRequired() {
		return this.isAcadeRequired;
	}

	public void setIsAcadeRequired(Boolean isAcadeRequired) {
		this.isAcadeRequired = isAcadeRequired;
	}

	public Boolean getIsLastPositionUsed() {
		return this.isLastPositionUsed;
	}

	public void setIsLastPositionUsed(Boolean isLastPositionUsed) {
		this.isLastPositionUsed = isLastPositionUsed;
	}

	public Boolean getIsLastPositionRequired() {
		return this.isLastPositionRequired;
	}

	public void setIsLastPositionRequired(Boolean isLastPositionRequired) {
		this.isLastPositionRequired = isLastPositionRequired;
	}

	public Boolean getIsNationalCourseUsed() {
		return this.isNationalCourseUsed;
	}

	public void setIsNationalCourseUsed(Boolean isNationalCourseUsed) {
		this.isNationalCourseUsed = isNationalCourseUsed;
	}

	public Boolean getIsNationalCourseRequired() {
		return this.isNationalCourseRequired;
	}

	public void setIsNationalCourseRequired(Boolean isNationalCourseRequired) {
		this.isNationalCourseRequired = isNationalCourseRequired;
	}

	public Boolean getIsProvincialCourseUsed() {
		return this.isProvincialCourseUsed;
	}

	public void setIsProvincialCourseUsed(Boolean isProvincialCourseUsed) {
		this.isProvincialCourseUsed = isProvincialCourseUsed;
	}

	public Boolean getIsProvincialCourseRequired() {
		return this.isProvincialCourseRequired;
	}

	public void setIsProvincialCourseRequired(Boolean isProvincialCourseRequired) {
		this.isProvincialCourseRequired = isProvincialCourseRequired;
	}

	public Boolean getIsSchoolCourseUsed() {
		return this.isSchoolCourseUsed;
	}

	public void setIsSchoolCourseUsed(Boolean isSchoolCourseUsed) {
		this.isSchoolCourseUsed = isSchoolCourseUsed;
	}

	public Boolean getIsSchoolCourseRequired() {
		return this.isSchoolCourseRequired;
	}

	public void setIsSchoolCourseRequired(Boolean isSchoolCourseRequired) {
		this.isSchoolCourseRequired = isSchoolCourseRequired;
	}

	public Boolean getIsNationalPlanUsed() {
		return this.isNationalPlanUsed;
	}

	public void setIsNationalPlanUsed(Boolean isNationalPlanUsed) {
		this.isNationalPlanUsed = isNationalPlanUsed;
	}

	public Boolean getIsNationalPlanRequired() {
		return this.isNationalPlanRequired;
	}

	public void setIsNationalPlanRequired(Boolean isNationalPlanRequired) {
		this.isNationalPlanRequired = isNationalPlanRequired;
	}

	public Boolean getIsTextbookWriterUsed() {
		return this.isTextbookWriterUsed;
	}

	public void setIsTextbookWriterUsed(Boolean isTextbookWriterUsed) {
		this.isTextbookWriterUsed = isTextbookWriterUsed;
	}

	public Boolean getIsTextbookWriterRequired() {
		return this.isTextbookWriterRequired;
	}

	public void setIsTextbookWriterRequired(Boolean isTextbookWriterRequired) {
		this.isTextbookWriterRequired = isTextbookWriterRequired;
	}

	public Boolean getIsOtherTextbookUsed() {
		return this.isOtherTextbookUsed;
	}

	public void setIsOtherTextbookUsed(Boolean isOtherTextbookUsed) {
		this.isOtherTextbookUsed = isOtherTextbookUsed;
	}

	public Boolean getIsOtherTextbookRequired() {
		return this.isOtherTextbookRequired;
	}

	public void setIsOtherTextbookRequired(Boolean isOtherTextbookRequired) {
		this.isOtherTextbookRequired = isOtherTextbookRequired;
	}

	public Boolean getIsResearchUsed() {
		return this.isResearchUsed;
	}

	public void setIsResearchUsed(Boolean isResearchUsed) {
		this.isResearchUsed = isResearchUsed;
	}

	public Boolean getIsResearchRequired() {
		return this.isResearchRequired;
	}

	public void setIsResearchRequired(Boolean isResearchRequired) {
		this.isResearchRequired = isResearchRequired;
	}

	public Boolean getIsPublished() {
		return this.isPublished;
	}

	public void setIsPublished(Boolean isPublished) {
		this.isPublished = isPublished;
	}

	public Boolean getIsDeleted() {
		return this.isDeleted;
	}

	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public Timestamp getGmtCreate() {
		return this.gmtCreate;
	}

	public void setGmtCreate(Timestamp gmtCreate) {
		this.gmtCreate = gmtCreate;
	}

	public Long getFounderId() {
		return this.founderId;
	}

	public void setFounderId(Long founderId) {
		this.founderId = founderId;
	}

	public Timestamp getGmtUpdate() {
		return this.gmtUpdate;
	}

	public void setGmtUpdate(Timestamp gmtUpdate) {
		this.gmtUpdate = gmtUpdate;
	}

	public Long getMenderId() {
		return this.menderId;
	}

	public void setMenderId(Long menderId) {
		this.menderId = menderId;
	}

	@Override
	public String toString() {
		return "{id:" + id + ", materialName:" + materialName
				+ ", materialRound:" + materialRound + ", materialType:"
				+ materialType + ", deadline:" + deadline + ", actualDeadline:"
				+ actualDeadline + ", ageDeadline:" + ageDeadline
				+ ", mailAddress:" + mailAddress + ", progress:" + progress
				+ ", departmentId:" + departmentId + ", director:" + director
				+ ", projectEditor:" + projectEditor + ", isMultiBooks:"
				+ isMultiBooks + ", isMultiPosition:" + isMultiPosition
				+ ", isEduExpUsed:" + isEduExpUsed + ", isEduExpRequired:"
				+ isEduExpRequired + ", isWorkExpUsed:" + isWorkExpUsed
				+ ", isWorkExpRequired:" + isWorkExpRequired
				+ ", isTeachExpUsed:" + isTeachExpUsed
				+ ", isTeachExpRequired:" + isTeachExpRequired
				+ ", isAcadeUsed:" + isAcadeUsed + ", isAcadeRequired:"
				+ isAcadeRequired + ", isLastPositionUsed:"
				+ isLastPositionUsed + ", isLastPositionRequired:"
				+ isLastPositionRequired + ", isNationalCourseUsed:"
				+ isNationalCourseUsed + ", isNationalCourseRequired:"
				+ isNationalCourseRequired + ", isProvincialCourseUsed:"
				+ isProvincialCourseUsed + ", isProvincialCourseRequired:"
				+ isProvincialCourseRequired + ", isSchoolCourseUsed:"
				+ isSchoolCourseUsed + ", isSchoolCourseRequired:"
				+ isSchoolCourseRequired + ", isNationalPlanUsed:"
				+ isNationalPlanUsed + ", isNationalPlanRequired:"
				+ isNationalPlanRequired + ", isTextbookWriterUsed:"
				+ isTextbookWriterUsed + ", isTextbookWriterRequired:"
				+ isTextbookWriterRequired + ", isOtherTextbookUsed:"
				+ isOtherTextbookUsed + ", isOtherTextbookRequired:"
				+ isOtherTextbookRequired + ", isResearchUsed:"
				+ isResearchUsed + ", isResearchRequired:" + isResearchRequired
				+ ", isPublished:" + isPublished + ", isDeleted:" + isDeleted
				+ ", gmtCreate:" + gmtCreate + ", founderId:" + founderId
				+ ", gmtUpdate:" + gmtUpdate + ", menderId:" + menderId + "}";
	}
	
	

}