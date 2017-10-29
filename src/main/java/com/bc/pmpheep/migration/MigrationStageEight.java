/*
 * Copyright 2017 BangChen Information Technology Ltd., Co.
 * Licensed under the Apache License 2.0.
 */
package com.bc.pmpheep.migration;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.bc.pmpheep.back.po.PmphGroup;
import com.bc.pmpheep.back.po.PmphGroupMember;
import com.bc.pmpheep.back.po.PmphGroupMessage;
import com.bc.pmpheep.back.service.PmphGroupMemberService;
import com.bc.pmpheep.back.service.PmphGroupMessageService;
import com.bc.pmpheep.back.service.PmphGroupService;
import com.bc.pmpheep.general.bean.ImageType;
import com.bc.pmpheep.general.service.FileService;
import com.bc.pmpheep.migration.common.JdbcHelper;
import com.bc.pmpheep.migration.common.SQLParameters;
import com.bc.pmpheep.utils.ExcelHelper;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.Map;

/**
 * 数据迁移方案第八部分 - 小组相关数据迁移
 *
 * @author L.X <gugia@qq.com>
 */
@Component
public class MigrationStageEight {

    private final Logger logger = LoggerFactory.getLogger(MigrationStageEight.class);

    @Resource
    PmphGroupService groupService;
    @Resource
    PmphGroupMemberService groupMemberService;
    @Resource
    PmphGroupMessageService groupMessageService;
    @Resource
    FileService fileService;
    @Resource
    ExcelHelper excelHelper;

    public void start() {
        group();
        groupMember();
        groupMessage();
    }

    protected void group() {
        String tableName = "bbs_group"; //要迁移的旧库表名
        JdbcHelper.addColumn(tableName); //增加new_pk字段
        List<Map<String, Object>> maps = JdbcHelper.queryForList(tableName);//取得该表中所有数据
        int count = 0;//迁移成功的条目数
        List<Map<String, Object>> excel = new LinkedList<>();
        /* 开始遍历查询结果 */
        for (Map<String, Object> map : maps) {
            /* 根据MySQL字段类型进行类型转换 */
            String groupID = (String) map.get("groupID");
            String groupTitle = (String) map.get("groupTitle");
            int groupType = (Integer) map.get("groupType");
            String createTime = map.get("createTime").toString();
            String groupImg = (String) map.get("groupImg");
            String bookid = (String) map.get("bookid");
            /* 开始新增新表对象，并设置属性值 */
            PmphGroup pmphGroup = new PmphGroup();
            pmphGroup.setGroupName(groupTitle);
            pmphGroup.setGroupImage("default");//小组默认头像
            pmphGroup.setGmtCreate(Timestamp.valueOf(createTime));
            Long bookId = JdbcHelper.getPrimaryKey("teach_bookinfo", "bookid", bookid);
            if (null != bookId) {
                pmphGroup.setBookId(bookId);
            }
            pmphGroup = groupService.addPmphGroup(pmphGroup);
            long pk = pmphGroup.getId();
            JdbcHelper.updateNewPrimaryKey(tableName, pk, "groupID", groupID);//更新旧表中new_pk字段
            count++;
            /* 以下读取小组头像并保存在mongoDB中，读取失败时导出到Excel中 */
            String mongoId = "";
            try {
                mongoId = fileService.saveLocalFile(groupImg, ImageType.GROUP_AVATAR, pk);
            } catch (IOException ex) {
                logger.error("文件读取异常，路径<{}>，异常信息：{}", groupImg, ex.getMessage());
                map.put(SQLParameters.EXCEL_EX_HEADER, "文件读取异常");
                excel.add(map);
            } finally {
                pmphGroup.setGroupImage(mongoId);
                groupService.updatePmphGroup(pmphGroup);
            }
        }
        try {
            excelHelper.exportFromMaps(excel, tableName, tableName);
        } catch (IOException ex) {
            logger.error("异常数据导出到Excel失败", ex);
        }
        logger.info("bbs_group表迁移完成，异常条目数量：{}", excel.size());
        logger.info("原数据库中共有{}条数据，迁移了{}条数据", maps.size(), count);
    }

