package com.bc.pmpheep.back.service.test;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bc.pmpheep.back.po.CmsSchedule;
import com.bc.pmpheep.back.service.CmsScheduleService;
import com.bc.pmpheep.back.util.DateUtil;
import com.bc.pmpheep.test.BaseTest;

/**
 * 
 * <pre>
 * 功能描述：CmsScheduleService 测试类
 * 使用示范：
 * 
 * 
 * @author (作者) nyz
 * 
 * @since (该版本支持的JDK版本) ：JDK 1.6或以上
 * @version (版本) 1.0
 * @date (开发日期) 2017-10-26
 * @modify (最后修改时间) 
 * @修改人 ：nyz 
 * @审核人 ：
 * </pre>
 */
public class CmsScheduleServiceTest extends BaseTest {
    Logger             logger = LoggerFactory.getLogger(CmsScheduleServiceTest.class);

    @Resource
    CmsScheduleService cmsScheduleService;

    /**
     * 
     * <pre>
     * 功能描述：add 方法测试
     * 使用示范：
     *
     * </pre>
     */
    @Test
    public void testAddCmsSchedule() {
        CmsSchedule cmsSchedule = this.addCmsSchedule();
        logger.info(cmsSchedule.toString());
        Assert.assertNotNull("插入内容后返回的CmsSchedule.id不应为空", cmsSchedule.getId());
    }

    @Test
    public void testUpdateCmsSchedule() {
        CmsSchedule cmsSchedule = this.addCmsSchedule();
        Integer count =
        cmsScheduleService.updateCmsSchedule(new CmsSchedule(cmsSchedule.getId(), 30809L,
                                                             DateUtil.getCurrentTime()));
        Assert.assertTrue("是否更新CmsSchedule成功", count > 0);
    }

    @Test
    public void testGetCmsScheduleById() {
        CmsSchedule cmsSchedule = this.addCmsSchedule();
        CmsSchedule cms = cmsScheduleService.getCmsScheduleById(cmsSchedule.getId());
        logger.info(cms.toString());
        Assert.assertNotNull("按ID查询是否该对象", cms);
    }

    @Test
    public void testDeleteCmsScheduleById() {
        CmsSchedule cmsSchedule = this.addCmsSchedule();
        Assert.assertTrue("是否删除成功",
                          cmsScheduleService.deleteCmsScheduleById(cmsSchedule.getId()) > 0);
        CmsSchedule cs = this.addCmsSchedule();
        List<Long> idList = new ArrayList<Long>(1);
        idList.add(cs.getId());
        Assert.assertTrue("批量删除是否成功", cmsScheduleService.deleteCmsScheduleByIds(idList) > 0);
    }

    private CmsSchedule addCmsSchedule() {
        CmsSchedule cmsSchedule =
        cmsScheduleService.addCmsSchedule(new CmsSchedule(1102L, DateUtil.getCurrentTime()));
        return cmsSchedule;
    }
}
