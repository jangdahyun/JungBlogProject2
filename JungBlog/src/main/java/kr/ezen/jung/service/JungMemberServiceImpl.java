package kr.ezen.jung.service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.ezen.jung.dao.JungBoardDAO;
import kr.ezen.jung.dao.JungMemberDAO;
import kr.ezen.jung.vo.CommonVO;
import kr.ezen.jung.vo.JungBoardVO;
import kr.ezen.jung.vo.JungMemberVO;
import kr.ezen.jung.vo.PagingVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service(value = "jungMemberService")
@Transactional
public class JungMemberServiceImpl implements JungMemberService{

	@Autowired
	private JungMemberDAO memberDAO;
	@Autowired
	private JungBoardDAO boardDAO;
	
	@Override // 리턴 타입을 UserDetails을 구현한 VO로 바꿔주고 DAO에서 Userid로 VO를 얻어 리턴한다.
	public JungMemberVO loadUserByUsername(String username) throws UsernameNotFoundException {
		log.info(" : " + username + "으로 호출");
		JungMemberVO memberVO = null;
		try {
			memberVO = memberDAO.selectByUsername(username);
			if (memberVO == null) {
				throw new UsernameNotFoundException("지정 아이디가 없습니다.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		log.info(memberVO + "리턴");
		return memberVO;
	}
	
	@Override // 유저 조회
	public JungMemberVO selectByUsername(String username) {
		JungMemberVO memberVO = null;
		try {
			memberVO = memberDAO.selectByUsername(username);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return memberVO;
	}
	
	@Override // 유저 조회
	public JungMemberVO selectByIdx(int idx) {
		JungMemberVO memberVO = null;
		try {
			memberVO = memberDAO.selectByIdx(idx);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return memberVO;
	}
	
	@Override // 중복된 username(id)확인 : 
	public int selectCountByUsername(String username) {
		int result = 0;
	    try {
	        result = memberDAO.selectCountByUsername(username);
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return result;
	}

	@Override // 신규 유저 추가
	public void insert(JungMemberVO memberVO) {
		try {
			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			memberVO.setPassword( passwordEncoder.encode(memberVO.getPassword() ));
			memberDAO.insert(memberVO);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Override // 유저 정보 수정
	public void update(JungMemberVO memberVO) {
		try {
			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			JungMemberVO dbMemberVO = memberDAO.selectByUsername(memberVO.getUsername());
			if( passwordEncoder.matches(memberVO.getPassword(), dbMemberVO.getPassword()) ) {
				memberDAO.update(memberVO);				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override // 유저 정보 삭제
	public void delete(JungMemberVO memberVO) {
		try {
			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			JungMemberVO dbMemberVO = memberDAO.selectByUsername(memberVO.getUsername());
			if(passwordEncoder.matches(memberVO.getPassword(), dbMemberVO.getPassword())) {
				memberDAO.delete(memberVO.getUsername());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	// 관리자 용
	@Override
	/**
	 * 유저를 이름순으로 주기
	 * @param null을 넘기면 idx순 아무거나 넘기면 username순
	 * @return List<JungMemberVO>
	 */
	public PagingVO<JungMemberVO> getUsers(CommonVO cv) {
		PagingVO<JungMemberVO> pv = null;
		try {
			HashMap<String, Object> map = new HashMap<>();
			cv.setS(20);
			cv.setB(5);
			map.put("search", cv.getSearch());
			int totalCount = memberDAO.selectCountUser(map); // 서치가 되면 서치가 되게 수정해함!
			pv = new PagingVO<>(totalCount, cv.getCurrentPage(), cv.getSizeOfPage(), cv.getSizeOfBlock()); // 페이지 계산 완료
			
			map.put("startNo", pv.getStartNo());
			map.put("endNo", pv.getEndNo());
			
			
			List<JungMemberVO> userList = memberDAO.selectUser(map);
			
			for(JungMemberVO user : userList) {
				// 쓴 글 갯수
				user.setBoardCount(boardDAO.selectCountByUserIdx(user.getIdx()));
				// 좋아요한 갯수 ??
				
			}
			pv.setList(userList);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return pv;
	}
	@Override
	public void updateRole(JungMemberVO memberVO) {
		try {
			memberDAO.updateRole(memberVO);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
