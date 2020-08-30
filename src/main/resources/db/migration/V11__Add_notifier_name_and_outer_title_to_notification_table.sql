ALTER TABLE notification
    ADD notifier_name VARCHAR(100) AFTER notifier;

ALTER TABLE notification
    ADD outer_title VARCHAR(256) AFTER outer_id;