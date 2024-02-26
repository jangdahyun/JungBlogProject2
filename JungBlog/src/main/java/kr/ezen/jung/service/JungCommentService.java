package kr.ezen.jung.service;

import java.util.List;

import kr.ezen.jung.vo.JungCommentVO;

public interface JungCommentService {
	List<JungCommentVO> selectByRef(int boardRef);
	
	int selectCountByRef(int boardRef);
	
	List<JungCommentVO> selectByUserRef(int userRef);
	
	void insert(JungCommentVO jungCommentVO);
	
	void update(JungCommentVO jungCommentVO);
	
	void delete(int idx);
	
	void deleteByBoardRef(int boardRef);

	void deleteByUserRef(int userRef);
}
