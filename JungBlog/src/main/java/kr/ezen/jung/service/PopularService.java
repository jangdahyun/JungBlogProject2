package kr.ezen.jung.service;

import kr.ezen.jung.vo.CommonVO;
import kr.ezen.jung.vo.PagingVO;
import kr.ezen.jung.vo.PopularVO;

public interface PopularService {
	// 1. 저장하기
	void insertPopular(PopularVO p);
	// 2. 관리자용 분석용
	PagingVO<PopularVO> getUserTrendAnalysis(CommonVO cv);
	
	// 3. 인기게시물보기는 boardService로 이전
}
