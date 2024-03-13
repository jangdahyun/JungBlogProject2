package kr.ezen.jung.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import kr.ezen.jung.vo.RssVO.Item;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RssReader {
	private static final String RSS_IT_URL = "https://www.hankyung.com/feed/all-news";
	private List<Item> previousItem = new ArrayList<>();
	
	
	// @Scheduled(fixedRate = 60000) // 1분마다 실행
    public void checkForUpdates() {
		log.info("뉴스 읽기 시작");
        try {
            int count = 1;
        	List<Item> currentItems = readRss(); // 현재 가져온 제목들
            for (Item item : currentItems) {
                if (!previousItem.contains(item)) { // 새로운 항목이 발견되면!
                	item.setContent(getNewsContent(item.getLink()));
                	System.out.println("새로운 항목 발견: " + count++);
                }
            }
            previousItem = currentItems; // 이전 제목들 갱신
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
    private static List<Item> readRss() throws IOException {
        List<Item> items = new ArrayList<>();
        Document doc = Jsoup.connect(RSS_IT_URL).get();
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
    
    private static String getNewsContent(String urladdress) throws IOException{
    	String result = null;
    	Document doc = Jsoup.connect(urladdress).get();
    	Elements articletxt = doc.select("#articletxt");
		if(articletxt != null) {
			result = articletxt.first().html();
		}
		return result;
    }
}
