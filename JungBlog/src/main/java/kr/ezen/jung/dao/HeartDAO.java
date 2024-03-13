package kr.ezen.jung.dao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.ezen.jung.vo.HeartVO;
@Mapper
public interface HeartDAO {
	void insertHeart(HeartVO heartVO) throws SQLException;
	void deleteHeart(HeartVO heartVO) throws SQLException;

	/**좋아요 가져오기*/
	int countHeart(int idx) throws SQLException;
	
	/**유저가 좋아요 누른 게시글 가져오기*/
	List<HeartVO> selectHeartByUseridx(HashMap<String, Integer> map) throws SQLException;
	
	//<!-- 하트 중복확인 -->
	int select(HashMap<String,Integer> map) throws SQLException;
	
	/**유저가 누른 하트 개수 가져오기 */
	int heartCountByUserRef(int userRef) throws SQLException;
}
