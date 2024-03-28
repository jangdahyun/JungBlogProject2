package kr.ezen.jung.vo;

import java.util.Date;

import lombok.Data;

@Data
public class RssCommentVO {
	private int idx;
	private int rssBoardRef;
	private int depth;
	private int parentCommentRef;
	private int userRef;
	private String reply;
	private Date regDate;
	
	private String userNickName;
	
	private int childCommentCount;
}
