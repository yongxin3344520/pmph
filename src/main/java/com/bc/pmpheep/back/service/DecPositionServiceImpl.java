/**
 * 
 */
package com.bc.pmpheep.back.service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bc.pmpheep.back.dao.DecPositionDao;
import com.bc.pmpheep.back.plugin.PageParameter;
import com.bc.pmpheep.back.plugin.PageResult;
import com.bc.pmpheep.back.po.DecPosition;
import com.bc.pmpheep.back.po.DecPositionPublished;
import com.bc.pmpheep.back.po.Material;
import com.bc.pmpheep.back.po.PmphUser;
import com.bc.pmpheep.back.po.Textbook;
import com.bc.pmpheep.back.service.common.SystemMessageService;
import com.bc.pmpheep.back.util.ArrayUtil;
import com.bc.pmpheep.back.util.CollectionUtil;
import com.bc.pmpheep.back.util.JsonUtil;
import com.bc.pmpheep.back.util.ObjectUtil;
import com.bc.pmpheep.back.util.PageParameterUitl;
import com.bc.pmpheep.back.util.SessionUtil;
import com.bc.pmpheep.back.util.StringUtil;
import com.bc.pmpheep.back.vo.DecPositionEditorSelectionVO;
import com.bc.pmpheep.back.vo.DecPositionVO;
import com.bc.pmpheep.back.vo.DeclarationCountVO;
import com.bc.pmpheep.back.vo.DeclarationResultBookVO;
import com.bc.pmpheep.back.vo.DeclarationResultSchoolVO;
import com.bc.pmpheep.back.vo.DeclarationSituationBookResultVO;
import com.bc.pmpheep.back.vo.DeclarationSituationSchoolResultVO;
import com.bc.pmpheep.back.vo.NewDecPosition;
import com.bc.pmpheep.back.vo.TextBookDecPositionVO;
import com.bc.pmpheep.back.vo.TextbookDecVO;
import com.bc.pmpheep.general.bean.FileType;
import com.bc.pmpheep.general.service.FileService;
import com.bc.pmpheep.service.exception.CheckedExceptionBusiness;
import com.bc.pmpheep.service.exception.CheckedExceptionResult;
import com.bc.pmpheep.service.exception.CheckedServiceException;

/**
 * <p>
 * Title:DecPositionServiceImpl
 * <p>
 * <p>
 * Description:作家职位遴选信息
 * <p>
 * 
 * @author lyc
 * @date 2017年10月9日 下午6:05:54
 */
@Service
public class DecPositionServiceImpl implements DecPositionService {

    @Autowired
    private DecPositionDao              decPositionDao;
    @Autowired
    private FileService                 fileService;
    @Autowired
    private DecPositionService          decPositionService;
    @Autowired
    private TextbookLogService          textbookLogService;
    @Autowired
    private TextbookService             textbookService;
    @Autowired
    private MaterialService             materialService;
    @Autowired
    private SystemMessageService        systemMessageService;
    @Autowired
    private DecPositionPublishedService decPositionPublishedService;

    @Override
    public DecPosition addDecPosition(DecPosition decPosition) throws CheckedServiceException {
        if (null == decPosition) {
            throw new CheckedServiceException(CheckedExceptionBusiness.MATERIAL,
                                              CheckedExceptionResult.NULL_PARAM, "参数不能为空");
        }
        if (null == decPosition.getDeclarationId()) {
            throw new CheckedServiceException(CheckedExceptionBusiness.MATERIAL,
                                              CheckedExceptionResult.NULL_PARAM, "申报表id不能为空");
        }
        if (null == decPosition.getTextbookId()) {
            throw new CheckedServiceException(CheckedExceptionBusiness.MATERIAL,
                                              CheckedExceptionResult.NULL_PARAM, "书籍id不能为空");
        }
        if (null == decPosition.getPresetPosition()) {
            throw new CheckedServiceException(CheckedExceptionBusiness.MATERIAL,
                                              CheckedExceptionResult.NULL_PARAM, "申报职务不能为空");
        }
        decPositionDao.addDecPosition(decPosition);
        return decPosition;
    }

    @Override
    public Integer deleteDecPosition(Long id) throws CheckedServiceException {
        if (null == id) {
            throw new CheckedServiceException(CheckedExceptionBusiness.MATERIAL,
                                              CheckedExceptionResult.NULL_PARAM, "id不能为空");
        }
        return decPositionDao.deleteDecPosition(id);
    }

    @Override
    public Integer updateDecPosition(DecPosition decPosition) throws CheckedServiceException {
        if (null == decPosition.getId()) {
            throw new CheckedServiceException(CheckedExceptionBusiness.MATERIAL,
                                              CheckedExceptionResult.NULL_PARAM, "id不能为空");
        }
        return decPositionDao.updateDecPosition(decPosition);
    }

    @Override
    public DecPosition getDecPositionById(Long id) throws CheckedServiceException {
        if (null == id) {
            throw new CheckedServiceException(CheckedExceptionBusiness.MATERIAL,
                                              CheckedExceptionResult.NULL_PARAM, "id不能为空");
        }
        return decPositionDao.getDecPositionById(id);
    }

