package kr.ezen.jung.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import kr.ezen.jung.vo.CommonVO;
import kr.ezen.jung.vo.JungMemberVO;
import kr.ezen.jung.vo.PagingVO;

public interface JungMemberService extends UserDetailsService{

	JungMemberVO selectByUsername(String username);
	
	JungMemberVO selectByIdx(int idx);
	
	int selectCountByUsername(String username);
	
	void insert(JungMemberVO memberVO);
	
	void update(JungMemberVO memberVO);
	
	void delete(JungMemberVO memberVO);

	// 관리자 용
	PagingVO<JungMemberVO> getUsers(CommonVO cv);
	
	void updateRole(JungMemberVO memberVO);
}
