DROP TABLE jungcategort_tb ;
DROP SEQUENCE jungcategort_tb_idx_seq;
CREATE SEQUENCE jungcategort_tb_idx_seq;



CREATE TABLE  jungcategort_tb(
	idx NUMBER PRIMARY KEY,
	categoryName varchar2(100) NOT null
);



SELECT * FROM jungcategort_tb;



INSERT INTO jungcategort_tb VALUES (jungcategort_tb_idx_seq.nextval, '블로그'); -- 여기서 이름을 일단 미리 만들어 주자
INSERT INTO jungcategort_tb VALUES (jungcategort_tb_idx_seq.nextval, '자료실'); -- 여기서 이름을 일단 미리 만들어 주자
INSERT INTO jungcategort_tb VALUES (jungcategort_tb_idx_seq.nextval, 'QnA'); -- 여기서 이름을 일단 미리 만들어 주자