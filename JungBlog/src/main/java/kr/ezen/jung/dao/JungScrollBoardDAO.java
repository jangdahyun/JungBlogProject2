package kr.ezen.jung.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.ibatis.annotations.Mapper;

import kr.ezen.jung.vo.JungBoardVO;

@Mapper
public interface JungScrollBoardDAO {
	
	/**
	 * @param (lastItemIdx, sizeOfPage, categoryNum, search)
	 * @return
	 */
	ArrayList<JungBoardVO> selectScrollList(HashMap<String, Object> map) throws SQLException;
	
	/** board의 최대 idx 리턴 */
	int findLastItemIdx() throws SQLException;
}
