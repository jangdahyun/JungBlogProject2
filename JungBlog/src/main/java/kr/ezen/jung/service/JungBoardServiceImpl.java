package kr.ezen.jung.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.ezen.jung.dao.CategoryDAO;
import kr.ezen.jung.dao.HeartDAO;
import kr.ezen.jung.dao.JungBoardDAO;
import kr.ezen.jung.dao.JungCommentDAO;
import kr.ezen.jung.dao.JungFileBoardDAO;
import kr.ezen.jung.dao.JungQnABoardDAO;
import kr.ezen.jung.dao.JungScrollBoardDAO;
import kr.ezen.jung.dao.PopularDAO;
import kr.ezen.jung.vo.CommonVO;
import kr.ezen.jung.vo.HeartVO;
import kr.ezen.jung.vo.JungBoardVO;
import kr.ezen.jung.vo.PagingVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service(value = "jungBoardService")
public class JungBoardServiceImpl implements JungBoardService {

	@Autowired
	private JungBoardDAO jungBoardDAO;
	
	@Autowired
	private JungMemberService jungMemberService;
	
	@Autowired
	private HeartDAO heartDAO;
	
	@Autowired
	private CategoryDAO categoryDAO;
	
	@Autowired
	private JungFileBoardDAO jungFileBoardDAO;
	
	@Autowired
	private JungCommentDAO jungCommentDAO;
	
