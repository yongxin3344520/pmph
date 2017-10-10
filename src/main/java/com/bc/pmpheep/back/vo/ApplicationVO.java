package com.bc.pmpheep.back.vo;

import java.util.ArrayList;
import java.util.List;

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

/**
 *@author MrYang 
 *@CreateDate 2017年9月30日 下午4:34:28
 *
 **/
public class ApplicationVO {
	//申报职位
	private List<DecPosition> decPositionList =new ArrayList<DecPosition>(5);
	//作家申报表
	private Declaration       declaration     =new Declaration();
	//主要学习经历
	private List<DecEduExp>   decEduExpList   =new ArrayList<DecEduExp>(5);
	//主要工作经历
	private List<DecWorkExp>  decWorkExpList  =new ArrayList<DecWorkExp>(5);
	//主要教学经历
	private List<DecTeachExp> decTeachExpList =new ArrayList<DecTeachExp>(5);
	//主要学术兼职
	private List<DecAcade>    decAcadeList =new ArrayList<DecAcade>(5);
	//上版教材参编情况
	private List<DecLastPosition> decLastPositionList =new ArrayList<DecLastPosition>(5);
	//国家级精品课程建设情况 //type 1=国家
	private List<DecCourseConstruction> decNationalCourseConstructionList =new ArrayList<DecCourseConstruction>(5);
	//省部级精品课程建设情况//type 2=省部
	private List<DecCourseConstruction> decProvinceCourseConstructionList =new ArrayList<DecCourseConstruction>(5);
	//学校精品课程建设情况   //type 3=学校
	private List<DecCourseConstruction> decSchoolCourseConstructionList =new ArrayList<DecCourseConstruction>(5);
	//主编国家规划教材情况
	private List<DecNationalPlan> decNationalPlanList =new ArrayList<DecNationalPlan>(5);
	//教材编写情况
	private List<DecTextbook>     decTextbookList =new ArrayList<DecTextbook>(5);
	//其他教材编写情况
	private List<DecNationalPlan> decOtherPositionList =new ArrayList<DecNationalPlan>(5);
	//科研情况
	private List<DecResearch>   decResearchList =new ArrayList<DecResearch>(5);
	//作家扩展项
	private List<DecExtension> decExtensionList =new ArrayList<DecExtension>(5);
	
	
	public ApplicationVO() {
		super();
	}
	
	public List<DecPosition> getDecPositionList() {
		return decPositionList;
	}
	public void setDecPositionList(List<DecPosition> decPositionList) {
		this.decPositionList = decPositionList;
	}
	public Declaration getDeclaration() {
		return declaration;
	}
	public void setDeclaration(Declaration declaration) {
		this.declaration = declaration;
	}
	public List<DecEduExp> getDecEduExpList() {
		return decEduExpList;
	}
	public void setDecEduExpList(List<DecEduExp> decEduExpList) {
		this.decEduExpList = decEduExpList;
	}
	public List<DecWorkExp> getDecWorkExpList() {
		return decWorkExpList;
	}
	public void setDecWorkExpList(List<DecWorkExp> decWorkExpList) {
		this.decWorkExpList = decWorkExpList;
	}
	public List<DecTeachExp> getDecTeachExpList() {
		return decTeachExpList;
	}
	public void setDecTeachExpList(List<DecTeachExp> decTeachExpList) {
		this.decTeachExpList = decTeachExpList;
	}
	public List<DecAcade> getDecAcadeList() {
		return decAcadeList;
	}
	public void setDecAcadeList(List<DecAcade> decAcadeList) {
		this.decAcadeList = decAcadeList;
	}
	public List<DecLastPosition> getDecLastPositionList() {
		return decLastPositionList;
	}
	public void setDecLastPositionList(List<DecLastPosition> decLastPositionList) {
		this.decLastPositionList = decLastPositionList;
	}
	public List<DecCourseConstruction> getDecNationalCourseConstructionList() {
		return decNationalCourseConstructionList;
	}
	public void setDecNationalCourseConstructionList(
			List<DecCourseConstruction> decNationalCourseConstructionList) {
		this.decNationalCourseConstructionList = decNationalCourseConstructionList;
	}
	public List<DecCourseConstruction> getDecProvinceCourseConstructionList() {
		return decProvinceCourseConstructionList;
	}
	public void setDecProvinceCourseConstructionList(
			List<DecCourseConstruction> decProvinceCourseConstructionList) {
		this.decProvinceCourseConstructionList = decProvinceCourseConstructionList;
	}
	public List<DecCourseConstruction> getDecSchoolCourseConstructionList() {
		return decSchoolCourseConstructionList;
	}
	public void setDecSchoolCourseConstructionList(
			List<DecCourseConstruction> decSchoolCourseConstructionList) {
		this.decSchoolCourseConstructionList = decSchoolCourseConstructionList;
	}
	public List<DecNationalPlan> getDecNationalPlanList() {
		return decNationalPlanList;
	}
	public void setDecNationalPlanList(List<DecNationalPlan> decNationalPlanList) {
		this.decNationalPlanList = decNationalPlanList;
	}
	public List<DecTextbook> getDecTextbookList() {
		return decTextbookList;
	}
	public void setDecTextbookList(List<DecTextbook> decTextbookList) {
		this.decTextbookList = decTextbookList;
	}
	public List<DecNationalPlan> getDecOtherPositionList() {
		return decOtherPositionList;
	}
	public void setDecOtherPositionList(List<DecNationalPlan> decOtherPositionList) {
		this.decOtherPositionList = decOtherPositionList;
	}
	public List<DecResearch> getDecResearchList() {
		return decResearchList;
	}
	public void setDecResearchList(List<DecResearch> decResearchList) {
		this.decResearchList = decResearchList;
	}
	public List<DecExtension> getDecExtensionList() {
		return decExtensionList;
	}
	public void setDecExtensionList(List<DecExtension> decExtensionList) {
		this.decExtensionList = decExtensionList;
	}
	
}