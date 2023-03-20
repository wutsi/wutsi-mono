INSERT INTO T_PASSWORD(id, account_id, username, value, salt, is_deleted, deleted)
    VALUES
        (100, 100, '+1237670000000', '7ea74928036112ac8985c2220bccb82f', 'spicy stuff', false, null),
        (999, 100, '+1237670000099', '12343', 'hot pepper', true, now());
