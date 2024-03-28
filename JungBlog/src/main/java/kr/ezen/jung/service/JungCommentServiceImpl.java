package kr.ezen.jung.service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.ezen.jung.dao.JungCommentDAO;
import kr.ezen.jung.dao.JungMemberDAO;
import kr.ezen.jung.vo.CommonVO;
import kr.ezen.jung.vo.JungCommentVO;
import kr.ezen.jung.vo.PagingVO;

@Service(value = "jungCommentService")
@Transactional
public class JungCommentServiceImpl implements JungCommentService{
	
	@Autowired
	private JungCommentDAO jungCommentDAO;
	
	@Autowired
	private JungMemberDAO jungMemberDAO;

	/**
	 * 댓글을 페이징해서 가져오는 메시드
	 * @param int boardRef, CommonVO
	 * @return PagingVO<JungCommentVO>
	 */
	@Override
	public PagingVO<JungCommentVO> selectByRef(int boardRef, CommonVO cv) {
		PagingVO<JungCommentVO> pv = null;
		try {
			HashMap<String, Object> map = new HashMap<>();
			cv.setS(5);
			cv.setB(5);
			int totalCount = jungCommentDAO.selectCountByRef(boardRef);
			pv = new PagingVO<>(totalCount, cv.getCurrentPage(), cv.getSizeOfPage(), cv.getSizeOfBlock());
			map.put("boardRef", boardRef);
			map.put("startNo", pv.getStartNo());
			map.put("endNo", pv.getEndNo());
			List<JungCommentVO> commentList = jungCommentDAO.selectByRef(map);
			for(JungCommentVO comment : commentList) {
				// 유저정보
				comment.setMember(jungMemberDAO.selectByIdx(comment.getUserRef()));
			}
			pv.setList(commentList);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return pv;
	}
	
	@Override
	public int selectCountByRef(int boardRef) {
		int result = 0;
		try {
			result = jungCommentDAO.selectCountByRef(boardRef);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	@Override
	public int insert(JungCommentVO jungCommentVO) {
		int result = 0;
		try {
			jungCommentDAO.insert(jungCommentVO);
			result = 1;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public int update(JungCommentVO jungCommentVO) {
		int result = 0;
		try {
			jungCommentDAO.update(jungCommentVO);
			result = 1;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public int delete(int idx) {
		int result = 0;
		try {
			jungCommentDAO.delete(idx);
			result = 1;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public void deleteByBoardRef(int boardRef) {
		try {
			jungCommentDAO.deleteByBoardRef(boardRef);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void deleteByUserRef(int userRef) {
		try {
			jungCommentDAO.deleteByUserRef(userRef);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
