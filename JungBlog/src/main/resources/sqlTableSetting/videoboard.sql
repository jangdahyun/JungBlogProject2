SELECT * FROM JUNG_VIDEO;

CREATE SEQUENCE jung_video_idx_seq;

CREATE TABLE JUNG_VIDEO  (
   idx NUMBER PRIMARY KEY ,
   REF NUMBER NOT null ,
   videourl varchar2(200) NOT NULL,
   youtube varchar2(200) NOT NULL
  );