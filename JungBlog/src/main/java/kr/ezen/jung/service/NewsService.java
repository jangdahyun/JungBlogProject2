package kr.ezen.jung.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import com.googlecode.jslint4java.UnicodeBomInputStream;

import kr.ezen.jung.vo.RssVO;
import kr.ezen.jung.vo.RssVO.Item;

@Component(value = "newsService")
public class NewsService {
	
	public List<Item> getNewsByUrl(String urlAddress) {
		List<Item> items = null;
		URL url;
		JAXBContext context;
		RssVO vo = null;
		try {
			context = JAXBContext.newInstance(RssVO.class);
			Unmarshaller um = context.createUnmarshaller();
			url = new URL(urlAddress);
			InputStream is = url.openStream();
			UnicodeBomInputStream is2 = new UnicodeBomInputStream(is);
			is2.skipBOM();
			InputStreamReader isr = new InputStreamReader(is2);
			vo = (RssVO) um.unmarshal(isr);
			items = new ArrayList<>();
			for(Item item : vo.getChannel().getItem()) {
				items.add(item);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return items;
	}
	
	// jsoup을 이용해 주소에 대한 내용 파싱해서 map에 넘겨주기
	/** 주소에 대한 내용을 파싱해서 리턴하는 메서드 */
	public Map<String, String> getNewsByDetailUrl(String urladdress){
		Map<String, String> map = null;
		try {
			map = new HashMap<>();
			Document doc = Jsoup.connect(urladdress).get();
			
			Elements articletxt = doc.select("#articletxt");
			if(articletxt != null) {
				map.put("all", articletxt.first().html());
			} else {
				map.put("all", "");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}
}
