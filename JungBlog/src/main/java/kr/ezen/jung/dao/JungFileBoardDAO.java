package kr.ezen.jung.dao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.ezen.jung.vo.JungFileBoardVO;

@Mapper
public interface JungFileBoardDAO {
	List<JungFileBoardVO> selectList(HashMap<String,Object> map) throws SQLException;
	JungFileBoardVO selectfileByIdx(int idx) throws SQLException;
	List<JungFileBoardVO> selectfileByRef(int idx) throws SQLException;
	int selectCount(HashMap<String,Object> map) throws SQLException;
	void insert(JungFileBoardVO fileboardVO) throws SQLException;
	void update(JungFileBoardVO fileBoardVO) throws SQLException;
	void updateRealCount(int idx) throws SQLException;
	void deleteFake(int idx) throws SQLException;
	void deleteReal(int idx) throws SQLException;
	
}
