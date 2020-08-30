CREATE TABLE notification
(
    id BIGINT AUTO_INCREMENT,
    notifier BIGINT NOT NULL,
    receiver BIGINT NOT NULL,
    outer_id BIGINT NOT NULL,
    type INT,
    status INT DEFAULT 0 NOT NULL,
    gmt_create BIGINT NOT NULL,
    CONSTRAINT notification_pk
        PRIMARY KEY (id)
);