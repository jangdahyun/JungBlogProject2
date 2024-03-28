package kr.ezen.jung.service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.ezen.jung.dao.JungVideoDAO;
import kr.ezen.jung.vo.CommonVO;
import kr.ezen.jung.vo.JungVideoVO;
import kr.ezen.jung.vo.PagingVO;

@Service(value = "jungVideoService")
public class JungVideoServiceImpl implements JungVideoService{

	@Autowired
	private JungVideoDAO jungVideoDAO;
	
	/**
	 * 파일 저장하기
	 */
	@Override
	public void insert(JungVideoVO videoVO) {
		try {
			jungVideoDAO.insert(videoVO);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 게시글에 해당하는 파일 읽어오기
	 */
	@Override
	public List<JungVideoVO> selectvideoByRef(int idx) {
		List<JungVideoVO> list = null;
		try {
			list = jungVideoDAO.selectvideoByRef(idx);
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
			jungVideoDAO.deleteByRef(ref);
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
			jungVideoDAO.deleteByIdx(idx);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public PagingVO<JungVideoVO> selectList(CommonVO commonVO) {
		PagingVO<JungVideoVO> pv = null;
		try {
			HashMap<String, Object> map = new HashMap<>();
			map.put("search", commonVO.getSearch());
			map.put("categoryNum", commonVO.getCategoryNum());
			
			int totalCount = jungVideoDAO.selectCount(map); // 서치가 되면 서치가 되게 수정해함!
			pv = new PagingVO<>(totalCount, commonVO.getCurrentPage(), commonVO.getSizeOfPage(),
					commonVO.getSizeOfBlock()); // 페이지 계산 완료
			
			map.put("startNo", pv.getStartNo());
			map.put("endNo", pv.getEndNo());
			
			
			List<JungVideoVO> list = jungVideoDAO.selectList(map);
			
			pv.setList(list);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return pv;
	}
	
	@Override
	// 글 1개 보기
	public JungVideoVO selectvideoByIdx(int idx) {
		JungVideoVO videoVO = null;
		try {
			videoVO = jungVideoDAO.selectvideoByIdx(idx);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return videoVO;
	}

	
}