    protected void groupMember() {
        String tableName = "bbs_groupusers"; //要迁移的旧库表名
        JdbcHelper.addColumn(tableName); //增加new_pk字段
        List<Map<String, Object>> maps = JdbcHelper.queryForList(tableName);//取得该表中所有数据
        int count = 0;//迁移成功的条目数
        List<Map<String, Object>> excel = new LinkedList<>();
        String sql = "SELECT sysflag FROM sys_user WHERE userid = ?";
        /* 开始遍历查询结果 */
        for (Map<String, Object> map : maps) {
            PmphGroupMember member = new PmphGroupMember();
            String groupID = (String) map.get("groupID");
            Long groupId = JdbcHelper.getPrimaryKey("bbs_group", "groupID", groupID);
            if (null == groupId) {
                map.put(SQLParameters.EXCEL_EX_HEADER, "未找到成员所属小组");
                excel.add(map);
                logger.error("未找到成员所属小组，此结果将被记录在Excel中");
                continue;
            }
            String userID = (String) map.get("userID");
            Long userId = JdbcHelper.getPrimaryKey("sys_user", "userid", userID);
            if (null == userId) {
                map.put(SQLParameters.EXCEL_EX_HEADER, "未找到成员在sys_user表中的对应主键");
                excel.add(map);
                logger.error("未找到成员在sys_user表中的对应主键，此结果将被记录在Excel中");
                continue;
            } else {
                member.setUserId(userId);
                Integer sysflag = JdbcHelper.getJdbcTemplate().queryForObject(sql, Integer.class, userID);
                switch (sysflag) {
                    case 0:
                        member.setIsWriter(false);
                        break;
                    case 1:
                        member.setIsWriter(true);
                        break;
                    default:
                        map.put(SQLParameters.EXCEL_EX_HEADER, "小组成员对应的sysflag无法识别");
                        excel.add(map);
                        logger.error("小组成员对应的sysflag无法识别，此结果将被记录在Excel中");
                        continue;
                }
            }
            member.setGroupId(groupId);
            /* 是否创建者的判断依据是GUID是否等于groupID */
            String guid = map.get("GUID").toString();
            if (guid.equals(groupID)) {
                member.setIsFounder(true);
            }
            String userName = map.get("userName").toString();
            member.setDisplayName(userName);
            /* 旧表的isManager和新表的is_admin疑似刚好相反 */
            int isManager = (Integer) map.get("isManager");
            member.setIsAdmin(isManager == 0);
            member = groupMemberService.addPmphGroupMember(member);
            long pk = member.getId();
            JdbcHelper.updateNewPrimaryKey(tableName, pk, "GUID", guid);//更新旧表中new_pk字段
            count++;
        }
        try {
            excelHelper.exportFromMaps(excel, tableName, tableName);
        } catch (IOException ex) {
            logger.error("异常数据导出到Excel失败", ex);
        }
        logger.info("bbs_groupusers表迁移完成，异常条目数量：{}", excel.size());
        logger.info("原数据库中共有{}条数据，迁移了{}条数据", maps.size(), count);
    }

    protected void groupMessage() {
        String tableName = "bbs_discuss"; //要迁移的旧库表名
        JdbcHelper.addColumn(tableName); //增加new_pk字段
        /* 取得PmphGroupMember对应旧表的所有数据 */
        List<Map<String, Object>> maps = JdbcHelper.queryForList(tableName);//取得该表中所有数据
        int count = 0;//迁移成功的条目数
        List<Map<String, Object>> excel = new LinkedList<>();
        String sql = "SELECT new_pk FROM bbs_groupusers WHERE groupID = ? AND userID = ?";
        /* 以下开始遍历旧表数据，根据相应规则进行数据转换 */
        for (Map<String, Object> map : maps) {
            PmphGroupMessage groupMessage = new PmphGroupMessage();
            String groupID = (String) map.get("groupID");
            Long groupId = JdbcHelper.getPrimaryKey("bbs_group", "groupID", groupID);
            /* 如果消息没有对应的小组id，则小组可能已被删除，这里应该打印并记录（待完成） */
            if (null == groupId) {
                map.put(SQLParameters.EXCEL_EX_HEADER, "未找到消息对应小组");
                excel.add(map);
                logger.error("未找到消息对应小组，此结果将被记录在Excel中");
                continue;
            }
            groupMessage.setGroupId(groupId);
            /* 新库member_id是pmph_group_member的主键，这里需要转换 */
            String userID = (String) map.get("userID");
            Long memberId = JdbcHelper.getJdbcTemplate().queryForObject(sql, Long.class, groupID, userID);
            if (null == memberId) {
                map.put(SQLParameters.EXCEL_EX_HEADER, "未找到消息对应成员");
                excel.add(map);
                logger.error("未找到消息对应成员，此结果将被记录在Excel中");
                continue;
            }
            groupMessage.setMemberId(memberId);
            String content = (String) map.get("content");
            groupMessage.setMsgContent(content);
            String createtime = map.get("createtime").toString();
            groupMessage.setGmtCreate(Timestamp.valueOf(createtime));
            String id = (String) map.get("ID");
            groupMessage = groupMessageService.addPmphGroupMessage(groupMessage);
            long pk = groupMessage.getId();
            JdbcHelper.updateNewPrimaryKey(tableName, pk, "ID", id);//更新旧表中new_pk字段
            count++;
        }
        try {
            excelHelper.exportFromMaps(excel, tableName, tableName);
        } catch (IOException ex) {
            logger.error("异常数据导出到Excel失败", ex);
        }
        logger.info("bbs_discuss表迁移完成，异常条目数量：{}", excel.size());
        logger.info("原数据库中共有{}条数据，迁移了{}条数据", maps.size(), count);
    }
}
