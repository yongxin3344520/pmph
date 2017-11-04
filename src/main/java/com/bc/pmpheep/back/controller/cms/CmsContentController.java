package com.bc.pmpheep.back.controller.cms;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bc.pmpheep.back.plugin.PageParameter;
import com.bc.pmpheep.back.po.CmsContent;
import com.bc.pmpheep.back.service.CmsContentService;
import com.bc.pmpheep.back.vo.CmsContentVO;
import com.bc.pmpheep.controller.bean.ResponseBean;

/**
 * 
 * <pre>
 * 功能描述：CMS 控制器
 * 使用示范：
 * 
 * 
 * @author (作者) nyz
 * 
 * @since (该版本支持的JDK版本) ：JDK 1.6或以上
 * @version (版本) 1.0
 * @date (开发日期) 2017-10-31
 * @modify (最后修改时间) 
 * @修改人 ：nyz 
 * @审核人 ：
 * </pre>
 */
@Controller
@RequestMapping(value = "/cms")
@SuppressWarnings({ "rawtypes", "unchecked" })
public class CmsContentController {
    @Autowired
    CmsContentService cmsContentService;

    /**
     * 
     * <pre>
     * 功能描述：分页查询条件查询CmsContent 内容发布列表
     * 使用示范：
     *
     * @param pageNumber 当前页
     * @param pageSize 页面数据条数
     * @param cmsContentVO 
     * @param sessionId
     * @return 分页数据集
     * </pre>
     */
    @RequestMapping(value = "/contents", method = RequestMethod.GET)
    @ResponseBody
    public ResponseBean listCmsContent(
    @RequestParam(name = "pageNumber", defaultValue = "1") Integer pageNumber,
    @RequestParam(name = "pageSize") Integer pageSize, CmsContentVO cmsContentVO,
    @RequestParam("sessionId") String sessionId) {
        PageParameter<CmsContentVO> pageParameter =
        new PageParameter<CmsContentVO>(pageNumber, pageSize, cmsContentVO);
        return new ResponseBean(cmsContentService.listCmsContent(pageParameter, sessionId));
    }

    /**
     * 
     * <pre>
     * 功能描述：CMS-添加内容
     * 使用示范：
     *
     * @param cmsContent CmsContent对象
     * @param files 上传附件数组
     * @param content 内容信息
     * @param scheduledTime 定时发布时间
     * @param sessionId sessionId
     * @param loginType 用户类型
     * @return CmsContent 对象
     * </pre>
     */
    @ResponseBody
    @RequestMapping(value = "/content/new", method = RequestMethod.POST)
    public ResponseBean saveCmsContent(CmsContent cmsContent, @RequestParam("file") String[] files,
    @RequestParam("content") String content, @RequestParam("scheduledTime") String scheduledTime,
    @RequestParam("sessionId") String sessionId) {
        try {
            return new ResponseBean(cmsContentService.addCmsContent(cmsContent,
                                                                    files,
                                                                    content,
                                                                    scheduledTime,
                                                                    sessionId));
        } catch (IOException e) {
            return new ResponseBean(e);
        }
    }

    /**
     * 
     * <pre>
     * 功能描述：内容发布
     * 使用示范：
     *
     * @param id 主键ID
     * @return  影响行数
     * </pre>
     */
    @ResponseBody
    @RequestMapping(value = "/content/{id}/publish", method = RequestMethod.PUT)
    public ResponseBean publishCmsContent(@PathVariable("id") Long id) {
        return new ResponseBean(cmsContentService.publishCmsContentById(id));
    }

    /**
     * 
     * <pre>
     * 功能描述：点击标题查看内容
     * 使用示范：
     *
     * @param id CmsContent_id 主键
     * @return Map<String,Object>
     * </pre>
     */
    @ResponseBody
    @RequestMapping(value = "/content/{id}/detail", method = RequestMethod.GET)
    public ResponseBean searchCmsContent(@PathVariable("id") Long id) {
        return new ResponseBean(cmsContentService.getCmsContentAndContentAndAttachmentById(id));
    }

    /**
     * 
     * <pre>
     * 功能描述：内容修改页面查找带回
     * 使用示范：
     *
     * @param id 主键ID
     * @return Map<String,Object>
     * </pre>
     */
    @ResponseBody
    @RequestMapping(value = "/content/{id}", method = RequestMethod.GET)
    public ResponseBean getCmsContentAndContentAndAttachment(@PathVariable("id") Long id) {
        return new ResponseBean(cmsContentService.getCmsContentById(id));
    }

    /**
     * 
     * <pre>
     * 功能描述：内容修改
     * 使用示范：
     *
     * @param id 主键ID
     * @return 影响行数
     * </pre>
     */
    @ResponseBody
    @RequestMapping(value = "/content/update", method = RequestMethod.PUT)
    public ResponseBean updateCmsContent(CmsContent cmsContent,
    @RequestParam("file") String[] files, @RequestParam("content") String content,
    @RequestParam("attachment") String[] attachment,
    @RequestParam("scheduledTime") String scheduledTime, @RequestParam("sessionId") String sessionId) {
        try {
            return new ResponseBean(cmsContentService.updateCmsContent(cmsContent,
                                                                       files,
                                                                       content,
                                                                       attachment,
                                                                       scheduledTime,
                                                                       sessionId));
        } catch (IOException e) {
            return new ResponseBean(e);
        }
    }

    /**
     * 
     * <pre>
     * 功能描述：内容隐藏
     * 使用示范：
     *
     * @param id 主键ID
     * @return 影响行数
     * </pre>
     */
    @ResponseBody
    @RequestMapping(value = "/content/{id}/hide", method = RequestMethod.PUT)
    public ResponseBean hideCmsContent(@PathVariable("id") Long id) {
        return new ResponseBean(cmsContentService.hideCmsContentById(id));
    }

    /**
     * 
     * <pre>
     * 功能描述：内容删除
     * 使用示范：
     *
     * @param id 主键ID
     * @return 影响行数
     * </pre>
     */
    @ResponseBody
    @RequestMapping(value = "/content/{id}", method = RequestMethod.DELETE)
    public ResponseBean delCmsContent(@PathVariable("id") Long id) {
        return new ResponseBean(cmsContentService.deleteCmsContentById(id));
    }
}
