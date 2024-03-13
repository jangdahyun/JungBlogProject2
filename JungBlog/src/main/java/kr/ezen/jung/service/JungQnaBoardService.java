package kr.ezen.jung.service;

import kr.ezen.jung.vo.CommonVO;
import kr.ezen.jung.vo.JungQnaBoardVO;
import kr.ezen.jung.vo.PagingVO;
public interface JungQnaBoardService {
	public PagingVO<JungQnaBoardVO> selectList(CommonVO cv);
	public void insert(JungQnaBoardVO qnaVO);
	public void update(JungQnaBoardVO qnaVO);
	public void delete(JungQnaBoardVO qnaVO);
	public JungQnaBoardVO selectByIdx(int idx);
	public int selectCount();
}
