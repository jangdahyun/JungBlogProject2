CREATE SEQUENCE tb_Popular_idx_seq;
CREATE TABLE tb_popular(
	idx NUMBER PRIMARY KEY,
	boardRef NUMBER NOT NULL,
	userRef NUMBER NOT NULL,
	interaction NUMBER NOT NULL,
	interaction_time TIMESTAMP DEFAULT sysdate
	FOREIGN KEY (boardRef) REFERENCES jung_board(idx) ON DELETE cascade,
	FOREIGN KEY (userRef) REFERENCES jung_member(idx) ON DELETE cascade
);
SELECT * FROM tb_popular;

DROP TABLE tb_popular;
DROP SEQUENCE tb_Popular_idx_seq;
