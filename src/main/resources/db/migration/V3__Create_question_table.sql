CREATE TABLE question
(
	id INT AUTO_INCREMENT NOT NULL,
	title VARCHAR(50),
	description TEXT,
	creator INT,
	comment_count INT DEFAULT 0,
	view_count INT DEFAULT 0,
	like_count INT DEFAULT 0,
	tag VARCHAR(256),
    gmt_create BIGINT,
    gmt_modified BIGINT,
	CONSTRAINT question_pk
		PRIMARY KEY (id)
);