DROP SEQUENCE tb_rssBoard_idx_seq;

DROP TABLE tb_rssBoard;


CREATE SEQUENCE tb_rssBoard_idx_seq;

CREATE table tb_rssBoard(
	idx NUMBER PRIMARY KEY,
	title varchar2(200) NOT NULL,			-- 제목
	link VARCHAR2(1000) NOT NULL,			-- 링크 > 이게 식별자역활이 됨!
	image VARCHAR2(1000),					-- 이미지 사진
	author VARCHAR2(100) NOT NULL,			-- 작성자
	pubDate VARCHAR2(100) NOT NULL,			-- 게시일
	category VARCHAR2(100) NOT NULL,		-- 카테고리
	readCount NUMBER DEFAULT 0,			-- 조회수
	likeCount NUMBER DEFAULT 0
);



SELECT * FROM jung_board;