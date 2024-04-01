package kr.ezen.jung.service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import kr.ezen.jung.dao.RssDAO;
import kr.ezen.jung.vo.RssVO.Item;

@Component(value = "newsService")
public class NewsServiceImpl implements NewsService{

	@Autowired
	private RssDAO rssDAO;
	
	/** 뉴스기사목록얻기 (search, catogory, size, lastItemIdx) */
	@Override
	public List<Item> getItems(String search, String category, int sizeOfPage, int lastItemIdx) {
		List<Item> list = null;
		try {
			HashMap<String, String> map = new HashMap<>();
			map.put("search", search);
			map.put("category", category);
			map.put("sizeOfPage", sizeOfPage + "");
			map.put("lastItemIdx", lastItemIdx + "");
			list = rssDAO.selectRssList(map);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	@Override
	public int getLastItemIdx() {
		int result = 0;
		try {
			result = rssDAO.getLastItemIdx();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/** 글 하나 보기 idx로 얻기 */
	@Override
	public Item selectByIdx(int idx) {
		Item item = null;
		try {
			item = rssDAO.selectByIdx(idx);
			if(item != null) {
				Document doc = Jsoup.connect(item.getLink()).get();
				Elements articletxt = doc.select("#articletxt");
				Elements headline = doc.select(".headline");
				if(articletxt != null) {
					item.setContent(articletxt.first().html());
				}
				if(headline != null) {
					item.setTitle(headline.first().html());
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return item;
	}
	
	/** 조회 수 증가 */
	@Override
	public void updateReadCount(int idx) {
		try {
			rssDAO.updateReadCount(idx);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/** 좋아요 수 증가 */
	@Override
	public int updateLikeCount(int idx) {
		int result = 0;
		try {
			rssDAO.updateLikeCount(idx);
			result = 1;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
}
