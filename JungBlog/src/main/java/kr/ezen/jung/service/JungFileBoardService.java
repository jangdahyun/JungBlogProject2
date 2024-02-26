package kr.ezen.jung.service;

import java.util.List;

import kr.ezen.jung.vo.CommonVO;
import kr.ezen.jung.vo.JungBoardVO;
import kr.ezen.jung.vo.JungFileBoardVO;
import kr.ezen.jung.vo.PagingVO;

public interface JungFileBoardService {
	// 1. 페이징
	PagingVO<JungFileBoardVO> selectList(CommonVO commonVO);
	// 2. 한개얻기
	JungFileBoardVO selectfileByIdx(int idx);
	// 3. 저장

    void insert(JungFileBoardVO fileboardVO);

	// 4. 게시글에서 내리기
	void deleteFake(int idx);
	// 5. 게시글에서 삭제
	void deleteReal(int idx);
	// 6. 수정
	void update(JungFileBoardVO fileBoardVO);
	// 7. 내가 쓴 글만 보기
	List<JungFileBoardVO> selectfileByRef(int idx);
	// 8. 조회수 증가
	void updateReadCount(int idx);
	//9.조회수 증가
	/*
	 * void updateReadCount(int idx);
	 * 
	 * //좋아요 개수 int countHeart(int idx); //좋아요 저장 int insertHeart(HeartVO heartVO);
	 * //좋아요 삭제 int deleteHeart(HeartVO HeartVO); //유저가 좋아요 누른 게시글 번호 가져오기
	 * List<Integer> selectHeartByUseridx(int userRef);
	 * 
	 * //<!-- 하트 중복확인 --> int select(int userRef,int boardRef);
	 */
}
