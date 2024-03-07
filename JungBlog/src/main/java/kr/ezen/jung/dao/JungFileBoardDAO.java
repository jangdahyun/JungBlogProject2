package kr.ezen.jung.dao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.ezen.jung.vo.JungFileBoardVO;

@Mapper
public interface JungFileBoardDAO {
	
	void insert(JungFileBoardVO fileboardVO) throws SQLException;
	
	List<JungFileBoardVO> selectfileByRef(int ref) throws SQLException;
	
	void deleteByRef(int idx) throws SQLException;
	
	
	
	
	
	//=========================================================================================================================================================
	
	void deleteByIdx(int idx) throws SQLException;
	
	JungFileBoardVO selectfileByIdx(int idx) throws SQLException;
	
	List<JungFileBoardVO> selectList(HashMap<String,Object> map) throws SQLException;
	
	int selectCount(HashMap<String,Object> map) throws SQLException;
	
}
