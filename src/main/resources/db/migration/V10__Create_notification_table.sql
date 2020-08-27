create table notification
(
    id bigint auto_increment,
    notifier bigint not null,
    receiver bigint not null,
    outer_id bigint not null,
    type int,
    status int default 0 not null,
    gmt_create bigint not null,
    constraint NOTIFICATION_PK
        primary key (ID)
);

comment on table notification is '通知表';

comment on column notification.id is '通知id';

comment on column notification.notifier is '通知发起人';

comment on column notification.receiver is '通知接收人';

comment on column notification.outer_id is '问题/回复 id';

comment on column notification.type is '回复的类型，1 问题；2 回复';

comment on column notification.status is '通知是否已读，0：未读，1：已读';