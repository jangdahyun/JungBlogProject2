package kr.ezen.jung.service;

import java.util.List;

import kr.ezen.jung.vo.CommonVO;
import kr.ezen.jung.vo.JungFileBoardVO;
import kr.ezen.jung.vo.PagingVO;

public interface JungFileBoardService {
	// 저장
	void insert(JungFileBoardVO fileboardVO);
	// 게시글에 해당하는 파일 가져오기
	List<JungFileBoardVO> selectfileByRef(int idx);
	// 게시글에 따른 지우기
	void deleteByRef(int ref);
	
	
	
	
	
	// 하나 지우기
	void deleteByIdx(int idx);
	// 1. 페이징
	PagingVO<JungFileBoardVO> selectList(CommonVO commonVO);
	// 2. 한개얻기
	JungFileBoardVO selectfileByIdx(int idx);

}
