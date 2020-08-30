CREATE TABLE comment
(
    id BIGINT AUTO_INCREMENT,
    parent_id BIGINT NOT NULL,
    type INT NOT NULL,
    commentator INT NOT NULL,
    like_count BIGINT NOT NULL DEFAULT 0,
    gmt_create BIGINT NOT NULL,
    gmt_modified BIGINT NOT NULL,
    CONSTRAINT comment_pk
        PRIMARY KEY (id)
);