	@Autowired
	private PopularDAO popularDAO;


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
				board.setMember(jungMemberService.selectByIdx(board.getRef()));
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
		JungBoardVO board = null;
		try {
			board = jungBoardDAO.selectByIdx(idx);
			// 카테고리 이름
			board.setCategoryName(categoryDAO.selectCategoryBycategoryNum(board.getCategoryNum()));
			// 유저정보 넣어주기
			board.setMember(jungMemberService.selectByIdx(board.getRef()));
			// 좋아요 갯수 넣어주기
			board.setCountHeart(heartDAO.countHeart(board.getIdx()));
			// 파일
			board.setFileboardVO(jungFileBoardDAO.selectfileByRef(board.getIdx()));
			// 댓글수
			board.setCommentCount(jungCommentDAO.selectCountByRef(board.getIdx()));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return board;
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
	public int hide(int idx) {
		int result = 0;
		try {
			jungBoardDAO.hide(idx);
			result = 1;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	@Override
	/**
	 * 4. 게시물에서 글 보이기!
	 * 
	 * @param jung_board의 idx
	 */
	public int show(int idx) {
		int result = 1;
		try {
			jungBoardDAO.show(idx);
			result = 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}


	@Override
	/**
	 * 6. db에서 글삭제!
	 * 
	 * @param jung_board의 idx
	 */
	public void delete(int idx) {
		try {
			// 개선점: 비번을 입력하여 확인 후 지울 것인지 아님 그냥 지울 것인가?
			jungBoardDAO.delete(idx);
			jungFileBoardDAO.deleteByRef(idx);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void deleteByUserRef(int ref) {
		try {
			// 개선점: 비번을 입력하여 확인 후 지울 것인지 아님 그냥 지울 것인가?
			jungBoardDAO.deleteByUserRef(ref);
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
	public PagingVO<JungBoardVO> selectByRef(CommonVO commonVO) {
		PagingVO<JungBoardVO> pv = null;
		try {
				HashMap<String, Object> map = new HashMap<>();
				map.put("search", commonVO.getSearch());
				map.put("categoryNum", commonVO.getCategoryNum());
				map.put("orderCode", commonVO.getOrderCode());
				map.put("deleted","all");
				map.put("userRef", commonVO.getUserRef());
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
					board.setMember(jungMemberService.selectByIdx(board.getRef()));
					
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
	/**유저가 좋아요 누른 게시글 가져오기*/
	@Override
	public PagingVO<JungBoardVO> selectHeartByUseridx(CommonVO cv) {
		PagingVO<JungBoardVO> pv= null;
		try {
			HashMap<String, Integer> map = new HashMap<>();
			map.put("userRef", cv.getUserRef());
			int totalCount = heartDAO.heartCountByUserRef(map.get("userRef")); // 서치가 되면 서치가 되게 수정해함!
			pv = new PagingVO<>(totalCount, cv.getCurrentPage(), cv.getSizeOfPage(), cv.getSizeOfBlock()); // 페이지 계산 완료
			map.put("startNo", pv.getStartNo());
			map.put("endNo", pv.getEndNo());
			List<HeartVO> heartList = heartDAO.selectHeartByUseridx(map);
			List<JungBoardVO> boardList = new ArrayList<>();
			log.info("heartList:{}",heartList);
			for(HeartVO heart: heartList) {
				JungBoardVO board = jungBoardDAO.selectByIdx(heart.getBoardRef());
				if(board != null) {
					// 카테고리 이름
					board.setCategoryName(categoryDAO.selectCategoryBycategoryNum(board.getCategoryNum()));
					// 유저정보 넣어주기
					board.setMember(jungMemberService.selectByIdx(board.getRef()));
					
					// 좋아요 갯수 넣어주기
					board.setCountHeart(heartDAO.countHeart(board.getIdx()));
					// 파일
					board.setFileboardVO(jungFileBoardDAO.selectfileByRef(board.getIdx()));
					// 댓글수
					board.setCommentCount(jungCommentDAO.selectCountByRef(board.getIdx()));
					boardList.add(board);
				}
			}
			pv.setList(boardList);
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return pv;
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

	@Override
	public List<JungBoardVO> findPopularBoard() {
		List<JungBoardVO> list = null;
		try {
			List<Integer> popularList = popularDAO.findPopularBoard();
			list = new ArrayList<>();
			for(Integer boardRef : popularList) {
				JungBoardVO board = jungBoardDAO.selectByIdx(boardRef);
	            if(board != null) {
	                // 카테고리 이름
	                board.setCategoryName(categoryDAO.selectCategoryBycategoryNum(board.getCategoryNum()));
	                // 유저정보 넣어주기
	                board.setMember(jungMemberService.selectByIdx(board.getRef()));
	                // 좋아요 갯수 넣어주기
	                board.setCountHeart(heartDAO.countHeart(board.getIdx()));
	                // 파일
	                board.setFileboardVO(jungFileBoardDAO.selectfileByRef(board.getIdx()));
	                // 댓글수
	                board.setCommentCount(jungCommentDAO.selectCountByRef(board.getIdx()));
	                list.add(board);
	            }
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	@Autowired
	JungQnABoardDAO jungQnABoardDAO;
	
	@Override
	public PagingVO<JungBoardVO> selectQnAList(CommonVO cv){
		PagingVO<JungBoardVO> pv = null;
		try {
				HashMap<String, Object> map = new HashMap<>();
				map.put("search", cv.getSearch());
				if(cv.getOrderCode() != null) {
					if(cv.getOrderCode().equals("0")) {
						map.put("commentSize", 0);
					} else {
						map.put("commentSize", 1);
					}
				}
				int totalCount = jungQnABoardDAO.selectQnACount(map);
				pv = new PagingVO<>(totalCount, cv.getCurrentPage(), cv.getSizeOfPage(), cv.getSizeOfBlock()); // 페이지 계산 완료
				
				map.put("startNo", pv.getStartNo());
				map.put("endNo", pv.getEndNo());
				
				List<JungBoardVO> list = jungQnABoardDAO.selectQnAList(map);
				
				for(JungBoardVO board : list) {
					// 카테고리 이름
					board.setCategoryName(categoryDAO.selectCategoryBycategoryNum(board.getCategoryNum()));
					// 유저정보 넣어주기
					board.setMember(jungMemberService.selectByIdx(board.getRef()));
					// 좋아요 갯수 넣어주기
					board.setCountHeart(heartDAO.countHeart(board.getIdx()));
					// 파일
					board.setFileboardVO(jungFileBoardDAO.selectfileByRef(board.getIdx()));
					// 댓글수
					board.setCommentCount(jungCommentDAO.selectCountByRef(board.getIdx()));
					HashMap<String, Object> commentMap = new HashMap<>();
					commentMap.put("boardRef", board.getIdx());
					commentMap.put("startNo", 1);
					commentMap.put("endNo", 1);
					board.setCommentList(jungCommentDAO.selectByRef(commentMap));
					
				}
				pv.setList(list);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return pv;
	}
	
	@Override
	public String getQnAInfo(){
		String result = null;
		try {
			HashMap<String, Object> map = new HashMap<>();
			int totalCount = jungQnABoardDAO.selectQnACount(map);
			map.put("commentSize", 0);
			int unCommentCount = jungQnABoardDAO.selectQnACount(map);
			map.put("commentSize", 1);
			int commentCount = jungQnABoardDAO.selectQnACount(map);
			result = "<span class='title-info'>답변하지 않은 문의 수 : "+unCommentCount+"건 / 답변한 문의 수 : "+commentCount+"건 / 총 문의 수: "+totalCount+"건)</span>";
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	@Autowired
	private JungScrollBoardDAO jungScrollBoardDAO;
	
	@Override
	public ArrayList<JungBoardVO> selectScrollBoard(int lastItemIdx, int sizeOfPage, Integer categoryNum, String search) {
		ArrayList<JungBoardVO> list = null;
		try {
			HashMap<String, Object> map = new HashMap<>();
			map.put("lastItemIdx", lastItemIdx);
			map.put("sizeOfPage", sizeOfPage);
			map.put("categoryNum", categoryNum);
			map.put("search", search);
			list = jungScrollBoardDAO.selectScrollList(map);
			for(JungBoardVO board : list) {
				// 유저정보 넣어주기
				board.setMember(jungMemberService.selectByIdx(board.getRef()));
				// 파일
				board.setFileboardVO(jungFileBoardDAO.selectfileByRef(board.getIdx()));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public int findLastItemIdx() {
		int result=0;
		try {
			result = jungScrollBoardDAO.findLastItemIdx();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
}
