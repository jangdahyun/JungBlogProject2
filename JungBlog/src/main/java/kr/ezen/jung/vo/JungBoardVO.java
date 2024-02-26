package kr.ezen.jung.vo;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class JungBoardVO {
	private int idx;					// 키필드
	private int ref;					// user>idx
	private int categoryNum;			// 1 :blog ,2:file, 3: QnA
	
	private String title;				// 제목
	private String content;				// 내용
	
	private int readCount;				// 조회수
	private Date regDate;				// 게시일
	private int	deleted;				// 게시글 보이기 0 안보이기 1
	// db설계끝
	
	
	private String categoryName;		// 카테고리 이름
	// 좋아요!
	private int countHeart;				// 좋아요 수
	private int commentCount;			// 댓글 수

	// 제한된 유저정보만 idx, 이름(nickname), 사진, 
	private JungMemberVO member;			
	
	private List<JungCommentVO> commentList;	// 댓글 리스트
	private List<JungFileBoardVO> fileboardVO;	// 파일 리스트
	
	// ------------------------------------------------------------
	// 추가등등
	// ------------------------------------------------------------
	// 태그
	
	// ------------------------------------------------------------
	
}
