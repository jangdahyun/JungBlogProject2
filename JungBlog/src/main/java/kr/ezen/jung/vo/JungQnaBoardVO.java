package kr.ezen.jung.vo;

import java.util.Date;

import lombok.Data;

@Data
public class JungQnaBoardVO {
	private int idx;					// 키필드
	private int ref;					// user>idx
	private int categoryNum;			// 1 :blog ,2:file, 3: QnA
	
	private String title;				// 제목
	private String content;				// 내용
	
	private int readCount;				// 조회수
	private Date regDate;				// 게시일
	private int	deleted;				// 게시글 보이기 0 안보이기 1

}
