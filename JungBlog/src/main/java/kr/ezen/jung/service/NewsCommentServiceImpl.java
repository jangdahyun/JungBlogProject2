package kr.ezen.jung.service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.ezen.jung.dao.RssCommentDAO;
import kr.ezen.jung.vo.RssCommentVO;

@Service(value = "newsCommentService")
public class NewsCommentServiceImpl implements NewsCommentService{
	@Autowired
	private RssCommentDAO commentDAO;

	@Override
	public List<RssCommentVO> getCommentByRssBoardRef(int rssBoardRef, int sizeOfPage, int lastItemIdx) {
		List<RssCommentVO> list = null;
		try {
			HashMap<String, String> map = new HashMap<>();
			map.put("rssBoardRef", rssBoardRef+"");
			map.put("sizeOfPage", sizeOfPage+"");
			map.put("lastItemIdx", lastItemIdx+"");
			list = commentDAO.selectScrollByRssRef(map);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
	@Override
	public List<RssCommentVO> findReplyByParentCommentRef(int parentCommentRef) {
		List<RssCommentVO> list = null;
		try {
			list = commentDAO.findReplyByParentCommentRef(parentCommentRef);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public int getTotalCountByRssBoardRef(int rssBoardRef) {
		int result = 0;
		try {
			result = commentDAO.selectTotalCountByRssBoardRef(rssBoardRef);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public int findLastItemIdx() {
		int result = 0;
		try {
			result = commentDAO.findLastItemIdx();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public int insertRssComment(RssCommentVO rcv) {
		int result = 0;
		try {
			commentDAO.insert(rcv);
			result = 1;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public int updateRssComment(int idx, String reply) {
		int result = 0;
		try {
			RssCommentVO rcv = new RssCommentVO();
			rcv.setIdx(idx);
			rcv.setReply(reply);
			commentDAO.update(rcv);
			result = 1;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public int deleteRssCommentByIdx(int idx) {
		int result = 0;
		try {
			commentDAO.deleteByIdx(idx);
			commentDAO.deleteByCommentRef(idx);
			result = 1;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
}
