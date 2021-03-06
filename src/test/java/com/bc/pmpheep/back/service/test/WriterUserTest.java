package com.bc.pmpheep.back.service.test;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import com.bc.pmpheep.back.dao.WriterUserDao;
import com.bc.pmpheep.back.plugin.PageParameter;
import com.bc.pmpheep.back.plugin.PageResult;
import com.bc.pmpheep.back.po.WriterUser;
import com.bc.pmpheep.back.service.WriterUserService;
import com.bc.pmpheep.back.util.Const;
import com.bc.pmpheep.back.vo.WriterUserManagerVO;
import com.bc.pmpheep.test.BaseTest;

/**
 * WriterUser 单元测试
 * 
 * @author Administrator
 * 
 */
public class WriterUserTest extends BaseTest {
    Logger            logger = LoggerFactory.getLogger(WriterUserTest.class);

    @Autowired
    WriterUserService userService;
    @Resource
    WriterUserDao     writerUserDao;

    @Test
    @Rollback(Const.ISROLLBACK)
    public void testCount() {
        WriterUser writerUser = this.addWriterUser();
        Long num = writerUserDao.getWriterUserCount();
        Assert.assertNotNull("获取条数失败", num);
    }

    /**
     * WriterUser 添加Test
     */
    @Test
    @Rollback(Const.ISROLLBACK)
    public void testAdd() {
        WriterUser writerUser = this.addWriterUser();
        Assert.assertNotNull("添加失败", writerUser.getId());
    }

    @Test
    @Rollback(Const.ISROLLBACK)
    public void testAddList() {
        WriterUser writerUser = this.addWriterUser();
        List<Long> allList = new ArrayList<>();
        allList.add(1L);
        allList.add(1L);
        allList.add(2L);
        allList.add(3L);
        WriterUser ps = userService.add(new WriterUser("test1", "123"), allList);// 给单用户添加多个角色
        Assert.assertNotNull(ps);
    }

    /**
     * 查询
     */
    // @Test
    // @Rollback(Const.ISROLLBACK)
    // public void testGetLists() {
    // WriterUser wtUser;
    // List<WriterUser> wtUsers;
    // List<WriterPermission> listPermissions;
    // wtUsers = userService.getList();// 查询所有
    // Assert.assertNotNull(wtUsers);
    // wtUser = userService.getByUsernameAndPassword("test1", ShiroKit.md5("123", "test1"));//
    // 按UserName
    // Assert.assertNotNull(wtUser);
    // wtUser = userService.get(1L);// 按ID查询对象
    // Assert.assertNotNull(wtUser);
    // wtUser = userService.login("test1", "123");
    // Assert.assertNotNull(wtUser);
    // wtUsers = userService.getListByRole(1L);
    // Assert.assertNotNull(wtUsers);
    // listPermissions = userService.getListAllResource(1L);
    // Assert.assertNotNull(listPermissions);
    // List<String> listRoleNameList = userService.getListRoleSnByUser(1L);
    // Assert.assertNotNull(listRoleNameList);
    // List<WriterRole> pr = userService.getListUserRole(1L);
    // Assert.assertNotNull(pr);
    // }

    /**
     * writerUser 更新方法
     */
    // @Test
    // @Rollback(Const.ISROLLBACK)
    // public void testUpdatePmphUser() {
    // WriterUser writerUser = new WriterUser();
    // writerUser.setId(18L);
    // writerUser.setUsername("admin1");
    // List<Long> userIdList = new ArrayList<Long>();
    // userIdList.add(1L);
    // userIdList.add(2L);
    // WriterUser pu = userService.update(writerUser);
    // Assert.assertNotNull(pu);
    // WriterUser pu1 = userService.update(writerUser, userIdList);
    // Assert.assertNotNull(pu1);
    // }

    /**
     * 
     * 
     * 功能描述：作家用户管理页面的单元测试
     * 
     * 
     */
    @Test
    @Rollback(Const.ISROLLBACK)
    public void testBackWriterUser() {
        WriterUser writerUser = this.addWriterUser();
        // PageParameter<GroupMemberWriterUserVO> pageParameter = new PageParameter<>(1,
        // 20);
        // GroupMemberWriterUserVO groupMemberWriterUserVO = new
        // GroupMemberWriterUserVO();
        // groupMemberWriterUserVO.setBookname(null);
        // groupMemberWriterUserVO.setChosenPosition(null);
        // groupMemberWriterUserVO.setName(null);
        // pageParameter.setParameter(groupMemberWriterUserVO);
        // PageResult<GroupMemberWriterUserVO> pageResult =
        // userService.listGroupMemberWriterUsers(pageParameter);
        // Assert.assertNotNull(pageResult);
        // logger.debug(pageResult.toString());
        PageParameter<WriterUserManagerVO> pageParameter2 = new PageParameter<>();
        WriterUserManagerVO writerUserManagerVO = new WriterUserManagerVO();
        writerUserManagerVO.setName(null);
        writerUserManagerVO.setOrgName(null);
        writerUserManagerVO.setRank(null);
        pageParameter2.setPageNumber(1);
        pageParameter2.setPageSize(20);
        pageParameter2.setParameter(writerUserManagerVO);
        PageResult<WriterUserManagerVO> pageResult2 = userService.getListWriterUser(pageParameter2,null);
        Assert.assertNotNull(pageResult2);
    }

    /**
     * ：修改作家用户
     */
    @Test
    @Rollback(Const.ISROLLBACK)
    public void testUpdateWriterUserOfBack() {
        WriterUser writerUser = this.addWriterUser();
        writerUser.setUsername(writerUser.getUsername());
        writerUser.setRealname("姓名");
        Assert.assertNotNull("修改失败", userService.updateWriterUserOfBack(writerUser));
    }

    /**
     * 后台添加作家用户
     */
    @Test
    @Rollback(Const.ISROLLBACK)
    public void testAddWriterUserOfBack() {
        WriterUser writerUser = this.addWriterUser();
        writerUser.setUsername("zuojiayonghu");
        writerUser.setNickname("昵称");
        writerUser.setHandphone("13488889898");
        writerUser.setEmail("123@126.com");
        Assert.assertTrue("添加失败", userService.addWriterUserOfBack(writerUser).equals("SUCCESS"));
    }

    /**
     * 获取教师审核列表
     */
    @Test
    @Rollback(Const.ISROLLBACK)
    public void testGetTeacherCheckList() {
        WriterUserManagerVO writerUserManagerVO = new WriterUserManagerVO();
        PageParameter<WriterUserManagerVO> pageParameter =
        new PageParameter<WriterUserManagerVO>(1, 10, writerUserManagerVO);
        Assert.assertNotNull("获取教师列表失败", userService.getTeacherCheckList(pageParameter));
    }

    /**
     * 根据id查询用户权限
     */
    @Test
    @Rollback(Const.ISROLLBACK)
    public void testGetWriterUserPermissionByUserId() {
        WriterUser writerUser = this.addWriterUser();
        Assert.assertNotNull("获取用户权限失败",
                             userService.getWriterUserPermissionByUserId(writerUser.getId()));
    }

    private WriterUser addWriterUser() {
        WriterUser writerUser =
        userService.add(new WriterUser("user", "123", false, 123L, null, null, null, null, null,
                                       null, null, null, null, null, null, null, null, null, null,
                                       null, false, null, null, null, null, false, false, null,
                                       null, null, null, false, null, null));
        return writerUser;
    }
    
	@Test
	public void resetPasswordTest() {
		String password = userService.resetPassword(1L);
		Assert.assertNotNull("修改失败", password);
	}
}
