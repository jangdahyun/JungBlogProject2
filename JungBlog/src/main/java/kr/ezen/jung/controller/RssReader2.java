package kr.ezen.jung.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import kr.ezen.jung.dao.RssDAO;
import kr.ezen.jung.vo.RssVO.Item;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@EnableScheduling
public class RssReader2 {
	private static final String RSS_URL = "https://www.hankyung.com/all-news";
	private List<Item> previousItem = new ArrayList<>();
	
	@Autowired
	private RssDAO rssDAO;
	
	@Scheduled(fixedRate = 5 * 60000) // 1분마다 실행
    public void checkForUpdates() {
		log.info("뉴스 읽기 시작");
        try {
        	List<Item> currentItems = readRss(); // 현재 가져온 뉴스들!
            for (Item item : currentItems) {
                if (!previousItem.contains(item)) {
                	
                	Map<String, String> data = getData(item.getLink());
                	item.setCategory(data.get("catogory"));
                	item.setPubDate(data.get("pubDate"));
                	item.setAuthor(data.get("author"));
                	if(item.getAuthor() == null || item.getAuthor().length() == 0) {
                		item.setAuthor(" ");
                	}
                	
                	log.info("item => {}", item);
                	/*
                	SimpleDateFormat originalFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
                	Date date = null;
                	try {
                	    date = originalFormat.parse(item.getPubDate());
                	} catch (ParseException e) {
                	    e.printStackTrace();
                	    return; // 예외 발생 시 종료하거나 처리할 방법을 선택하세요.
                	}
                	SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                	String formattedDate = newFormat.format(date);
				
                	// 포맷팅된 문자열을 item 객체에 설정
                	item.setPubDate(formattedDate);
                	*/
                	
                	int check = rssDAO.findByLink(item.getLink());
                	if(check != 1) {
                		rssDAO.insert(item);
                	}
                }
            }
            previousItem = currentItems; // 이전 제목들 갱신
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
			e.printStackTrace();
		}
    }
	
    private static List<Item> readRss() throws IOException {
        List<Item> items = new ArrayList<>();
        Document doc = Jsoup.connect(RSS_URL).get();
        Elements rssLis = doc.select(".news-list > li"); // RSS 피드의 항목 선택
        for (Element rssLi : rssLis) {
            Item item = new Item();
            item.setTitle(rssLi.select(".news-tit a").text());
            
            item.setLink(rssLi.select(".news-tit a").attr("href"));
            
            item.setImage(rssLi.select(".thumb img").attr("src"));
            
            items.add(item);
        }
        Collections.reverse(items);
        return items;
    }
    
    private static Map<String, String> getData(String urlAddress) throws IOException{
    	Map<String, String> map = new HashMap<>();
    	Document doc = Jsoup.connect(urlAddress).get();
    	Elements breadcrumb = doc.select(".breadcrumb");		// 카테고리
    	Elements pubDateEl = doc.select(".txt-date");			// 게시일
    	Elements authorEl = doc.select(".author .item");		// 저자
		if(breadcrumb != null && breadcrumb.size() > 0) {
            Element category = breadcrumb.first().selectFirst("a");
            if(category != null) {
                 map.put("catogory", category.text());
            }
        }
		/*
		if(figureImg != null && figureImg.size() > 0) {
			Elements imgTags = figureImg.select("img");
			String src = imgTags.get(0).attr("src");
			if (src != null && !src.equals("")) {
				map.put("str", src);
			}
		}
		*/
		
		if(pubDateEl != null && pubDateEl.size() > 0) {
			Element pubDate = pubDateEl.first();
			if(pubDate != null) {
				//log.info("pubDate => {}", pubDate.text());
                map.put("pubDate", pubDate.text());
           }
		}
		if(authorEl != null && authorEl.size() > 0) {
			Element author = authorEl.first();
			if(author != null) {
				//log.info("pubDate => {}", pubDate.text());
				map.put("author", author.text());
			}
		}
		return map;
    }
}
