package com.bc.pmpheep.back.service.test;
import java.util.Random;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.annotation.Rollback;

import com.bc.pmpheep.back.dao.MaterialContactDao;
import com.bc.pmpheep.back.po.MaterialContact;
import com.bc.pmpheep.test.BaseTest;
import com.bc.pmpheep.back.service.MaterialContactService;
import com.bc.pmpheep.back.util.Const;
/**
 * AreaDao 单元测试
 *
 * @author mryang
 */
public class MaterialContactServiceTest extends BaseTest {
	Logger logger = LoggerFactory.getLogger(MaterialContactServiceTest.class);
	
	@Resource
	private MaterialContactService materialContactService;
	@Resource
	MaterialContactDao materialContactDao;
	
	Random r =new Random();
	MaterialContact materialContact=new MaterialContact(new Long(r.nextInt(200)),new Long(r.nextInt(200)),"contactUserName", "contactPhone", "contactEmai",1);
	
    @Test
    @Rollback(Const.ISROLLBACK) 
    public void testAddMaterialContact() {
    	Long num = materialContactDao.getMaterialContactCount();
    	logger.info("一共有{}", num);
    	//新增
    	materialContactService.addMaterialContact(materialContact);
    	Assert.assertTrue("添加失败",materialContact.getId() > 0 );
    }
    @Test
    @Rollback(Const.ISROLLBACK) 
    public void testUpdateMaterialContact() {
    	materialContactService.addMaterialContact(materialContact);
    	//修改
    	materialContact.setContactUserName(String.valueOf(r.nextInt(200)));
    	Assert.assertTrue("更新失败",materialContactService.updateMaterialContact(materialContact) > 0 );
    	//修改
    	materialContact.setContactUserName(String.valueOf(r.nextInt(200)));
    	materialContact.setId((long) r.nextInt(200));
    	Assert.assertTrue("更新失败",materialContactService.updateMaterialContact(materialContact) >= 0 );
    }
    
    @Test
    @Rollback(Const.ISROLLBACK) 
    public void testDeleteMaterialContactById() {
    	//删除
    	Assert.assertTrue("删除失败",materialContactService.deleteMaterialContactById(2L) >= 0 );
    }
    @Test
    @Rollback(Const.ISROLLBACK) 
    public void testDeleteMaterialContactsByMaterialId() {
    	//删除
    	Assert.assertTrue("删除失败",materialContactService.deleteMaterialContactsByMaterialId(30L) >= 0 );
    }
    @Test
    @Rollback(Const.ISROLLBACK) 
    public void testGetMaterialContactById() {
    	materialContactService.addMaterialContact(materialContact);
    	MaterialContact  materialContact = materialContactService.getMaterialContactById(this.materialContact.getId());
    	//查询
    	Assert.assertNotNull("获取数据失败",materialContact);
    	materialContact = materialContactService.getMaterialContactById(222L);
    }
}





