package kr.ezen.jung.vo;

import lombok.Data;

@Data
public class MailVO {
	private String from;	// 보내는이
	private String to;		// 받는이
	private String subject;	// 제목
	private String content;	// 내용
}
