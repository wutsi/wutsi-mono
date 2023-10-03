INSERT INTO T_USER(id, super_user, name, email, full_name)
VALUES (111, false, 'john111', 'john111.partner@gmail.com', 'Jane Doe'),
       (211, false, 'john200', 'john200.partner@gmail.com', 'Yo Man'),
       (311, false, 'john300', 'john300.partner@gmail.com', 'Ray'),
       (411, false, 'john400', 'john400.partner@gmail.com', '???')
;

INSERT INTO T_STORY(id, user_fk, title, tagline, summary, language, status, read_count)
VALUES (100, 111, 'Story100', 'Sample Tagline', 'This is summary', 'en', 1, 1000),
       (101, 111, 'Story101', 'Sample Tagline', 'This is summary', 'en', 1, 11),
       (200, 211, 'Story200', 'Sample Tagline', 'This is summary', 'en', 1, 2000)
;

INSERT INTO T_STORY_KPI(story_id, type, year, month, value)
VALUES (200, 1, YEAR(now()), MONTH(now()) + 1, 11)
;

INSERT INTO T_USER_KPI(user_id, type, year, month, value)
VALUES (211, 1, YEAR(now()), MONTH(now()), 555),
       (211, 3, YEAR(now()), MONTH(now()), 555)
;

INSERT INTO T_SUBSCRIPTION(user_fk, subscriber_fk, timestamp)
VALUES (111, 211, now()),
       (111, 311, now()),
       (111, 411, date_sub(NOW(), interval 1 day)),

       (211, 111, now())
;
