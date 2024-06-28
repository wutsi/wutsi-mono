INSERT INTO T_USER(id, super_user, name, email, full_name, wpp, country)
VALUES (111, false, 'john111', 'herve.tchepannou@gmail.com', 'Jane Doe', true, 'cm'),
       (211, false, 'john200', 'tchbansi@hotmail.com', 'Yo Man', true, 'cm'),
       (311, false, 'john300', 'john300.partner@gmail.com', 'Ray', true, 'cm'),
       (411, false, 'john400', 'john400.partner@gmail.com', '???', false, 'cm')
;

INSERT INTO T_WALLET(id, user_fk, currency, country, account_number)
VALUES ('111', 111, 'XAF', 'cm', '+237670000000'),
       ('211', 211, 'XAF', 'cm', '+237670000001'),
       ('311', 311, 'XAF', 'cm', '+237670000002'),
       ('411', 411, 'XAF', 'cm', '+237670000003')
;

UPDATE T_USER U JOIN T_WALLET W on U.id = W.user_fk
SET U.wallet_id=W.id;

INSERT INTO T_STORY(id, user_fk, wpp_score, title, status)
VALUES (100, 111, 100, 'Story100', 1),
       (200, 211, 90, 'Story200', 1),
       (201, 211, 50, 'Story201', 1),
       (202, 211, 100, 'Story202', 1),
       (300, 311, 90, 'Story300', 1),
       (400, 411, 90, 'Story400', 1)
;

INSERT INTO T_STORY_KPI(story_id, type, year, month, value, source)
VALUES (100, 1, 2020, 1, 100, 0),
       (100, 4, 2020, 1, 400, 0),
       (100, 6, 2020, 1, 100, 0),
       (100, 5, 2020, 1, 10, 0),
       (100, 9, 2020, 1, 15, 0),
       (100, 10, 2020, 1, 5, 0),

       (200, 1, 2020, 1, 475, 0),
       (200, 4, 2020, 1, 300, 0),
       (200, 6, 2020, 1, 475, 0),
       (200, 9, 2020, 1, 60, 0),
       (200, 10, 2020, 1, 30, 0),

       (201, 1, 2020, 1, 50, 0),
       (201, 4, 2020, 1, 100, 0),
       (201, 6, 2020, 1, 40, 0),
       (201, 5, 2020, 1, 1, 0),
       (201, 9, 2020, 1, 5, 0),
       (201, 10, 2020, 1, 1, 0),

       (202, 1, 2020, 1, 75, 0),
       (202, 4, 2020, 1, 75, 0),
       (202, 6, 2020, 1, 60, 0),
       (202, 5, 2020, 1, 2, 0),
       (202, 9, 2020, 1, 2, 0),

       (300, 1, 2020, 1, 350, 0),
       (300, 4, 2020, 1, 200, 0),
       (300, 6, 2020, 1, 300, 0)
;

