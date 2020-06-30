alter table COMMENT
    add content text;

comment on column COMMENT.content is '评论内容';
