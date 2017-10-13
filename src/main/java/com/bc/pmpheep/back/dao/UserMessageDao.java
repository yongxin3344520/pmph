package com.bc.pmpheep.back.dao;

import java.util.List;
import org.springframework.stereotype.Repository;
import com.bc.pmpheep.back.plugin.PageParameter;
import com.bc.pmpheep.back.po.UserMessage;
import com.bc.pmpheep.back.vo.MessageStateVO;
import com.bc.pmpheep.back.vo.UserMessageVO;

/**
 * 
 * UserMessage 实体类数据访问层接口
 *
 * @author Mryang
 *
 * @createDate 2017年9月27日 下午2:33:53
 *
 */
@Repository
public interface UserMessageDao {

	/**
	 * 
	 * 获取MessageState列表（同时查询分页数据和总条数）
	 * 
	 * @author Mryang
	 * @createDate 2017年9月27日 上午10:36:10
	 * @param pageParameter
	 * @return List<MessageStateVO>
	 */
	List<MessageStateVO> listMessageState(PageParameter<MessageStateVO> pageParameter);

	/**
	 * 
	 * 
	 * 功能描述：根据条件查询所有的条数
	 *
	 * @param title
	 *            标题
	 * @return 总数
	 *
	 */
	Integer getMessageTotal(PageParameter<UserMessageVO> pageParameter);

	/**
	 * 
	 * 
	 * 功能描述：初始化/模糊查询系统消息 分页
	 *
	 * @param pageParameter
	 *            分页条件以及查询条件
	 * @return 一页的结果集
	 *
	 */
	List<UserMessageVO> listMessage(PageParameter<UserMessageVO> pageParameter);

	/**
	 * 批量插入 UserMessage
	 * 
	 * @author Mryang
	 * @createDate 2017年9月28日 下午3:35:46
	 * @param userMessageList
	 */
	void addUserMessageBatch(List<UserMessage> userMessageList);

	/**
	 * 根据消息msgId 获取 UserMessage集
	 * 
	 * @author Mryang
	 * @createDate 2017年9月29日 下午3:17:56
	 * @param msgId
	 * @return
	 */
	List<UserMessage> getMessageByMsgId(String msgId);

	/**
	 * 通过msgId 动态更新UserMessage
	 */
	Integer updateUserMessageByMsgId(String msgId);

	/**
	 * 通过id 动态更新UserMessage
	 */
	Integer updateUserMessageById(Long id);

	/**
	 * 通过消息id删除UserMessage
	 */
	Integer deleteMessageByMsgId(String msgId);

}
