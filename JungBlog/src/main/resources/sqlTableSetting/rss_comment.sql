DROP TABLE rss_comment;
DROP SEQUENCE rss_comment_idx_seq;

CREATE SEQUENCE rss_comment_idx_seq;

CREATE TABLE rss_comment (
    idx NUMBER PRIMARY KEY,
    rssBoardRef NUMBER NOT NULL,
    DEPTH NUMBER DEFAULT 0,
    parentCommentRef NUMBER NOT NULL,
    userRef NUMBER NOT NULL,
    reply VARCHAR2(200) NOT NULL,
    regDate TIMESTAMP DEFAULT SYSDATE,
    CONSTRAINT fk_rss_comment_rssBoardRef FOREIGN KEY (rssBoardRef) REFERENCES tb_rssBoard(idx) ON DELETE CASCADE,
    CONSTRAINT fk_rss_comment_userRef FOREIGN KEY (userRef) REFERENCES JUNG_MEMBER(idx) ON DELETE CASCADE
);
