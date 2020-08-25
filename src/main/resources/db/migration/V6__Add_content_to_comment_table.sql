alter table COMMENT
    add content text after commentator;

comment on column COMMENT.content is '评论内容';
