CREATE TABLE user
(
	id INT AUTO_INCREMENT NOT NULL,
	account_id VARCHAR(100),
	name VARCHAR(50),
	token CHAR(36),
	gmt_create BIGINT,
	gmt_modified BIGINT,
	CONSTRAINT user_pk
		PRIMARY KEY (id)
);