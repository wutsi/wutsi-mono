INSERT INTO T_USER(id, name, email, full_name, picture_url, login_count) VALUES
    (1, 'ray.sponsible', 'ray.sponsible@gmail.com', 'Ray Sponsible', 'https://picture.com/ray.sponsible', 5),
    (2, 'john.smith', 'herve.tchepannou@gmail.com', 'John Smith', 'https://picture.com/login', 1),
    (3, 'not-whitelisted', 'user-not-whitelisted@gmail.com', 'John Smith', 'https://picture.com/login', 1),
    (4, 'no-email', null, 'John Smith', 'https://picture.com/login', 1),
    (5, 'alread-sent', 'already-sent@gmail.com', 'Jane Doe', 'https://picture.com/login', 1)
;

INSERT INTO T_ACCOUNT(id, provider_fk, user_fk, provider_user_id, login_count, last_login_date_time) VALUES
    (10, 1, 1, 'ray.sponsible', 1, '2018-01-01')
;

INSERT INTO T_SESSION(account_fk, run_as_user_fk, access_token, refresh_token, login_date_time, logout_date_time) VALUES
    (10, null, 'session-ray', null, now(), null)
;

INSERT INTO T_STORY(id, user_fk, topic_fk, status, published_date_time, title, tagline, summary, language) VALUES
    (10, 1, 101, 1, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 'The war in Ukraine create a new front in world domination', 'This is an exemple of tagline', 'This is summary', 'en'),
    (20, 2, 101, 1, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 'Roger Milla marque 10 buts!', null, 'This is summary', 'fr'),
    (30, 1, 101, 0, null, 'Sample Story', 'Sample Tagline', 'This is summary', 'en')
;

INSERT INTO T_STORY_CONTENT(story_fk, content_type, language, content, modification_date_time) VALUES
    (10, 'application/editorjs', 'en', '{"time":1584718404278, "blocks":[]}', now()),
    (20, 'application/editorjs', 'en', '{"time":1584718404278, "blocks":[]}', now())
;

INSERT INTO T_SUBSCRIPTION(user_fk, subscriber_fk) VALUES
    (1,  2),
    (1,  3),
    (1,  4),
    (1,  5)
;

INSERT INTO T_EVENT(id, stream_id, type, entity_id, user_id, device_id, version) VALUES
    ('100', 7, 'urn:wutsi:blog:event:story-daily-email-sent', '10', '5', null,  1)
;
