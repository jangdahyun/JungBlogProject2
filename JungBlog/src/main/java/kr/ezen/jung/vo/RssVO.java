package kr.ezen.jung.vo;

import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "rss")
public class RssVO {
	@XmlAttribute(name = "version")
	private String version;
	
    private Channel channel;
    
    @Data
    @XmlRootElement
    public static class Channel {
    	private String title;
		private String link;
    	private String language;
		private String copyright;
		private String pubDate;
		private String lastBuildDate;
		private String description;
		private Image image;
        private List<Item> item;
    }
    
    
    @Data
	@XmlRootElement
	public static class Image {
		private String title;
		private String url;
		private String link;
	}
    
    
    @Data
    @XmlRootElement
    public static class Item {
    	private int idx;
        private String title;
        private String link;
        private String image;
        private String author;
        private String pubDate;
        private String category;
        private int readCount;
        private String content;
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Item item = (Item) o;
            return Objects.equals(link, item.link);
        }

        @Override
        public int hashCode() {
            return Objects.hash(link);
        }
    }
}
