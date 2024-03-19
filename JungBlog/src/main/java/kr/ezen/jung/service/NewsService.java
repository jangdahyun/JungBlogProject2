package kr.ezen.jung.service;

import java.util.List;

import kr.ezen.jung.vo.RssVO.Item;

public interface NewsService {
	
	// 1. 뉴스 목록 가져오기 (search, category, sizeOfPage, lastItemIdx)
	List<Item> getItems(String search, String category, int sizeOfPage, int lastItemIdx);
	// 2. 가장큰 idx 얻기
	int getLastItemIdx();
	
	// 3. 뉴스 한개 보기 => 
	Item selectByIdx(int idx);
	
	// 4. 조회수 증가시키기
	void updateReadCount(int idx);
	
}
