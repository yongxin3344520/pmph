package com.bc.pmpheep.back.service;

import java.util.List;

import com.bc.pmpheep.back.plugin.Page;
import com.bc.pmpheep.back.po.Org;
import com.bc.pmpheep.back.vo.OrgVO;
import com.bc.pmpheep.service.exception.CheckedServiceException;

/**
 * OrgService 接口
 * 
 * @author Mryang
 *
 */
public interface OrgService {

	/**
	 * 
	 * @param org
	 *            实体对象
	 * @return Org 带主键
	 * @throws CheckedServiceException
	 */
	Org addOrg(Org org) throws CheckedServiceException;

	/**
	 * 
	 * @param id
	 * @return Org
	 * @throws CheckedServiceException
	 */
	Org getOrgById(Long id) throws CheckedServiceException;

	/**
	 * 
	 * @param id
	 * @return 影响行数
	 * @throws CheckedServiceException
	 */
	Integer deleteOrgById(Long id) throws CheckedServiceException;

	/**
	 * @param org
	 * @return 影响行数
	 * @throws CheckedServiceException
	 */
	Integer updateOrg(Org org) throws CheckedServiceException;

	/**
	 * @param page
	 *            带有分页参数和查询条件参数
	 * @return Page<OrgVO,OrgVO> 包含 List<OrgVO>以及分页数据
	 * @throws CheckedServiceException
	 */
	Page<OrgVO, OrgVO> getOrgList(Page<OrgVO, OrgVO> page) throws CheckedServiceException;
	
	/**
	 * 
	 *  
	 * 功能描述：在新增用户与修改用户时查询机构
	 *
	 * @param orgName 机构名称
	 * @return 模糊查询出的机构集合
	 * @throws CheckedServiceException
	 *
	 */
	List<OrgVO> getOegListByOrgName(String orgName) throws CheckedServiceException;
}
