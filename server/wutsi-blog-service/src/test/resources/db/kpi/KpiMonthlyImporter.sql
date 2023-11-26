INSERT INTO T_USER(id, super_user, name, email, full_name)
VALUES (111, false, 'john111', 'john111.partner@gmail.com', 'Jane Doe'),
       (211, false, 'john200', 'john200.partner@gmail.com', 'Yo Man'),
       (311, false, 'john300', 'john300.partner@gmail.com', 'Ray'),
       (411, false, 'john400', 'john400.partner@gmail.com', '???')
;

INSERT INTO T_STORY(id, user_fk, title, tagline, summary, language, status, read_count, total_duration_seconds,
                    click_count)
VALUES (100, 111, 'Story100', 'Sample Tagline', 'This is summary', 'en', 1, 1000, 1001, 0),
       (101, 111, 'Story101', 'Sample Tagline', 'This is summary', 'en', 1, 11, 1011, 1),
       (200, 211, 'Story200', 'Sample Tagline', 'This is summary', 'en', 1, 2000, 2001, 0)
;

INSERT INTO T_STORY_KPI(story_id, type, year, month, value)
VALUES (200, 1, YEAR(now()), MONTH(now()) + 1, 11),
       (200, 4, YEAR(now()), MONTH(now()) + 1, 700),
       (200, 5, YEAR(now()), MONTH(now()) + 1, 7)
;

INSERT INTO T_USER_KPI(user_id, type, year, month, value)
VALUES (211, 1, YEAR(now()), MONTH(now()), 555),
       (211, 3, YEAR(now()), MONTH(now()), 888),
       (211, 4, YEAR(now()), MONTH(now()), 300),
       (211, 5, YEAR(now()), MONTH(now()), 11)
;

INSERT INTO T_SUBSCRIPTION(user_fk, subscriber_fk, timestamp, story_fk)
VALUES (111, 211, now(), 100),
       (111, 311, now(), 100),
       (111, 411, date_sub(NOW(), interval 1 month), null),

       (211, 111, now(), null)
;

INSERT INTO T_LIKE_V2(story_fk, user_fk, device_id, timestamp)
VALUES (100, 111, null, now()),
       (100, null, 'device-search', now()),
       (101, null, 'device-search', now()),
       (200, null, 'device-search', now())
;

INSERT INTO T_COMMENT_V2(story_fk, user_fk, text, timestamp)
VALUES (100, 111, 'event-100', now()),
       (100, 211, 'event-100', now()),
       (100, 311, 'event-100', now()),
       (100, 411, 'event-100', now()),

       (101, 411, 'event-100', now())
;
