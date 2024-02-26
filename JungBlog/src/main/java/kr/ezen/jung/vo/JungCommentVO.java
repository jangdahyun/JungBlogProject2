package kr.ezen.jung.vo;

import java.util.Date;

import lombok.Data;

@Data
/** 댓글을 담는 객체 생성
 * @version
 */
public class JungCommentVO {
	private int idx;			// 키필드
	private int boardRef;		// board idx
	private int userRef;		// user idx
	private String reply;		// 내용
	private Date regDate;		// 게시일
	
	
	private JungMemberVO member;
	
	// 추가 기능 댓글 페이징 댓글 페이징은 자바스크립트로 하자.
	
}
