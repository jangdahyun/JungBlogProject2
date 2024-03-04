package kr.ezen.jung.service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.ezen.jung.dao.CategoryDAO;
import kr.ezen.jung.dao.HeartDAO;
import kr.ezen.jung.dao.JungBoardDAO;
import kr.ezen.jung.dao.JungCommentDAO;
import kr.ezen.jung.dao.JungFileBoardDAO;
import kr.ezen.jung.dao.JungMemberDAO;
import kr.ezen.jung.vo.CommonVO;
import kr.ezen.jung.vo.HeartVO;
import kr.ezen.jung.vo.JungBoardVO;
import kr.ezen.jung.vo.PagingVO;

@Service(value = "jungBoardService")
public class JungBoardServiceImpl implements JungBoardService {

	@Autowired
	private JungBoardDAO jungBoardDAO;
	
	@Autowired
	private JungMemberDAO jungMemberDAO;
	
	@Autowired
	private HeartDAO heartDAO;
	
	@Autowired
	private CategoryDAO categoryDAO;
	
	@Autowired
	private JungFileBoardDAO jungFileBoardDAO;
	
	@Autowired

	private JungCommentDAO jungCommentDAO;


	@Override
	/**
	 * 1. 리스트 보기!
	 * 
	 * @param commonVO 에는 currentPage, sizeOfPage, sizeOfBloak, search 값이 들어있는 클래스
	 * @return 페이징 처리를 한 PagingVO객체 리턴
	 */
	public PagingVO<JungBoardVO> selectList(CommonVO commonVO) {
		PagingVO<JungBoardVO> pv = null;
		try {
			HashMap<String, Object> map = new HashMap<>();
			map.put("search", commonVO.getSearch());
			map.put("categoryNum", commonVO.getCategoryNum());
			map.put("orderCode", commonVO.getOrderCode());
			int totalCount = jungBoardDAO.selectCount(map); // 서치가 되면 서치가 되게 수정해함!
			pv = new PagingVO<>(totalCount, commonVO.getCurrentPage(), commonVO.getSizeOfPage(),
					commonVO.getSizeOfBlock()); // 페이지 계산 완료
			
			map.put("startNo", pv.getStartNo());
			map.put("endNo", pv.getEndNo());
			
			
			List<JungBoardVO> list = jungBoardDAO.selectList(map);
			
			for(JungBoardVO board : list) {
				// 카테고리 이름
				board.setCategoryName(categoryDAO.selectCategoryBycategoryNum(board.getCategoryNum()));
				// 유저정보 넣어주기
				board.setMember(jungMemberDAO.selectByIdx(board.getRef()));
				// 좋아요 갯수 넣어주기
				board.setCountHeart(heartDAO.countHeart(board.getIdx()));
				// 파일
				board.setFileboardVO(jungFileBoardDAO.selectfileByRef(board.getIdx()));

				// 댓글수
				board.setCommentCount(jungCommentDAO.selectCountByRef(board.getIdx()));

			
			}
			pv.setList(list);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return pv;
	}

	@Override
	/**
	 * 2. 글 1개 보기!
	 * 
	 * @param jung_board의 idx
	 * @return idx에 일치하는 JungBoardVO
	 */
	public JungBoardVO selectByIdx(int idx) {
		JungBoardVO jungBoardVO = null;
		try {
			jungBoardVO = jungBoardDAO.selectByIdx(idx);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return jungBoardVO;
	}

	@Override
	/**
	 * 3. 글 쓰기!
	 * 
	 * @param JungBoardVO jungBoardVO
	 */
	public void insert(JungBoardVO jungBoardVO) {
		try {
			jungBoardDAO.insert(jungBoardVO);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	/**
	 * 4. 게시물에서 글 숨기기기능!
	 * 
	 * @param jung_board의 idx
	 */
	public void deleteFake(int idx) {
		try {
			jungBoardDAO.deleteFake(idx);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	/**
	 * 6. db에서 글삭제!
	 * 
	 * @param jung_board의 idx
	 */
	public void deleteReal(int idx) {
		try {
			// 개선점: 비번을 입력하여 확인 후 지울 것인지 아님 그냥 지울 것인가?
			jungBoardDAO.deleteReal(idx);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	/**
	 * 5. 글 수정하기
	 * 
	 * @param JungBoardVO jungBoardVO
	 */
	public void update(JungBoardVO jungBoardVO) {
		try {
			jungBoardDAO.update(jungBoardVO);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	/*
	 * 내가 쓴 글만 보기
	 * 
	 * @param JungMemberVO idx
	 * 
	 * @return idx와 일치하는 JungBoardVO의 ref
	 */
	public List<JungBoardVO> selectByRef(int idx) {
		List<JungBoardVO> list = null;
		try {
			list = jungBoardDAO.selectByUserIdx(idx);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * @param jung_board의 idx
	 * @author 정다현
	 */
	
	@Override
	public void updateReadCount(int idx) {
		try {
			jungBoardDAO.updateReadCount(idx);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public int countHeart(int idx) {
		int result = 0;
		try {
			result = heartDAO.countHeart(idx);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
		
		
	}

	
	@Override
	public int insertHeart(HeartVO heartVO) {
		int result = 0;
		try {
			heartDAO.insertHeart(heartVO);
			result = 1;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public int deleteHeart(HeartVO heartVO) {
		int result = 0;
		try {
			heartDAO.deleteHeart(heartVO);
			result = 1;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
			
	}
	//유저가 좋아요 누른 게시글 번호 가져오기
	@Override
	public List<Integer> selectHeartByUseridx(int userRef) {
		List<Integer> list= null;
		try {
			list = heartDAO.selectHeartByUseridx(userRef);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public int select(int userRef, int boardRef) {
		int result=0;
		HashMap<String, Integer>map=new HashMap<>();
		map.put("userRef", userRef);
		map.put("boardRef", boardRef);
		try {
			result=heartDAO.select(map);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	
	
	
	@Override
	public String findCategoryName(int categoryNum) {
		String categoryName = "";
		try {
			categoryName = categoryDAO.selectCategoryBycategoryNum(categoryNum);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return categoryName;
	}

	@Override
	public List<String> findCategoryList() {
		List<String> categoryList = null;
		try {
			categoryList = categoryDAO.selectCategory();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return categoryList;
	}

	


}
