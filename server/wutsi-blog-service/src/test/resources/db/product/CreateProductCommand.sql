INSERT INTO T_USER(id, super_user, name, email, full_name)
VALUES (100, false, 'john1', 'john1.partner@gmail.com', 'Jane Doe'),
       (200, false, 'john2', 'john2.partner@gmail.com', 'Jane Doe'),
       (300, false, 'john3', 'john3.partner@gmail.com', 'Jane Doe');

INSERT INTO T_STORE(id, user_fk, currency)
VALUES ('1', 100, 'XAF');

INSERT INTO T_CATEGORY(id, level, title, long_title, parent_fk)
VALUES (1000, 0, 'Literature', 'Literature', null),
       (1001, 1, 'Autobiography', 'Literature > Autobiography', 1000);
