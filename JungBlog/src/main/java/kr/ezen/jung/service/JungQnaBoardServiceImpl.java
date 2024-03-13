package kr.ezen.jung.service;

import org.springframework.stereotype.Service;

import kr.ezen.jung.vo.CommonVO;
import kr.ezen.jung.vo.JungQnaBoardVO;
import kr.ezen.jung.vo.PagingVO;

@Service(value = "JungQnaBoardService")
public class JungQnaBoardServiceImpl implements JungQnaBoardService{

	
	
	
	@Override
	public PagingVO<JungQnaBoardVO> selectList(CommonVO cv) {
		
		return null;
	}

	@Override
	public void insert(JungQnaBoardVO qnaVO) {
		
	}

	@Override
	public void update(JungQnaBoardVO qnaVO) {
			
	}

	@Override
	public void delete(JungQnaBoardVO qnaVO) {
		
	}

	@Override
	public JungQnaBoardVO selectByIdx(int idx) {
		
		return null;
	}

	@Override
	public int selectCount() {
		
		return 0;
	}

}
