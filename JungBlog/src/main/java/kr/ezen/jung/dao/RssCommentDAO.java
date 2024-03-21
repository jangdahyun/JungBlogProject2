package kr.ezen.jung.dao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.ezen.jung.vo.RssCommentVO;

@Mapper
public interface RssCommentDAO {
	
	/** 페이지 목록 얻기 (rssBoardRef, sizeOfPage, lastItemIdx) */
	List<RssCommentVO> selectScrollByRssRef(HashMap<String, String> map) throws SQLException;
	
	/** 가장큰 idx 얻기 */
	int findLastItemIdx() throws SQLException;
	
	/** boardRef에 해당하는 댓글수 얻기 */
	int selectTotalCountByRssBoardRef(int rssBoardRef) throws SQLException;
	
	/** 댓글에 해당하는 하위 댓글 얻기 */
	List<RssCommentVO> findReplyByParentCommentRef(int parentCommentRef) throws SQLException;
	
	/** 저장하기 */
	void insert(RssCommentVO rcv) throws SQLException;
	
	/** 수정하기 */
	void update(RssCommentVO rcv) throws SQLException;
	
	/** 삭제하기 */
	void deleteByIdx(int idx) throws SQLException;
}
