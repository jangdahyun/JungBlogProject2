package kr.ezen.jung.dao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.ezen.jung.vo.JungMemberVO;

@Mapper
public interface JungMemberDAO {
	// 특정 유저 조회
	JungMemberVO selectByUsername(String username) throws SQLException;
	
	JungMemberVO selectByEmail(String email) throws SQLException;
	
	JungMemberVO selectByIdx(int idx) throws SQLException;
	
	//비밀번호 찾기
	void findPwByUserName(JungMemberVO memberVO) throws SQLException;
	
	// 아이디가 같은게 잇는지 확인
	int selectCountByUsername(String username) throws SQLException;
	
	//닉네임 중복 확인
	int selectCountByNickName(String username) throws SQLException;
	
	// 저장
	void insert(JungMemberVO memberVO) throws SQLException;
	
	// 수정
	void update(JungMemberVO memberVO) throws SQLException; 

	// 유저 정보 삭제
	void delete(int idx) throws SQLException;
	
	int emailCheck(String email) throws SQLException;
	
	// 관리자용
	// 리스트 타입으로 모든 유저 조회
	List<JungMemberVO> selectUser(HashMap<String, Object> map) throws SQLException;
	
	// 페이징용
	int selectCountUser(HashMap<String, Object> map) throws SQLException;
	
	// 관리자가 유저에게 권한 부여하기
	void updateRole(JungMemberVO memberVO) throws SQLException;
}