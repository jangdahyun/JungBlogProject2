DROP SEQUENCE jung_board_idx_seq;

DROP TABLE jung_board;


CREATE SEQUENCE jung_board_idx_seq;

CREATE table jung_board(
	idx NUMBER PRIMARY KEY ,
	REF NUMBER NOT null ,
	categoryNum number NOT NULL,
	title varchar2(200) NOT null,
	content varchar2(4000) NOT null,
	readCount NUMBER DEFAULT 0,
	regDate timestamp DEFAULT sysdate,
	deleted char(1) DEFAULT 0
);



SELECT * FROM jung_board;