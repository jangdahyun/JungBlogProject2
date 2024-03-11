package kr.ezen.jung.dao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.ezen.jung.vo.JungBoardVO;

@Mapper
public interface JungBoardDAO {
	List<JungBoardVO> selectList(HashMap<String,Object> map) throws SQLException;
	JungBoardVO selectByIdx(int idx) throws SQLException;
	int selectCount(HashMap<String,Object> map) throws SQLException;
	void insert(JungBoardVO jungBoardVO) throws SQLException;
	void update(JungBoardVO jungBoardVO) throws SQLException;
	void updateReadCount(int idx) throws SQLException;
	void hide(int idx) throws SQLException;
	void show(int idx) throws SQLException;
	void delete(int idx) throws SQLException;
	
	
	// 관리자용 및 마이페이지용
	List<JungBoardVO> selectByUserIdx(int userIdx) throws SQLException;
	int selectCountByUserIdx(int userIdx) throws SQLException;
}
