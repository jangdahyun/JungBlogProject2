package kr.ezen.jung.service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.ezen.jung.dao.JungFileBoardDAO;
import kr.ezen.jung.vo.CommonVO;
import kr.ezen.jung.vo.JungFileBoardVO;
import kr.ezen.jung.vo.PagingVO;

@Service(value = "jungFileboardService")
public class JungFileBoardServiceImpl implements JungFileBoardService{

	@Autowired
	private JungFileBoardDAO jungFileBoardDAO;
	
	/**
	 * 파일 저장하기
	 */
	@Override
	public void insert(JungFileBoardVO fileboardVO) {
		try {
			jungFileBoardDAO.insert(fileboardVO);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 게시글에 해당하는 파일 읽어오기
	 */
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
	
	/**
	 * 게시글에 해당하는 파일 지우기
	 * @param idx
	 */
	@Override
	public void deleteByRef(int ref) {
		try {
			jungFileBoardDAO.deleteByRef(ref);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	
	//========================================================================================================================================================
	/**
	 * 해당하는 하나 지우기 
	 */
	@Override
	public void deleteByIdx(int idx) {
		try {
			jungFileBoardDAO.deleteByIdx(idx);
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
			
			pv.setList(list);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return pv;
	}
	
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

	
}
