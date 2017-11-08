package com.bc.pmpheep.back.controller.cms;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bc.pmpheep.back.plugin.PageParameter;
import com.bc.pmpheep.back.service.CmsContentService;
import com.bc.pmpheep.back.vo.CmsContentVO;
import com.bc.pmpheep.controller.bean.ResponseBean;

/**
 * 
 * <pre>
 * 功能描述：社外内容审核 控制器
 * 使用示范：
 * 
 * 
 * @author (作者) nyz
 * 
 * @since (该版本支持的JDK版本) ：JDK 1.6或以上
 * @version (版本) 1.0
 * @date (开发日期) 2017-11-2
 * @modify (最后修改时间) 
 * @修改人 ：nyz 
 * @审核人 ：
 * </pre>
 */
@Controller
@RequestMapping(value = "/cms")
@SuppressWarnings({ "rawtypes", "unchecked" })
public class CmsContentCheckController {
    @Autowired
    CmsContentService cmsContentService;

    /**
     * 
     * <pre>
     * 功能描述：分页查询条件查询《社外内容审核》列表
     * 使用示范：
     *
     * @param pageNumber 当前页
     * @param pageSize 页面数据条数
     * @param cmsContentVO
     * @param sessionId
     * @return 分页数据集
     * </pre>
     */
    @ResponseBody
    @RequestMapping(value = "/check", method = RequestMethod.GET)
    public ResponseBean listContentCheck(
    @RequestParam(name = "pageNumber", defaultValue = "1") Integer pageNumber,
    @RequestParam(name = "pageSize") Integer pageSize, CmsContentVO cmsContentVO,
    @RequestParam("sessionId") String sessionId) {
        PageParameter<CmsContentVO> pageParameter =
        new PageParameter<CmsContentVO>(pageNumber, pageSize, cmsContentVO);
        return new ResponseBean(cmsContentService.listContentCheck(pageParameter, sessionId));
    }

    /**
     * 
     * <pre>
     * 功能描述：社外内容审核操作(通过/拒绝)
     * 使用示范：
     *
     * @param id 主键ID
     * @param authStatus 审核状态
     * @return 影响行数
     * </pre>
     */
    @ResponseBody
    @RequestMapping(value = "/check/content", method = RequestMethod.PUT)
    public ResponseBean updateCheckContent(@RequestParam("id") Long id,
    @RequestParam("authStatus") Short authStatus, @RequestParam("sessionId") String sessionId) {
        return new ResponseBean(cmsContentService.checkContentById(id, authStatus, sessionId));
    }

    /**
     * 
     * <pre>
     * 功能描述：社外内容审核操作(批量逻辑删除)
     * 使用示范：
     *
     * @param ids 主键ID集合
     * @return 影响行数
     * </pre>
     */
    @ResponseBody
    @RequestMapping(value = "/check/content", method = RequestMethod.DELETE)
    public ResponseBean updateContentByIds(@RequestParam("ids") List<Long> ids) {
        return new ResponseBean(cmsContentService.deleteCmsContentByIds(ids));
    }
}