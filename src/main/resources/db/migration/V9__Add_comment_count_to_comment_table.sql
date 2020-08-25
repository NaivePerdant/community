alter table COMMENT
    add comment_count int default 0 after content;

comment on column COMMENT.comment_count is '评论回复数';
