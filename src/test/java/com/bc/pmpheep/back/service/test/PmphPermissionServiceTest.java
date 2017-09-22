package com.bc.pmpheep.back.service.test;

import java.util.Random;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bc.pmpheep.back.po.PmphPermission;
import com.bc.pmpheep.back.service.PmphPermissionService;
import com.bc.pmpheep.test.BaseTest;

/**
 * AreaDao 单元测试
 * 
 * @author mryang
 */
public class PmphPermissionServiceTest extends BaseTest {
    Logger                        logger = LoggerFactory.getLogger(PmphPermissionServiceTest.class);

    @Resource
    private PmphPermissionService testService;

    // @Test
    // @Rollback(Const.ISROLLBACK)
    public void test() throws Exception {
        Random r = new Random();
        PmphPermission testPar =
        new PmphPermission(new Long(r.nextInt(200)), String.valueOf(r.nextInt(200)),
                           String.valueOf(r.nextInt(200)), String.valueOf(r.nextInt(200)),
                           String.valueOf(r.nextInt(200)), true, String.valueOf(r.nextInt(200)),
                           r.nextInt(200), null, null);
        logger.info("---PmphPermissionService 测试---------------------------------------------------------------------------------");
        // 新增
        testService.addPmphPermission(testPar);
        logger.info(testPar.toString());
        // 修改
        testPar.setMenuName(String.valueOf(r.nextInt(200)));
        logger.info(testService.updatePmphPermissionById(testPar).toString());
        // 删除
        logger.info(testService.deletePmphPermissionById(new PmphPermission((1L))).toString());
        // 查询
        logger.info(testService.getPmphPermissionById(new PmphPermission((2L))).toString());

    }

}