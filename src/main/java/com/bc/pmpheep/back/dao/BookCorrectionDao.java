package com.bc.pmpheep.back.dao;



import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;
import com.bc.pmpheep.back.po.BookCorrection;
import com.bc.pmpheep.back.vo.BookCorrectionAuditVO;


/**
 * BookCorrection  实体类数据访问层接口
 * 
 * @author mryang
 */
@Repository
public interface BookCorrectionDao {

	 /**
     * 新增一个    BookCorrection
     * @param bookCorrection 实体对象
     * @return  影响行数
     */
	Integer addBookCorrection(BookCorrection bookCorrection) ;
	
	/**
	 * 根据主键删除BookCorrection
	 * @introduction 
	 * @author Mryang
	 * @createDate 2017年12月19日 下午5:12:57
	 * @param id
	 * @return  影响行数
	 */
	Integer   deleteBookCorrectionById (Long id) ;
    
	 /**
     * 更新一个BookCorrection
     * @param bookCorrection 实体对象
     * @return 影响行数
     */
	Integer  updateBookCorrection(BookCorrection bookCorrection);
	
	/**
	 * 根据主键获取BookCorrection
	 * @introduction 
	 * @author Mryang
	 * @createDate 2017年12月19日 下午5:12:57
	 * @param id
	 * @return BookCorrection
	 */
	BookCorrection   getBookCorrectionById (Long id) ;
	
	/**
	 * 获取审核纠错页面的分页数据
	 * @introduction 
	 * @author Mryang
	 * @createDate 2017年12月20日 上午11:22:04
	 * @param map
	 * @return
	 */
	List<BookCorrectionAuditVO> listBookCorrectionAudit (Map<String,Object> map);
	
	/**
	 * 获取审核纠错页面的数据总数
	 * @introduction 
	 * @author Mryang
	 * @createDate 2017年12月20日 上午11:22:04
	 * @param map
	 * @return
	 */
	Integer listBookCorrectionAuditTotal (Map<String,Object> map);
	
}
