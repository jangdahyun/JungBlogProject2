package kr.ezen.jung.service;

import java.util.List;

import kr.ezen.jung.vo.RssCommentVO;

public interface NewsCommentService {
	/** 댓글 얻기 (boardRef, size, lastItemIdx) */
	List<RssCommentVO> getCommentByRssBoardRef(int rssBoardRef, int sizeOfPage, int lastItemIdx);
	
	/** parentCommentRef에 해당하는 대댓글 얻기 */
	List<RssCommentVO> findReplyByParentCommentRef(int parentCommentRef);
	
	/** boardRef에 해당하는 댓글 갯수 얻기 */
	int getTotalCountByRssBoardRef(int rssBoardRef);
	
	/** findLastItemIdx */
	int findLastItemIdx();
	
	/** 글쓰기 */
	int insertRssComment(RssCommentVO rcv);
	
	/** 수정하기 */
	int updateRssComment(int idx, String reply);
	
	/** 삭제하기 */
	int deleteRssCommentByIdx(int idx);
}
