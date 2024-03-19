package kr.ezen.jung.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
public class RssReader {
	private static final String RSS_URL = "https://www.hankyung.com/feed/all-news";
	private List<Item> previousItem = new ArrayList<>();
	
	@Autowired
	private RssDAO rssDAO;
	
	//@Scheduled(fixedRate = 60000) // 1분마다 실행
    public void checkForUpdates() {
		log.info("뉴스 읽기 시작");
        try {
        	List<Item> currentItems = readRss(); // 현재 가져온 뉴스들!
            for (Item item : currentItems) {
                if (!previousItem.contains(item)) {
                	if(item.getAuthor() == null || item.getAuthor().length()==0) {
                		item.setAuthor(" ");
                	}
                	SimpleDateFormat originalFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
                	Date date;
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
                	item.setCategory(getNewsCategory(item.getLink()));
                	log.info("게시글 {}", item);
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
        Elements rssItems = doc.select("item"); // RSS 피드의 항목 선택
        for (Element rssItem : rssItems) {
            Item item = new Item();
            item.setTitle(rssItem.select("title").text());
            item.setLink(rssItem.select("link").text());
            item.setImage(rssItem.select("image").text());
            item.setAuthor(rssItem.select("author").text());
            item.setPubDate(rssItem.select("pubDate").text());
            items.add(item);
        }
        return items;
    }
    
    private static String getNewsCategory(String urlAddress) throws IOException{
    	String result = null;
    	Document doc = Jsoup.connect(urlAddress).get();
    	Elements breadcrumb = doc.select(".breadcrumb");
		if(breadcrumb != null && breadcrumb.size() > 0) {
            Element category = breadcrumb.first().selectFirst("a");
            if(category != null) {
                result = category.text();
            }
        }
		return result;
    }
}
