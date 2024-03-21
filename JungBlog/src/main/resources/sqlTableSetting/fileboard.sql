CREATE SEQUENCE jung_file_board_idx_seq;

CREATE TABLE JUNG_FILE_BOARD (
   idx NUMBER PRIMARY KEY ,
   REF NUMBER NOT null ,
   filepath varchar2(200) NOT NULL,

   url varchar2(200) NOT NULL,
   constraint JUNG_FILE_BOARD_fk FOREIGN KEY (REF) references jung_board(idx) on delete cascade
)
SELECT * FROM JUNG_FILE_BOARD;