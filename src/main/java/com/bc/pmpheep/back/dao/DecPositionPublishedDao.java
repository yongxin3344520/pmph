package com.bc.pmpheep.back.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.bc.pmpheep.back.po.DecPositionPublished;

/**
 * 已公布作家申报职位表数据访问层接口
 * 
 * @author tyc 2018年1月15日 16:09
 */
@Repository
public interface DecPositionPublishedDao {

    /**
     * 新增
     * 
     * @author:tyc
     * @date:2018年1月15日下午16:10:12
     * @param decPositionPublished
     * @return
     */
    Integer addDecPositionPublished(DecPositionPublished decPositionPublished);

    /**
     * 删除
     * 
     * @author:tyc
     * @date:2018年1月15日下午16:17:54
     * @param id
     * @return
     */
    Integer deleteDecPositionPublished(Long id);

    /**
     * 更新
     * 
     * @author:tyc
     * @date:2018年1月15日下午16:18:34
     * @param decPositionPublished
     * @return
     */
    Integer updateDecPositionPublished(DecPositionPublished decPositionPublished);

    /**
     * 查询
     * 
     * @author:tyc
     * @date:2018年1月15日下午16:18:57
     * @param id
     * @return
     */
    DecPositionPublished getDecPositionPublishedById(Long id);

    /**
     * 
     * <pre>
     * 功能描述：批量新增
     * 使用示范：
     *
     * @param decPositionPublisheds
     * @return 影响行数
     * </pre>
     */
    Integer batchInsertDecPositionPublished(List<DecPositionPublished> decPositionPublisheds);

    /**
     * 
     * <pre>
     * 功能描述：按公布人id和申报表id删除
     * 使用示范： 影响行数
     *
     * @param map
     * @return
     * </pre>
     */
    Integer deleteDecPositionPublishedByPublisherIdAndDeclarationId(Map<String, Object> map);

}
