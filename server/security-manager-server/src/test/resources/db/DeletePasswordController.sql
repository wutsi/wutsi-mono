INSERT INTO T_PASSWORD(id, account_id, username, value, salt, is_deleted, deleted)
    VALUES
        (100, 100, '+1237670000000', '12343', 'spicy stuff', false, null),
        (999, 100, '+1237670000001', '12343', 'hot pepper', true, now());