    @Override
    public List<DecPosition> listDecPositionsByTextbookIdAndOrgid(List<Long> textBookIds, Long orgId)
    throws CheckedServiceException {
        if (null == textBookIds || textBookIds.size() == 0 || null == orgId) {
            throw new CheckedServiceException(CheckedExceptionBusiness.MATERIAL,
                                              CheckedExceptionResult.NULL_PARAM, "参数为空");
        }
        for (Long bookId : textBookIds) {
            if (null == bookId) {
                throw new CheckedServiceException(CheckedExceptionBusiness.MATERIAL,
                                                  CheckedExceptionResult.NULL_PARAM, "书籍参数为空");
            }
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("list", textBookIds); // 书籍ids
        map.put("orgId", orgId); // 网址类型机构
        return decPositionDao.listDecPositionsByTextbookIdAndOrgid(map);
    }

    @Override
    public List<DecPosition> listDecPositions(Long declarationId) throws CheckedServiceException {
        if (null == declarationId) {
            throw new CheckedServiceException(CheckedExceptionBusiness.MATERIAL,
                                              CheckedExceptionResult.NULL_PARAM, "申报表id不能为空");
        }
        return decPositionDao.listDecPositions(declarationId);
    }

    @Override
    public List<DecPosition> listDecPositionsByTextbookId(Long textbookId)
    throws CheckedServiceException {
        if (null == textbookId) {
            throw new CheckedServiceException(CheckedExceptionBusiness.MATERIAL,
                                              CheckedExceptionResult.NULL_PARAM, "书籍id不能为空");
        }
        return decPositionDao.listDecPositionsByTextbookId(textbookId);
    }

    @Override
    public List<DecPosition> listDecPositionsByTextBookIds(List<Long> textbookIds)
    throws CheckedServiceException {
        if (null == textbookIds || textbookIds.size() == 0) {
            throw new CheckedServiceException(CheckedExceptionBusiness.MATERIAL,
                                              CheckedExceptionResult.NULL_PARAM, "参数为空");
        }
        for (Long bookId : textbookIds) {
            if (null == bookId) {
                throw new CheckedServiceException(CheckedExceptionBusiness.MATERIAL,
                                                  CheckedExceptionResult.NULL_PARAM, "有书籍id为空");
            }
        }
        return decPositionDao.listDecPositionsByTextBookIds(textbookIds);
    }

    @Override
    public List<DecPosition> listChosenDecPositionsByTextbookId(Long textbookId)
    throws CheckedServiceException {
        if (null == textbookId) {
            throw new CheckedServiceException(CheckedExceptionBusiness.MATERIAL,
                                              CheckedExceptionResult.NULL_PARAM, "书籍id不能为空");
        }
        return decPositionDao.listChosenDecPositionsByTextbookId(textbookId);
    }

    @Override
    public List<Long> listDecPositionsByTextbookIds(String[] textbookIds)
    throws CheckedServiceException {
        if (0 == textbookIds.length) {
            throw new CheckedServiceException(CheckedExceptionBusiness.MATERIAL,
                                              CheckedExceptionResult.NULL_PARAM, "书籍id不能为空");
        }
        return decPositionDao.listDecPositionsByTextbookIds(textbookIds);
    }

    @Override
    public long saveBooks(DecPositionVO decPositionVO) throws IOException {
        List<NewDecPosition> list = decPositionVO.getList();
        if (CollectionUtil.isEmpty(list)) {
            throw new CheckedServiceException(CheckedExceptionBusiness.MATERIAL,
                                              CheckedExceptionResult.NULL_PARAM, "添加内容不能为空");
        }
        List<DecPosition> istDecPositions =
        decPositionDao.listDecPositions(list.get(0).getDeclarationId());
        String newId = ",";
        for (NewDecPosition newDecPosition : list) { // 遍历所有的id
            newId += newDecPosition.getId() + ",";
        }
        for (DecPosition DecPosition : istDecPositions) { // 遍历原数据
            if (!newId.contains("," + DecPosition.getId() + ",")) {
                if (ObjectUtil.notNull(DecPosition.getId())) {
                    decPositionDao.deleteDecPosition(DecPosition.getId());
                }
            }
        }
        for (NewDecPosition newDecPosition : list) {
            Long id = newDecPosition.getId();
            Long declarationId = newDecPosition.getDeclarationId();
            Long textbookId = newDecPosition.getTextbookId();
            String file = newDecPosition.getFile();
            String showPosition = newDecPosition.getShowPosition();
            DecPosition decPosition = new DecPosition();
            if ("编委".equals(showPosition)) {
                decPosition.setPresetPosition(1);
            } else if ("副主编".equals(showPosition)) {
                decPosition.setPresetPosition(2);
            } else if ("副主编,编委".equals(showPosition)) {
                decPosition.setPresetPosition(3);
            } else if ("主编".equals(showPosition)) {
                decPosition.setPresetPosition(4);
            } else if ("主编,编委".equals(showPosition)) {
                decPosition.setPresetPosition(5);
            } else if ("主编,副主编".equals(showPosition)) {
                decPosition.setPresetPosition(6);
            } else if ("主编,副主编,编委".equals(showPosition)) {
                decPosition.setPresetPosition(7);
            } else if ("数字编委".equals(showPosition)) {
                decPosition.setPresetPosition(8);
            } else if ("编委,数字编委".equals(showPosition)) {
                decPosition.setPresetPosition(9);
            } else if ("副主编,数字编委".equals(showPosition)) {
                decPosition.setPresetPosition(10);
            } else if ("副主编,编委,数字编委".equals(showPosition)) {
                decPosition.setPresetPosition(11);
            } else if ("主编,数字编委".equals(showPosition)) {
                decPosition.setPresetPosition(12);
            } else if ("主编,编委,数字编委".equals(showPosition)) {
                decPosition.setPresetPosition(13);
            } else if ("主编,副主编,数字编委".equals(showPosition)) {
                decPosition.setPresetPosition(14);
            } else if ("主编,副主编,编委,数字编委".equals(showPosition)) {
                decPosition.setPresetPosition(15);
            }
            File files = null;
            if (StringUtil.isEmpty(file)) {
                decPosition.setSyllabusId(null);
                decPosition.setSyllabusName(null);
            } else {
                files = new File(file);
                if (files.exists()) {
                    String fileName = files.getName(); // 获取原文件名字
                    decPosition.setSyllabusName(fileName);
                } else {
                    decPosition.setSyllabusId(null);
                    decPosition.setSyllabusName(null);
                }
            }
            decPosition.setDeclarationId(declarationId);
            decPosition.setTextbookId(textbookId);
            decPosition.setId(id);
            if (ObjectUtil.isNull(id)) { // 保存或者修改
                decPositionDao.addDecPosition(decPosition);
                String mongoId = null;
                if (ObjectUtil.notNull(decPosition.getId()) && StringUtil.notEmpty(file)) {
                    mongoId =
                    fileService.saveLocalFile(files, FileType.SYLLABUS, decPosition.getId());
                }
                if (StringUtil.notEmpty(mongoId)) {
                    decPosition.setSyllabusId(mongoId);
                    decPositionDao.updateDecPosition(decPosition);
                }
            } else {
                decPositionDao.updateDecPosition(decPosition);
            }
        }
        return list.size();
    }

    @Override
    public Map<String, Object> listEditorSelection(Long textbookId, Long materialId,
    String realName, Integer presetPosition) throws CheckedServiceException {
        if (ObjectUtil.isNull(textbookId)) {
            throw new CheckedServiceException(CheckedExceptionBusiness.MATERIAL,
                                              CheckedExceptionResult.NULL_PARAM, "书籍id不能为空");
        }
        if (ObjectUtil.isNull(materialId)) {
            throw new CheckedServiceException(CheckedExceptionBusiness.MATERIAL,
                                              CheckedExceptionResult.NULL_PARAM, "教材id不能为空");
        }
        Map<String, Object> resultMap = new HashMap<String, Object>();
        List<DecPositionEditorSelectionVO> listEditorSelectionVOs =
        decPositionDao.listEditorSelection(textbookId,
                                           StringUtil.toAllCheck(realName),
                                           presetPosition);
        // 排序
        List<DecPositionEditorSelectionVO> selectedDecPositionEditorSelectionVOs =
        new ArrayList<DecPositionEditorSelectionVO>();// 已遴选集合
        List<DecPositionEditorSelectionVO> unselectedDecPositionEditorSelectionVOs =
        new ArrayList<DecPositionEditorSelectionVO>();// 未遴选集合
        for (DecPositionEditorSelectionVO de : listEditorSelectionVOs) {
            if (ObjectUtil.notNull(de.getRank())) {
                selectedDecPositionEditorSelectionVOs.add(de);
            } else if (de.getChosenPosition() > 0) {
                selectedDecPositionEditorSelectionVOs.add(de);
            } else {
                unselectedDecPositionEditorSelectionVOs.add(de);
            }
        }
        List<DecPositionEditorSelectionVO> editorList =
        new ArrayList<DecPositionEditorSelectionVO>(selectedDecPositionEditorSelectionVOs.size());// 已遴选主编集合
        List<DecPositionEditorSelectionVO> subeditorList =
        new ArrayList<DecPositionEditorSelectionVO>(selectedDecPositionEditorSelectionVOs.size());// 已遴选副主编集合
        List<DecPositionEditorSelectionVO> editorialMemberList =
        new ArrayList<DecPositionEditorSelectionVO>(selectedDecPositionEditorSelectionVOs.size());// 已遴选编委集合
        List<DecPositionEditorSelectionVO> digitalrList =
        new ArrayList<DecPositionEditorSelectionVO>(selectedDecPositionEditorSelectionVOs.size());// 已遴选数字编委集合
        for (DecPositionEditorSelectionVO decVo : selectedDecPositionEditorSelectionVOs) {
            if (4 == decVo.getChosenPosition() || 12 == decVo.getChosenPosition() ) {// 主编          1100 0100
                editorList.add(decVo);
            } else if (2 == decVo.getChosenPosition() || 10 == decVo.getChosenPosition() ) {// 副主编  1010 0010
                subeditorList.add(decVo);
            } else if (1 == decVo.getChosenPosition() || 9  == decVo.getChosenPosition() ) {// 编委    1001 0001 
                editorialMemberList.add(decVo);
            } else if (8 == decVo.getChosenPosition()) {// 数字编委  1000
                digitalrList.add(decVo);
            }
        }
        Collections.sort(editorList, new Comparator<DecPositionEditorSelectionVO>() {
            public int compare(DecPositionEditorSelectionVO arg0, DecPositionEditorSelectionVO arg1) {
                return arg0.getRank().compareTo(arg1.getRank());
            }
        });

        Collections.sort(subeditorList, new Comparator<DecPositionEditorSelectionVO>() {
            public int compare(DecPositionEditorSelectionVO arg0, DecPositionEditorSelectionVO arg1) {
                return arg0.getRank().compareTo(arg1.getRank());
            }
        });
        Collections.sort(unselectedDecPositionEditorSelectionVOs,
                         new Comparator<DecPositionEditorSelectionVO>() {
                             public int compare(DecPositionEditorSelectionVO arg0,
                             DecPositionEditorSelectionVO arg1) {
                                 return arg1.getPresetPosition()
                                            .compareTo(arg0.getPresetPosition());
                             }
                         });
        List<DecPositionEditorSelectionVO> newDecPositionEditorSelectionVOs =
        new ArrayList<DecPositionEditorSelectionVO>(listEditorSelectionVOs.size());// 重新排序后的集合
        newDecPositionEditorSelectionVOs.addAll(editorList);
        newDecPositionEditorSelectionVOs.addAll(subeditorList);
        newDecPositionEditorSelectionVOs.addAll(editorialMemberList);
        newDecPositionEditorSelectionVOs.addAll(digitalrList);
        newDecPositionEditorSelectionVOs.addAll(unselectedDecPositionEditorSelectionVOs);
        resultMap.put("DecPositionEditorSelectionVO", newDecPositionEditorSelectionVOs);
        Material material = materialService.getMaterialById(materialId);
        resultMap.put("IsDigitalEditorOptional", material.getIsDigitalEditorOptional());
        return resultMap;
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Integer updateDecPositionEditorSelection(String jsonDecPosition, Integer selectionType,
    Integer editorOrSubeditorType, String sessionId) throws CheckedServiceException, IOException {
        if (StringUtil.isEmpty(jsonDecPosition)) {
            throw new CheckedServiceException(CheckedExceptionBusiness.MATERIAL,
                                              CheckedExceptionResult.NULL_PARAM, "遴选职位不能为空");
        }
        PmphUser pmphUser = SessionUtil.getPmphUserBySessionId(sessionId);
        if (ObjectUtil.isNull(pmphUser)) {
            throw new CheckedServiceException(CheckedExceptionBusiness.MATERIAL,
                                              CheckedExceptionResult.NULL_PARAM, "用户为空");
        }
        if (ObjectUtil.isNull(pmphUser.getId())) {
            throw new CheckedServiceException(CheckedExceptionBusiness.MATERIAL,
                                              CheckedExceptionResult.NULL_PARAM, "用户为空");
        }
        Integer count = 0;
        List<DecPosition> decPositions =
        new JsonUtil().getArrayListObjectFromStr(DecPosition.class, jsonDecPosition);// json字符串转List对象集合
        if (CollectionUtil.isEmpty(decPositions)) {
            throw new CheckedServiceException(CheckedExceptionBusiness.MATERIAL,
                                              CheckedExceptionResult.NULL_PARAM, "遴选职位为空");
        }
        // (1:确定，2：发布)
        Integer selectionType_1 = 1;
        Integer selectionType_2 = 2;
        // 获取书籍id
        Long textbookId = decPositions.get(0).getTextbookId();
        // 原来的历史遴选记录
        List<DecPosition> oldlist =
        decPositionService.listChosenDecPositionsByTextbookId(textbookId);
        // 1:确定
        if (selectionType_1.intValue() == selectionType.intValue()) {
            // 查询书籍下所有申报id
            List<Long> ids =
            decPositionService.getDecPositionIdByBookId(textbookId, editorOrSubeditorType);
            if (2 == editorOrSubeditorType) {
                List<Long> newEditorialMemberIds = new ArrayList<Long>();// 遴选的编委ID
                for (DecPosition decPosition : decPositions) {
                    if (1 == decPosition.getChosenPosition()
                        || 8 == decPosition.getChosenPosition()) {
                        if (!ids.contains(decPosition.getId())) {
                            newEditorialMemberIds.add(decPosition.getId());
                        }
                    }
                }
                if (newEditorialMemberIds.isEmpty()) {
                    Textbook textbook = textbookService.getTextbookById(textbookId);
                    if (0 != textbook.getRevisionTimes()) {
                        textbookService.updatRevisionTimesByTextBookId(-1, textbookId);
                    }
                } else {
                    textbookService.updatRevisionTimesByTextBookId(1, textbookId);
                }

            }

            // 初始化作家职位申报表
            if (CollectionUtil.isNotEmpty(ids)) {
                decPositionService.updateDecPositionSetDefault(ids, editorOrSubeditorType);
            }
            if (CollectionUtil.isNotEmpty(decPositions)) {
                count = decPositionDao.updateDecPositionEditorSelection(decPositions);
            }
            if (ObjectUtil.isNull(textbookId)) {
                throw new CheckedServiceException(CheckedExceptionBusiness.MATERIAL,
                                                  CheckedExceptionResult.NULL_PARAM, "书籍id为空");
            }
            Integer userType = 1;// 用户类型
            Long updaterId = pmphUser.getId(); // 获取修改者id
            // 添加新的遴选记录
            textbookLogService.addTextbookLog(oldlist, textbookId, updaterId, userType);
        }
        // 2：发布
        if (selectionType_2.intValue() == selectionType.intValue()) {
//            /*** 判断是否确认开始 ***/
//            List<DecPosition> decPosition1 =
//            decPositionDao.listChosenDecPositionsByTextbookId(textbookId);
//            // 筛选出主编，副主编
//            List<DecPosition> decPosition2 = new ArrayList<DecPosition>();
//            for (DecPosition item : decPosition1) {
//                if (null != item
//                    && null != item.getChosenPosition()
//                    && (item.getChosenPosition() == 4 || item.getChosenPosition() == 12
//                        || item.getChosenPosition() == 2 || item.getChosenPosition() == 10)) {
//                    decPosition2.add(item);
//                }
//            }
//            List<DecPosition> decPositions2 = new ArrayList<DecPosition>();
//            for (DecPosition decPosition : decPositions) {
//                if (null != decPosition
//                    && null != decPosition.getChosenPosition()
//                    && (decPosition.getChosenPosition() == 4
//                        || decPosition.getChosenPosition() == 12
//                        || decPosition.getChosenPosition() == 2 || decPosition.getChosenPosition() == 10)) {
//                    decPositions2.add(decPosition);
//                }
//            }
//            if (decPosition2.size() != decPositions2.size()) {
//                throw new CheckedServiceException(CheckedExceptionBusiness.MATERIAL,
//                                                  CheckedExceptionResult.ILLEGAL_PARAM,
//                                                  "还未确认主编/副主编,不能发布");
//            }
//            // 排序 按照 id 升排列
//            DecPosition[] decPosition3 = decPosition2.toArray(new DecPosition[] {});
//            for (int i = 0; i < decPosition3.length - 1; i++) {
//                for (int j = i + 1; j < decPosition3.length; j++) {
//                    // 前面一个
//                    DecPosition item1 = decPosition3[i];
//                    DecPosition item2 = decPosition3[j];
//                    if (item2.getId() < item1.getId()) {
//                        // 把小的先存起来
//                        DecPosition temp = decPosition3[j];
//                        // 交换位置
//                        decPosition3[j] = decPosition3[i];
//                        decPosition3[i] = temp;
//                    }
//                }
//            }
//            DecPosition[] decPositions3 = decPositions2.toArray(new DecPosition[] {});
//            for (int i = 0; i < decPositions3.length - 1; i++) {
//                for (int j = i + 1; j < decPositions3.length; j++) {
//                    // 前面一个
//                    DecPosition item1 = decPositions3[i];
//                    DecPosition item2 = decPositions3[j];
//                    if (item2.getId() < item1.getId()) {
//                        // 把小的先存起来
//                        DecPosition temp = decPositions3[j];
//                        // 交换位置
//                        decPositions3[j] = decPositions3[i];
//                        decPositions3[i] = temp;
//                    }
//                }
//            }
//            // 一一对比
//            for (int i = 0; i < decPosition3.length; i++) {
//                DecPosition item1 = decPosition3[i];
//                DecPosition item2 = decPositions3[i];
//                if (item1.getId().intValue() != item2.getId().intValue()) {
//                    throw new CheckedServiceException(CheckedExceptionBusiness.MATERIAL,
//                                                      CheckedExceptionResult.ILLEGAL_PARAM,
//                                                      "还未确认主编/副主编,不能发布");
//                }
//                if (item1.getRank().intValue() != item2.getRank().intValue()) {
//                    throw new CheckedServiceException(CheckedExceptionBusiness.MATERIAL,
//                                                      CheckedExceptionResult.ILLEGAL_PARAM,
//                                                      "还未确认主编/副主编,不能发布");
//                }
//                int chose1 = item1.getChosenPosition().intValue();
//                int chose2 = item2.getChosenPosition().intValue();
//                chose1 = chose1 > 8 ? chose1 - 8 : chose1;
//                chose2 = chose2 > 8 ? chose2 - 8 : chose2;
//                if (chose1 != chose2) {
//                    throw new CheckedServiceException(CheckedExceptionBusiness.MATERIAL,
//                                                      CheckedExceptionResult.ILLEGAL_PARAM,
//                                                      "还未确认主编/副主编,不能发布");
//                }
//            }
//            /*** 判断是否确认结束 ***/
        	//发布的时候 先确认
        	this.updateDecPositionEditorSelection(jsonDecPosition, 1 , editorOrSubeditorType, sessionId);
        	
            if (ObjectUtil.isNull(textbookId)) {
                throw new CheckedServiceException(CheckedExceptionBusiness.MATERIAL,
                                                  CheckedExceptionResult.NULL_PARAM, "书籍id为空");
            }
            // 获取当前书籍书申报信息(包含没有被遴选上的)
            List<DecPosition> decPositionsList =
            decPositionService.listDecPositionsByTextBookIds(new ArrayList<Long>(
                                                                                 Arrays.asList(textbookId)));
            if (CollectionUtil.isEmpty(decPositionsList)) {
                throw new CheckedServiceException(CheckedExceptionBusiness.MATERIAL,
                                                  CheckedExceptionResult.NULL_PARAM,
                                                  "当前书籍还未遴选主编，副主编");
            }
            // DecPositionPublished对象集合
            List<DecPositionPublished> decPositionPublisheds =
            new ArrayList<DecPositionPublished>(decPositionsList.size());
            for (DecPosition decPosition : decPositionsList) {
                if (null == decPosition || null == decPosition.getChosenPosition()
                    || decPosition.getChosenPosition().intValue() <= 0) {
                    continue;
                }
                // 筛选出遴选上的人员
                decPositionPublisheds.add(new DecPositionPublished(pmphUser.getId(),
                                                                   decPosition.getDeclarationId(),
                                                                   textbookId,
                                                                   decPosition.getPresetPosition(),
                                                                   decPosition.getChosenPosition(),
                                                                   decPosition.getRank(),
                                                                   decPosition.getSyllabusId(),
                                                                   decPosition.getSyllabusName()));
            }
            decPositionPublishedService.deleteDecPositionPublishedByTextBookId(textbookId);// 先删除当前发布人已发布的
            decPositionPublishedService.batchInsertDecPositionPublished(decPositionPublisheds);// 再添加
            // 发布时更新textbook表中is_chief_published（是否已公布主编/副主编）字段
            count = textbookService.updateTextbook(new Textbook(textbookId, true));
            if (count > 0) {
                // 发送消息
                systemMessageService.sendWhenConfirmFirstEditor(textbookId);
            }
        }
        return count;
    }

    @Override
    public PageResult<DeclarationSituationSchoolResultVO> listChosenDeclarationSituationSchoolResultVOs(
    PageParameter<DeclarationSituationSchoolResultVO> pageParameter) throws CheckedServiceException {
        if (ObjectUtil.isNull(pageParameter.getParameter().getMaterialId())) {
            throw new CheckedServiceException(CheckedExceptionBusiness.MATERIAL,
                                              CheckedExceptionResult.NULL_PARAM, "教材id不能为空");
        }
        // 如果机构名称不为空，则为模糊查询
        String schoolName = pageParameter.getParameter().getSchoolName();
        if (StringUtil.notEmpty(schoolName)) {
            pageParameter.getParameter().setSchoolName(schoolName);
        }
        PageResult<DeclarationSituationSchoolResultVO> pageResult =
        new PageResult<DeclarationSituationSchoolResultVO>();
        PageParameterUitl.CopyPageParameter(pageParameter, pageResult);
        // 得到申报单位的总数
        int total = decPositionDao.getSchoolCount(pageParameter.getParameter().getMaterialId());
        if (total > 0) {
            List<DeclarationSituationSchoolResultVO> declarationSituationSchoolResultVOs =
            decPositionDao.getSchoolResultChosen(pageParameter);
            List<DeclarationSituationSchoolResultVO> list = new ArrayList<>();
            for (DeclarationSituationSchoolResultVO declarationSituationSchoolResultVO : declarationSituationSchoolResultVOs) {
                // 计算申报人数
                Integer presetPersons =
                declarationSituationSchoolResultVO.getPresetPositionEditor()
                + declarationSituationSchoolResultVO.getPresetPositionSubeditor()
                + declarationSituationSchoolResultVO.getPresetPositionEditorial()
                + declarationSituationSchoolResultVO.getPresetDigitalEditor();
                // 计算当选人数
                Integer chosenPersons =
                declarationSituationSchoolResultVO.getChosenPositionEditor()
                + declarationSituationSchoolResultVO.getChosenPositionSubeditor()
                + declarationSituationSchoolResultVO.getChosenPositionEditorial()
                + declarationSituationSchoolResultVO.getIsDigitalEditor();
                declarationSituationSchoolResultVO.setPresetPersons(presetPersons);
                declarationSituationSchoolResultVO.setChosenPersons(chosenPersons);
                declarationSituationSchoolResultVO.setState(1);
                list.add(declarationSituationSchoolResultVO);
            }
            pageResult.setRows(list);
            pageResult.setTotal(total);
        }
        return pageResult;
    }

    @Override
    public PageResult<DeclarationSituationSchoolResultVO> listPresetDeclarationSituationSchoolResultVOs(
    PageParameter<DeclarationSituationSchoolResultVO> pageParameter) throws CheckedServiceException {
        if (ObjectUtil.isNull(pageParameter.getParameter().getMaterialId())) {
            throw new CheckedServiceException(CheckedExceptionBusiness.MATERIAL,
                                              CheckedExceptionResult.NULL_PARAM, "教材id不能为空");
        }
        String schoolName = pageParameter.getParameter().getSchoolName();
        // 如果机构名称不为空，则为模糊查询
        if (StringUtil.notEmpty(schoolName)) {
            pageParameter.getParameter().setSchoolName(schoolName);
        }
        PageResult<DeclarationSituationSchoolResultVO> pageResult =
        new PageResult<DeclarationSituationSchoolResultVO>();
        PageParameterUitl.CopyPageParameter(pageParameter, pageResult);
        int total = decPositionDao.getSchoolCount(pageParameter.getParameter().getMaterialId());
        if (total > 0) {
            List<DeclarationSituationSchoolResultVO> declarationSituationSchoolResultVOs =
            decPositionDao.getSchoolResultPreset(pageParameter);
            List<DeclarationSituationSchoolResultVO> list = new ArrayList<>();
            for (DeclarationSituationSchoolResultVO declarationSituationSchoolResultVO : declarationSituationSchoolResultVOs) {
                // 计算申报人数
                Integer presetPersons =
                declarationSituationSchoolResultVO.getPresetPositionEditor()
                + declarationSituationSchoolResultVO.getPresetPositionSubeditor()
                + declarationSituationSchoolResultVO.getPresetPositionEditorial()
                + declarationSituationSchoolResultVO.getPresetDigitalEditor();
                // 计算当选人数
                Integer chosenPersons =
                declarationSituationSchoolResultVO.getPresetPositionEditor()
                + declarationSituationSchoolResultVO.getChosenPositionSubeditor()
                + declarationSituationSchoolResultVO.getChosenPositionEditorial()
                + declarationSituationSchoolResultVO.getIsDigitalEditor();
                declarationSituationSchoolResultVO.setPresetPersons(presetPersons);
                declarationSituationSchoolResultVO.setChosenPersons(chosenPersons);
                declarationSituationSchoolResultVO.setState(2);
                list.add(declarationSituationSchoolResultVO);
            }
            pageResult.setRows(list);
            pageResult.setTotal(total);
        }
        return pageResult;
    }

    @Override
    public DeclarationCountVO getDeclarationCountVO(Long materialId) throws CheckedServiceException {
        DeclarationCountVO declarationCountVO = new DeclarationCountVO();
        Integer schoolDeclarationCount = decPositionDao.getSchoolDeclarationCount(materialId);
        Integer schoolDeclarationChosenCount =
        decPositionDao.getSchoolDeclarationChosenCount(materialId);
        String schoolDeclarationAverage = "0";
        String schoolDeclarationChosenAverage = "0";
        if (decPositionDao.getSchoolCount(materialId) > 0) {
            /*
             * 若院校数量大于0，计算院校申报平均数
             */
            Double presetAverage =
            (double) schoolDeclarationCount / decPositionDao.getSchoolCount(materialId);
            BigDecimal bigDecimalpreset = new BigDecimal(presetAverage);
            schoolDeclarationAverage =
            String.valueOf(bigDecimalpreset.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
            /*
             * 计算当选数的平均数
             */
            Double chosenAverage =
            (double) schoolDeclarationChosenCount / decPositionDao.getSchoolCount(materialId);
            BigDecimal bigDecimalchosen = new BigDecimal(chosenAverage);
            schoolDeclarationChosenAverage =
            String.valueOf(bigDecimalchosen.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
        }
        Integer editorCount = decPositionDao.getEditorCount(materialId);
        Integer subEditorCount = decPositionDao.getSubEditorCount(materialId);
        Integer editorialCount = decPositionDao.getEditorialCount(materialId);
        Integer digitalCount = decPositionDao.getDigitalCount(materialId);
        Integer chosenEditorCount = decPositionDao.getChosenEditorCount(materialId);
        Integer chosenSubeditorCount = decPositionDao.getChosenSubeditorCount(materialId);
        Integer chosenEditorialCount = decPositionDao.getChosenEditorialCount(materialId);
        Integer chosenDigitalCount = decPositionDao.getChosenDigitalCount(materialId);
        declarationCountVO.setSchoolDeclarationCount(schoolDeclarationCount);
        declarationCountVO.setSchoolDeclarationChosenCount(schoolDeclarationChosenCount);
        declarationCountVO.setSchoolDeclarationAverage(schoolDeclarationAverage);
        declarationCountVO.setSchoolDeclarationChosenAverage(schoolDeclarationChosenAverage);
        declarationCountVO.setEditorCount(editorCount);
        declarationCountVO.setSubEditorCount(subEditorCount);
        declarationCountVO.setEditorialCount(editorialCount);
        declarationCountVO.setDigitalCount(digitalCount);
        declarationCountVO.setChosenEditorCount(chosenEditorCount);
        declarationCountVO.setChosenSubeditorCount(chosenSubeditorCount);
        declarationCountVO.setChosenEditorialCount(chosenEditorialCount);
        declarationCountVO.setChosenDigitalCount(chosenDigitalCount);
        declarationCountVO.setMaterialId(materialId);
        return declarationCountVO;
    }

    @Override
    public PageResult<DeclarationSituationBookResultVO> listDeclarationSituationBookResultVOs(
    PageParameter<DeclarationSituationBookResultVO> pageParameter) throws CheckedServiceException {
        if (ObjectUtil.isNull(pageParameter.getParameter().getMaterialId())) {
            throw new CheckedServiceException(CheckedExceptionBusiness.MATERIAL,
                                              CheckedExceptionResult.NULL_PARAM, "教材id不能为空");
        }
        String bookName = pageParameter.getParameter().getBookName();
        if (StringUtil.notEmpty(bookName)) {
            pageParameter.getParameter().setBookName(bookName);
        }
        PageResult<DeclarationSituationBookResultVO> pageResult =
        new PageResult<DeclarationSituationBookResultVO>();
        PageParameterUitl.CopyPageParameter(pageParameter, pageResult);
        int total = decPositionDao.getBooks(pageParameter.getParameter().getMaterialId());
        if (total > 0) {
            List<DeclarationSituationBookResultVO> declarationSituationBookResultVOs =
            decPositionDao.getBookResult(pageParameter);
            List<DeclarationSituationBookResultVO> list = new ArrayList<>();
            for (DeclarationSituationBookResultVO declarationSituationBookResultVO : declarationSituationBookResultVOs) {
                // 计算申报人数和当选人数
                Integer presetPersons =
                declarationSituationBookResultVO.getPresetPositionEditor()
                + declarationSituationBookResultVO.getPresetPositionSubeditor()
                + declarationSituationBookResultVO.getPresetPositionEditorial()
                + declarationSituationBookResultVO.getPresetDigitalEditor();
                Integer chosenPersons =
                declarationSituationBookResultVO.getChosenPositionEditor()
                + declarationSituationBookResultVO.getChosenPositionSubeditor()
                + declarationSituationBookResultVO.getChosenPositionEditorial()
                + declarationSituationBookResultVO.getIsDigitalEditor();
                declarationSituationBookResultVO.setPresetPersons(presetPersons);
                declarationSituationBookResultVO.setChosenPersons(chosenPersons);
                list.add(declarationSituationBookResultVO);
            }
            pageResult.setRows(list);
            pageResult.setTotal(total);
        }
        return pageResult;
    }

    @Override
    public PageResult<DeclarationResultSchoolVO> listChosenDeclarationResultSchoolVOs(
    PageParameter<DeclarationResultSchoolVO> pageParameter) throws CheckedServiceException {
        if (ObjectUtil.isNull(pageParameter.getParameter().getMaterialId())) {
            throw new CheckedServiceException(CheckedExceptionBusiness.MATERIAL,
                                              CheckedExceptionResult.NULL_PARAM, "教材id不能为空");
        }
        String schoolName = pageParameter.getParameter().getSchoolName();
        if (StringUtil.notEmpty(schoolName)) {
            pageParameter.getParameter().setSchoolName(schoolName);
        }
        PageResult<DeclarationResultSchoolVO> pageResult =
        new PageResult<DeclarationResultSchoolVO>();
        PageParameterUitl.CopyPageParameter(pageParameter, pageResult);
        int total = decPositionDao.getSchoolCount(pageParameter.getParameter().getMaterialId());
        if (total > 0) {
            List<DeclarationResultSchoolVO> VOs = decPositionDao.getSchoolListChosen(pageParameter);
            List<DeclarationResultSchoolVO> list = new ArrayList<>();
            for (DeclarationResultSchoolVO declarationResultSchoolVO : VOs) {
                declarationResultSchoolVO.setState(1);
                list.add(declarationResultSchoolVO);
            }
            pageResult.setRows(list);
            pageResult.setTotal(total);
        }
        return pageResult;
    }

    @Override
    public PageResult<DeclarationResultSchoolVO> listPresetDeclarationResultSchoolVOs(
    PageParameter<DeclarationResultSchoolVO> pageParameter) throws CheckedServiceException {
        if (ObjectUtil.isNull(pageParameter.getParameter().getMaterialId())) {
            throw new CheckedServiceException(CheckedExceptionBusiness.MATERIAL,
                                              CheckedExceptionResult.NULL_PARAM, "教材id不能为空");
        }
        String schoolName = pageParameter.getParameter().getSchoolName();
        if (StringUtil.notEmpty(schoolName)) {
            pageParameter.getParameter().setSchoolName(schoolName);
        }
        PageResult<DeclarationResultSchoolVO> pageResult =
        new PageResult<DeclarationResultSchoolVO>();
        PageParameterUitl.CopyPageParameter(pageParameter, pageResult);
        int total = decPositionDao.getSchoolCount(pageParameter.getParameter().getMaterialId());
        if (total > 0) {
            List<DeclarationResultSchoolVO> VOs = decPositionDao.getSchoolListPreset(pageParameter);
            List<DeclarationResultSchoolVO> list = new ArrayList<>();
            for (DeclarationResultSchoolVO declarationResultSchoolVO : VOs) {
                declarationResultSchoolVO.setState(2);
                list.add(declarationResultSchoolVO);
            }
            pageResult.setRows(list);
            pageResult.setTotal(total);
        }
        return pageResult;
    }

    @Override
    public PageResult<DeclarationResultBookVO> listDeclarationResultBookVOs(
    PageParameter<DeclarationResultBookVO> pageParameter) throws CheckedServiceException {
        if (ObjectUtil.isNull(pageParameter.getParameter().getMaterialId())) {
            throw new CheckedServiceException(CheckedExceptionBusiness.MATERIAL,
                                              CheckedExceptionResult.NULL_PARAM, "教材id不能为空");
        }
        String bookName = pageParameter.getParameter().getBookName();
        if (StringUtil.notEmpty(bookName)) {
            pageParameter.getParameter().setBookName(bookName);
        }
        PageResult<DeclarationResultBookVO> pageResult = new PageResult<DeclarationResultBookVO>();
        PageParameterUitl.CopyPageParameter(pageParameter, pageResult);
        int total = decPositionDao.getBooks(pageParameter.getParameter().getMaterialId());
        if (total > 0) {
            List<DeclarationResultBookVO> list = decPositionDao.getBookList(pageParameter);
            pageResult.setRows(list);
            pageResult.setTotal(total);
        }
        return pageResult;
    }

    @Override
    public List<TextbookDecVO> getTextbookEditorList(Long textbookId)
    throws CheckedServiceException {
        if (textbookId == null) {
            throw new CheckedServiceException(CheckedExceptionBusiness.MATERIAL,
                                              CheckedExceptionResult.NULL_PARAM, "书籍id不能为空");
        }
        return decPositionDao.getTextbookEditorList(textbookId);
    }

    @Override
    public List<Long> getDecPositionIdByBookId(Long textbookId, Integer editorOrSubeditorType)
    throws CheckedServiceException {
        if (ObjectUtil.isNull(textbookId)) {
            throw new CheckedServiceException(CheckedExceptionBusiness.MATERIAL,
                                              CheckedExceptionResult.NULL_PARAM, "书籍id不能为空");
        }
        return decPositionDao.getDecPositionIdByBookId(textbookId, editorOrSubeditorType);
    }

    @Override
    public Integer updateDecPositionSetDefault(List<Long> ids, Integer editorOrSubeditorType)
    throws CheckedServiceException {
        if (CollectionUtil.isEmpty(ids)) {
            throw new CheckedServiceException(CheckedExceptionBusiness.MATERIAL,
                                              CheckedExceptionResult.NULL_PARAM, "主键id不能为空");
        }
        return decPositionDao.updateDecPositionSetDefault(ids, editorOrSubeditorType);
    }

    @Override
    public List<DecPosition> getDecPositionByTextbookId(Long textbookId)
    throws CheckedServiceException {
        if (null == textbookId) {
            throw new CheckedServiceException(CheckedExceptionBusiness.TEXTBOOK,
                                              CheckedExceptionResult.NULL_PARAM, "书籍id不能为空");
        }
        return decPositionDao.getDecPositionByTextbookId(textbookId);
    }

    @Override
    public PageResult<TextBookDecPositionVO> listDeclarationByTextbookIds(
    PageParameter<TextBookDecPositionVO> pageParameter) throws CheckedServiceException {
        if (ArrayUtil.isEmpty(pageParameter.getParameter().getTextBookIds())) {
            throw new CheckedServiceException(CheckedExceptionBusiness.TEXTBOOK,
                                              CheckedExceptionResult.NULL_PARAM, "书籍id不能为空");
        }
        PageResult<TextBookDecPositionVO> pageResult = new PageResult<TextBookDecPositionVO>();
        // 将页面大小和页面页码拷贝
        PageParameterUitl.CopyPageParameter(pageParameter, pageResult);
        // 包含数据总条数的数据集
        List<TextBookDecPositionVO> textBookList =
        decPositionDao.listDeclarationByTextbookIds(pageParameter);
        if (!textBookList.isEmpty()) {
            Integer count = textBookList.get(0).getCount();
            pageResult.setTotal(count);
            pageResult.setRows(textBookList);
        }
        return pageResult;
    }

}
