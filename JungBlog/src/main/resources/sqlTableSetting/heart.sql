DROP SEQUENCE heart_tb_heart_seq;
DROP TABLE heart_tb ;

CREATE sequence heart_tb_heart_seq;

CREATE TABLE  heart_tb(
	idx NUMBER PRIMARY KEY,
	boardRef NUMBER NOT NULL,
	userRef NUMBER NOT NULL
);

SELECT * FROM heart_tb;