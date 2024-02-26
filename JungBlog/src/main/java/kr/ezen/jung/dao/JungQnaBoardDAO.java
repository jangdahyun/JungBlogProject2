package kr.ezen.jung.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import kr.ezen.jung.vo.JungQnaBoardVO;

@Mapper
public interface JungQnaBoardDAO {
	// 1. 저장
	void insert(JungQnaBoardVO qnaVO) throws SQLException;
	// 2. 수정
	void update(JungQnaBoardVO qnaVO) throws SQLException;
	// 3. 삭제
	void delete(int idx) throws SQLException;
	// 4. 1페이지 분량 얻기
	List<JungQnaBoardVO> selectList(Map<String, Integer> map) throws SQLException;
	// 5. 1개 얻기
	JungQnaBoardVO selectByIdx(int idx) throws SQLException;
	// 6. 전체 개수얻기
	int selectCount() throws SQLException;
}
