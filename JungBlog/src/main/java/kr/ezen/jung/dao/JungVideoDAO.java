package kr.ezen.jung.dao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.ezen.jung.vo.JungVideoVO;

@Mapper
public interface JungVideoDAO {
	
	void insert(JungVideoVO videoVO) throws SQLException;
	
	List<JungVideoVO> selectvideoByRef(int ref) throws SQLException;
	
	void deleteByRef(int idx) throws SQLException;
	
	
	
	
	
	//=========================================================================================================================================================
	
	void deleteByIdx(int idx) throws SQLException;
	
	JungVideoVO selectvideoByIdx(int idx) throws SQLException;
	
	List<JungVideoVO> selectList(HashMap<String,Object> map) throws SQLException;
	
	int selectCount(HashMap<String,Object> map) throws SQLException;
	
}
