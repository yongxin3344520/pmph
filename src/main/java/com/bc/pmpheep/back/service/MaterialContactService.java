package com.bc.pmpheep.back.service;

import java.util.List;

import com.bc.pmpheep.back.po.MaterialContact;
import com.bc.pmpheep.service.exception.CheckedServiceException;

/**
 * MaterialContactService 接口
 * 
 * @author 曾庆峰
 *
 */
public interface MaterialContactService {

	/**
	 * 新增一个MaterialContact
	 * 
	 * @param MaterialContact
	 *            实体对象
	 * @return 带主键的 MaterialContact thorws CheckedServiceException
	 */
	MaterialContact addMaterialContact(MaterialContact materialContact) throws CheckedServiceException;

	/**
	 * 查询一个 MaterialContact 通过主键id
	 * 
	 * @param id
	 * @return MaterialContact
	 * @throws CheckedServiceException
	 */
	MaterialContact getMaterialContactById(Long id) throws CheckedServiceException;

	/**
	 * 删除MaterialContact 通过主键id
	 * 
	 * @param MaterialContact
	 * @return 影响行数
	 * @throws CheckedServiceException
	 */
	Integer deleteMaterialContactById(Long id) throws CheckedServiceException;

	/**
	 * 删除MaterialContact 通过主键materialId
	 * 
	 * @param MaterialContact
	 * @return 影响行数
	 * @throws CheckedServiceException
	 */
	Integer deleteMaterialContactsByMaterialId(Long materialId) throws CheckedServiceException;

	/**
	 * 根据id 更新materialContact不为null和''的字段
	 * 
	 * @param MaterialContact
	 * @return 影响行数
	 * @throws CheckedServiceException
	 */
	Integer updateMaterialContact(MaterialContact materialContact) throws CheckedServiceException;

	/**
	 * 
	 * 
	 * 功能描述：根据教材id获取联系人
	 *
	 * @param materialId
	 *            教材id
	 * @return
	 * @throws CheckedServiceException
	 *
	 */
	List<MaterialContact> listMaterialContactByMaterialId(Long materialId) throws CheckedServiceException;
}
