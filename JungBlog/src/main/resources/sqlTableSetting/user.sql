CREATE SEQUENCE jung_member_idx_seq;

CREATE TABLE jung_member(
	idx NUMBER PRIMARY KEY,
	username varchar2(100) NOT NULL,
	password varchar2(100) NOT NULL,
	name varchar2(100) NOT NULL,
	nickName varchar2(100) NOT NULL,
	phone varchar2(100) NOT NULL,
	stAddress varchar2(100) NOT NULL,
	dtAddress varchar2(100) NOT NULL,
	birthDate timestamp NOT NULL,
	gender char(1) DEFAULT 1,
	role varchar2(10) NOT NULL
)

DROP TABLE jung_member;

SELECT * FROM jung_member;

INSERT INTO jung_member VALUES (jung_member_idx_seq.nextval,'admin','123456','최고관리자',' ',' ',' ',' ',sysdate,1,'ROLE_ADMIN');
INSERT INTO jung_member VALUES (jung_member_idx_seq.nextval,'master','123456','최고관리자',' ',' ',' ',' ',sysdate,1,'ROLE_ADMIN');
INSERT INTO jung_member VALUES (jung_member_idx_seq.nextval,'webmaster','123456','최고관리자',' ',' ',' ',' ',sysdate,1,'ROLE_ADMIN');
INSERT INTO jung_member VALUES (jung_member_idx_seq.nextval,'root','123456','최고관리자',' ',' ',' ',' ',sysdate,1,'ROLE_ADMIN');
INSERT INTO jung_member VALUES (jung_member_idx_seq.nextval,'dba','123456','최고관리자',' ',' ',' ',' ',sysdate,1,'ROLE_ADMIN');
