package kr.ezen.jung.service;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.ezen.jung.dao.PopularDAO;
import kr.ezen.jung.vo.PopularVO;

@Service(value = "popularService")
public class PopularServiceImpl implements PopularService{
	
	@Autowired
	private PopularDAO popularDAO;
	// @Autowired
	// private JungBoardService jungBoardService;
	
	/**
	 * 저장하기
	 * interaction => 1:조회, 2:댓글, 3:좋아요
	 */
	@Override
	public void insertPopular(PopularVO p) {
		try {
			popularDAO.insertPopular(p);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 관리자용 페이징보기
	 */
//	@Override
//	public PagingVO<JungBoardVO> getUserTrendAnalysis(CommonVO cv) {
//		PagingVO<JungBoardVO> pv = null; // jungboardVo에 유저 및 board 정보가 들어있어 이걸로함
//		try {
//			// 1. popular의 크기를 구한다.
//			HashMap<String, Integer> map = new HashMap<>();
//			map.put("userRef", cv.getUserRef());
//			int totalCount = popularDAO.totalCountPopular(map);
//			pv = new PagingVO<>(totalCount, cv.getCurrentPage(), cv.getSizeOfPage(), cv.getSizeOfBlock());
//			map.put("startNo", pv.getStartNo());
//			map.put("endNo", pv.getEndNo());
//			List<PopularVO> popularList = popularDAO.getUserTrendAnalysis(map);
//			// 
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		return pv;
//	}
}
