package kr.ezen.jung.service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.ezen.jung.dao.HeartDAO;
import kr.ezen.jung.dao.JungFileBoardDAO;
import kr.ezen.jung.vo.CommonVO;
import kr.ezen.jung.vo.HeartVO;
import kr.ezen.jung.vo.JungBoardVO;
import kr.ezen.jung.vo.JungFileBoardVO;
import kr.ezen.jung.vo.PagingVO;

@Service(value = "jungFileboardService")
public class JungFileBoardServiceImpl implements JungFileBoardService{

	@Autowired
	private JungFileBoardDAO jungFileBoardDAO;
	
	@Autowired
	private HeartDAO heartDAO;	
	
	@Override
	// 글 1개 보기
	public JungFileBoardVO selectfileByIdx(int idx) {
		JungFileBoardVO fileBoardVO = null;
		try {
			fileBoardVO = jungFileBoardDAO.selectfileByIdx(idx);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return fileBoardVO;
	}

	@Override
	public void insert(JungFileBoardVO fileboardVO) {
		try {
			jungFileBoardDAO.insert(fileboardVO);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void deleteFake(int idx) {
		try {
			jungFileBoardDAO.deleteFake(idx);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void deleteReal(int idx) {
		try {
			jungFileBoardDAO.deleteReal(idx);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void update(JungFileBoardVO fileBoardVO) {
		try {
			jungFileBoardDAO.update(fileBoardVO);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// 유저가 쓴글만 보기
	@Override
	public List<JungFileBoardVO> selectfileByRef(int idx) {
		List<JungFileBoardVO> list = null;
		try {
			list = jungFileBoardDAO.selectfileByRef(idx);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public void updateReadCount(int idx) {
		try {
			jungFileBoardDAO.updateRealCount(idx);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	
	public PagingVO<JungFileBoardVO> selectList(CommonVO commonVO) {
		PagingVO<JungFileBoardVO> pv = null;
		try {
			HashMap<String, Object> map = new HashMap<>();
			map.put("search", commonVO.getSearch());
			map.put("categoryNum", commonVO.getCategoryNum());
			
			int totalCount = jungFileBoardDAO.selectCount(map); // 서치가 되면 서치가 되게 수정해함!
			pv = new PagingVO<>(totalCount, commonVO.getCurrentPage(), commonVO.getSizeOfPage(),
					commonVO.getSizeOfBlock()); // 페이지 계산 완료
			
			map.put("startNo", pv.getStartNo());
			map.put("endNo", pv.getEndNo());
			
			
			List<JungFileBoardVO> list = jungFileBoardDAO.selectList(map);
			
			for(JungFileBoardVO board : list) {
				// 카테고리 이름
				//board.setCategoryName(categoryDAO.selectCategoryBycategoryNum(board.getCategoryNum()));
				// 유저정보 넣어주기
				//board.setMember(jungMemberDAO.selectByIdx(board.getRef()));
				// 좋아요 갯수 넣어주기
				//board.setCountHeart(heartDAO.countHeart(board.getIdx()));
				// 파일
				//board.setFileboardVO(jungFileBoardDAO.selectfileByRef(board.getIdx()));
			}
			pv.setList(list);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return pv;
	}

	
}
