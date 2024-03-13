package kr.ezen.jung.dao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.ezen.jung.vo.JungBoardVO;

@Mapper
public interface JungQnABoardDAO {
	/** QnA만 조회하는 메서드 (search, commentSize=0or1) */
	List<JungBoardVO> selectQnAList(HashMap<String, Object> map) throws SQLException;
	
	/** QnA의 (search, commentSize=0or1)에 해당하는 전체 갯수 얻기 */
	int selectQnACount(HashMap<String, Object> map) throws SQLException;
	
}
