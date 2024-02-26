package kr.ezen.jung.service;

import java.sql.SQLException;

import kr.ezen.jung.vo.CommonVO;
import kr.ezen.jung.vo.JungQnaBoardVO;
import kr.ezen.jung.vo.PagingVO;
public interface JungQnaBoardService {
	// 목록보기
	PagingVO<JungQnaBoardVO> selectList(CommonVO cv);
	// 저장하기
	void insert(JungQnaBoardVO qnaVO);
	// 수정하기
	void update(JungQnaBoardVO qnaVO);
	// 삭제하기
	void delete(JungQnaBoardVO qnaVO);
	// 1개 얻기
	JungQnaBoardVO selectByIdx(int idx);
	// 6. 전체 개수얻기
	int selectCount() throws SQLException;
}
