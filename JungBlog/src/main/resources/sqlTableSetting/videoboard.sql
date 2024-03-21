SELECT * FROM JUNG_VIDEO;

CREATE SEQUENCE jung_video_idx_seq;

CREATE TABLE JUNG_VIDEO  (
   idx NUMBER PRIMARY KEY ,
   REF NUMBER NOT null ,
   filepath varchar2(200) NOT NULL,
   url varchar2(200) NOT NULL
  );