ALTER TABLE T_SHARE ADD COLUMN device_id VARCHAR(36) NOT NULL ;
ALTER TABLE T_SHARE MODIFY COLUMN user_fk BIGINT NULL;