package kr.ezen.jung.service;

import java.util.List;
import java.util.Map;

import kr.ezen.jung.vo.CommonVO;
import kr.ezen.jung.vo.HeartVO;
import kr.ezen.jung.vo.JungBoardVO;
import kr.ezen.jung.vo.PagingVO;

public interface JungBoardService {
	// 1. 페이징
	PagingVO<JungBoardVO> selectList(CommonVO commonVO);
	
	// 2. 한개얻기
	JungBoardVO selectByIdx(int idx);

	// 3. 저장
	void insert(JungBoardVO jungBoardVO);
	
	// 4. 게시글에서 내리기
	int hide(int idx);

	// 4-1. 게시글 보이기
	int show(int idx);
	
	// 5. 게시글 삭제
	void delete(int idx);
	
	//5-1 userRef를 통한 게시글 삭제
	void deleteByUserRef(int ref);
	
	// 6.수정
	void update(JungBoardVO jungBoardVO);
	
	// 7. 내가 쓴 글만 보기
	PagingVO<JungBoardVO> selectByRef(CommonVO commonVO);
	// 8. 임시저장
	
	//9.조회수 증가
	void updateReadCount(int idx);
	
	//좋아요  개수 
	int countHeart(int idx);
	//좋아요 저장
	int insertHeart(HeartVO heartVO);
	//좋아요 삭제
	int deleteHeart(HeartVO HeartVO);
	//유저가 좋아요 누른 게시글 번호 가져오기
	PagingVO<JungBoardVO> selectHeartByUseridx(CommonVO cv);
	
	//<!-- 하트 중복확인 -->
	int select(int userRef,int boardRef);
	
	// 카테고리 불러오기
	String findCategoryName(int categoryNum);
	
	List<String> findCategoryList();
	
	// 인기게시물 가져오기 (20개 한정임)
	List<JungBoardVO> findPopularBoard();
	
	PagingVO<JungBoardVO> selectQnAList(CommonVO cv);
	String getQnAInfo();
	
	
}
