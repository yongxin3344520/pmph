package com.bc.pmpheep.back.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.bc.pmpheep.back.common.service.BaseService;
import com.bc.pmpheep.back.dao.PmphGroupMessageDao;
import com.bc.pmpheep.back.po.PmphGroupMember;
import com.bc.pmpheep.back.po.PmphGroupMessage;

/**
 * PmphGroupMessageService 接口实现
 * @author Mryang
 *
 */
@Service
public class PmphGroupMessageServiceImpl extends BaseService implements PmphGroupMessageService {
	@Autowired
	private PmphGroupMessageDao pmphGroupMessageDao;
	
	/**
	 * 
	 * @param  PmphGroupMessage 实体对象
	 * @return  带主键的 PmphGroupMessage
	 * @throws Exception 
	 */
	@Override
	public PmphGroupMember addPmphGroupMessage (PmphGroupMessage pmphGroupMessage) throws Exception{
		return pmphGroupMessageDao.addPmphGroupMessage (pmphGroupMessage);
	}
	
	/**
	 * 
	 * @param PmphGroupMessage 必须包含主键ID
	 * @return  PmphGroupMessage
	 * @throws Exception，NullPointerException(主键为空)
	 */
	@Override
	public PmphGroupMessage getPmphGroupMessageById(PmphGroupMessage pmphGroupMessage) throws Exception{
		if(null==pmphGroupMessage.getId()){
			throw new NullPointerException("主键id为空");
		}
		return pmphGroupMessageDao.getPmphGroupMessageById(pmphGroupMessage);
	}
	
	/**
	 * 
	 * @param PmphGroupMessage
	 * @return  影响行数
	 * @throws Exception，NullPointerException(主键为空)
	 */
	@Override
	public Integer deletePmphGroupMessageById(PmphGroupMessage pmphGroupMessage) throws Exception{
		if(null==pmphGroupMessage.getId()){
			throw new NullPointerException("主键id为空");
		}
		return pmphGroupMessageDao.deletePmphGroupMessageById(pmphGroupMessage);
	}
	
	/**
	 * @param PmphGroupMessage
	 * @return 影响行数
	 * @throws Exception ，NullPointerException(主键为空)
	 */
	@Override 
	public Integer updatePmphGroupMessageById(PmphGroupMessage pmphGroupMessage) throws Exception{
		if(null==pmphGroupMessage.getId()){
			throw new NullPointerException("主键id为空");
		}
		return pmphGroupMessageDao.updatePmphGroupMessageById(pmphGroupMessage);
	}
}