create table comment
(
    id bigint auto_increment,
    parent_id bigint not null,
    type int not null,
    commentator int not null,
    like_count bigint not null default 0,
    gmt_create bigint not null,
    gmt_modified bigint not null,
    constraint COMMENT_PK
        primary key (id)
);

comment on column comment.parent_id is '父类的id';

comment on column comment.type is '父类的类型，0：问题的回复，1：回复的回复';

comment on column comment.commentator is '评论人id';

comment on column comment.gmt_create is '创建时间';

comment on column comment.gmt_modified is '更新时间';

comment on column comment.like_count is '点赞数';

