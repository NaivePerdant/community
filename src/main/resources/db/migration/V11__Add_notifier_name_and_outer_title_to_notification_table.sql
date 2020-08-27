alter table NOTIFICATION
    add NOTIFIER_NAME varchar(100) after NOTIFIER;

alter table NOTIFICATION
    add OUTER_TITLE varchar(256) after OUTER_ID;