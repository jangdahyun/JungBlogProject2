package kr.ezen.jung.service;

import kr.ezen.jung.vo.CommonVO;
import kr.ezen.jung.vo.JungCommentVO;
import kr.ezen.jung.vo.PagingVO;

public interface JungCommentService {
	PagingVO<JungCommentVO> selectByRef(int boardRef, CommonVO cv);
	
	int selectCountByRef(int boardRef);
	
	int insert(JungCommentVO jungCommentVO);
	
	int update(JungCommentVO jungCommentVO);
	
	int delete(int idx);
	
	void deleteByBoardRef(int boardRef);

	void deleteByUserRef(int userRef);
}
