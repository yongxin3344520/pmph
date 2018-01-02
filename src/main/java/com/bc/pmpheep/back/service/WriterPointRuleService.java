package com.bc.pmpheep.back.service;

import java.util.List;

import com.bc.pmpheep.back.plugin.PageParameter;
import com.bc.pmpheep.back.plugin.PageResult;
import com.bc.pmpheep.back.po.WriterPointRule;
import com.bc.pmpheep.back.vo.OrgVO;
import com.bc.pmpheep.back.vo.WriterPointRuleVO;
import com.bc.pmpheep.service.exception.CheckedServiceException;

/**
 * 积分规则 业务层
 * @author mr
 *
 */
public interface WriterPointRuleService {
	/**
	 * 分页查询积分规则列表
	 * @return
	 * @throws CheckedServiceException
	 */
	PageResult<WriterPointRuleVO> getListWriterPointRule(PageParameter<WriterPointRuleVO> pageParameter) 
			throws CheckedServiceException;
	
	/**
	 * 分页查询积分兑换规则列表
	 * @param pageParameter
	 * @return
	 * @throws CheckedServiceException
	 */
	PageResult<WriterPointRuleVO> getlistWriterPointRulePoint(PageParameter<WriterPointRuleVO> pageParameter) 
			throws CheckedServiceException;
    
    /**
     * 修改积分规则
     * @param writerPointRule
     * @return
     * @throws CheckedServiceException
     */
    Integer updateWriterPointRule(WriterPointRule writerPointRule) throws CheckedServiceException;
    
    /**
     * 添加积分规则
     * @param writerPointRule
     * @return
     * @throws CheckedServiceException
     */
    WriterPointRule addWriterPointRule(WriterPointRule writerPointRule)throws CheckedServiceException;
    
    /**
     * 删除积分规则
     * @param id
     * @return
     * @throws CheckedServiceException
     */
    Integer deleteWriterPointRule(Long id) throws CheckedServiceException;;
}
