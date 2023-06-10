ALTER TABLE T_USER ADD COLUMN read_count BIGINT NOT NULL DEFAULT 0;

UPDATE T_USER U
    SET U.read_count = (
        SELECT COALESCE(SUM(S.read_count), 0) FROM T_STORY S WHERE U.id=S.user_fk
    );
