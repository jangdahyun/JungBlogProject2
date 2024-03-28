package kr.ezen.jung.dao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.ezen.jung.vo.RssVO.Item;

@Mapper
public interface RssDAO {
	
	/** 1. 뉴스을 저장하는 */
	void insert(Item item) throws SQLException;
	
	/** 2. 새로운 항목이냐! 체크? 하는 */
	int findByLink(String link) throws SQLException;
	
	/** 3. 조회수 증가! */
	void updateReadCount(int idx) throws SQLException;
	/** 3.1 좋아요 증가! */
	void updateLikeCount(int idx) throws SQLException;
	
	/** 4. 뉴스 게시판 가져오기!(lastItemIdx, sizeOfPage, search, category, pugDate) */
	List<Item> selectRssList(HashMap<String, String> map) throws SQLException;
	
	/** 5. lastItemIdx 얻기 */
	int getLastItemIdx() throws SQLException;
	
	/** 6. 한개 얻기 */
	Item selectByIdx(int idx) throws SQLException;
}
