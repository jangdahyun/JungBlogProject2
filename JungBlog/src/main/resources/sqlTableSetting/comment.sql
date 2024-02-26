DROP SEQUENCE jung_comment_idx_seq;
DROP TABLE JUNG_COMMENT ;

CREATE sequence jung_comment_idx_seq;

CREATE TABLE jung_comment(
	idx NUMBER PRIMARY KEY,
	boardRef NUMBER NOT NULL,
	userRef NUMBER NOT NULL,
	reply varchar2(250) NOT NULL,
	regDate timestamp DEFAULT sysdate
);

SELECT * FROM jung_comment;