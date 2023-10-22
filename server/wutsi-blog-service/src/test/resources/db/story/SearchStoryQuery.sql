INSERT INTO T_USER(id, super_user, name, email, full_name, picture_url, login_count, active)
VALUES (1, false, 'ray.sponsible', 'ray.sponsible@gmail.com', 'Ray Sponsible', 'https://picture.com/ray.sponsible', 5,
        true)
     , (2, false, 'john.smith', 'john.smith@gmail.com', 'Jane Doe', 'https://picture.com/login', 1, false)
     , (10, false, 'john.partner', 'john.partner@gmail.com', 'Jane Doe', 'https://picture.com/login', 1, true)

     , (99, true, 'ze.god', 'ze.god@gmail.com', 'Ze Got', 'https://picture.com/login', 1, true)
;

INSERT INTO T_ACCOUNT(id, provider_fk, user_fk, provider_user_id, login_count, last_login_date_time)
VALUES (10, 1, 1, 'ray.sponsible', 1, '2018-01-01')
     , (20, 1, 2, 'jane.doe', 4, '2018-01-01')
     , (99, 1, 99, 'ze.god', 4, '2018-01-01')
;

INSERT INTO T_SESSION(account_fk, run_as_user_fk, access_token, refresh_token, login_date_time, logout_date_time)
VALUES (10, null, 'session-ray', null, now(), null)
     , (20, null, 'session-john', null, now(), null)
     , (99, 1, 'session-ze', null, now(), null)
;

INSERT INTO T_STORY(id, user_fk, title, tagline, summary, thumbnail_url, source_url, language, status,
                    published_date_time, word_count, reading_minutes, readability_score)
VALUES (1, 1, 'This is a story.', null,
        'This is the toolkit library from which all other modules inherit functionality. It also includes the core facades for the Tika API.',
        null, null, 'en', 0, null, 11, 1, 1)
     , (2, 1, 'Sample Story', 'Sample Tagline', 'This is summary', 'https://www.img.com/goo.png',
        'https://www.test.com/1/1/test.txt', 'en', 1, '2018-01-30', 1430, 7, 2)
     , (3, 1, 'Translate me', 'Sample Tagline', 'This is summary', 'https://www.img.com/goo.png',
        'https://www.test.com/1/1/test.txt', 'en', 1, '2018-01-30', 1430, 7, 2)

     , (10, 2, 'Sample Story 10', null, 'This is summary 10', 'https://www.img.com/10.png', null, 'en', 0, null, 1434,
        7, 10)
     , (11, 2, 'Sample Story 11', null, 'This is summary 11', 'https://www.img.com/11.png', null, 'en', 1, '2018-01-30',
        200, 1, 11)
     , (12, 2, 'Sample Story 12', null, 'This is summary 12', 'https://www.img.com/12.png',
        'https://www.test.com/1/1/test-12.txt', 'en', 0, null, 150, 1, 12)
     , (13, 2, null, null, null, null, null, 'en', 0, null, 150, 1, 13)
     , (14, 2, null, null, null, null, null, 'en', 0, null, 150, 1, 14)
     , (15, 2, null, null, null, null, null, 'en', 0, null, 150, 1, 15)
     , (16, 2, null, null, null, null, null, 'en', 0, null, 150, 1, 16)
     , (17, 2, null, null, null, null, null, 'en', 0, null, 150, 1, 17)
     , (18, 2, 'Sample Story 18', null, 'This is summary 18', 'https://www.img.com/18.png', null, 'en', 1, '2020-01-30',
        1200, 6, 18)

     , (20, 1, 'To Publish', null, 'This is summary 20', 'https://www.img.com/20.png', null, 'en', 0, null, 1200, 6, 20)
     , (21, 1, 'To Re-Publish', null, 'This is summary 21', 'https://www.img.com/21.png', null, 'en', 1, '2015-01-30',
        1200, 6, 21)
     , (22, 1, null, null, 'This is summary 21', 'https://www.img.com/21.png', null, 'en', 1, '2015-01-30', 1200, 6, 22)
     , (23, 1, 'To Re-Publish', null, null, 'https://www.img.com/21.png', null, 'en', 1, '2015-01-30', 1200, 6, 23)
     , (24, 10, 'To Publish - WPP', null, 'This is summary 20', 'https://www.img.com/20.png', null, 'en', 0, null, 1200,
        6, 20)
     , (25, 10, 'Schedule Publish #1', null, null, null, null, 'en', 0, null, 1200, 6, 20)

     , (30, 1, 'Already-Imported', null, null, 'https://www.img.com/21.png',
        'https://nkowa.com/podcast-jokkolabs-douala-les-valeurs-qui-nous-tiennent-a-coeur-sont-la-culture-lagriculture-et-linnovation',
        'en', 1, '2015-01-30', 1200, 6, 30)

     , (40, 1, 'WPP', null, null, 'https://www.img.com/21.png',
        'https://nkowa.com/podcast-jokkolabs-douala-les-valeurs-qui-nous-tiennent-a-coeur-sont-la-culture-lagriculture-et-linnovation',
        'en', 1, '2015-01-30', 1200, 6, 30)

     , (90, 1, 'Delete Me', null, null, null, null, 'en', 1, '2015-01-30', 1200, 6, 30)
     , (99, 1, 'Deleted', null, null, null, null, 'en', 1, '2015-01-30', 1200, 6, 30)
;

UPDATE T_STORY
SET topic_fk=101,
    wpp_status=1,
    wpp_rejection_reason='offensive',
    wpp_modification_date_time=now(),
    scheduled_publish_date_time='2040-01-02',
    access = 2
WHERE id = 2;

UPDATE T_STORY
SET read_count=2000
WHERE id = 11;

UPDATE T_STORY
SET read_count=200
WHERE id = 18;

UPDATE T_STORY
SET source_url_hash='bac453c63189f63093efe20cc991b127'
WHERE id = 30;
UPDATE T_STORY
SET live= true,
    live_date_time=NOW()
WHERE id IN (11, 18);
UPDATE T_STORY
SET deleted= true,
    deleted_date_time=NOW()
WHERE id = 99;


INSERT INTO T_STORY_CONTENT(id, story_fk, content_type, language, content, modification_date_time)
VALUES (1, 1, 'text/plain', 'en', 'Hello', now())
     , (2, 2, 'text/plain', 'en', 'World', now())
     , (30, 3, 'application/editorjs', 'en', '{"time":1584718404278, "blocks":[]}', now())
     , (31, 3, 'application/editorjs', 'fr', '{"time":1584718404278, "blocks":[]}', '2010-10-01')
     , (32, 3, 'application/editorjs', 'es', '{"time":1584718404278, "blocks":[]}', now())
;

INSERT INTO T_TAG(id, name, display_name, total_stories)
VALUES (1, 'covid-19', 'COVID-19', 100)
     , (2, 'github', 'Github', 1)
     , (3, 'git', 'Git', 102)
     , (4, 'gitflow', 'GitFlow', 7)
     , (5, 'test', 'Test', 0)
     , (6, 'computer', 'Computer', 0)
;

INSERT INTO T_STORY_TAG(story_fk, tag_fk)
VALUES (2, 1)
     , (2, 4)
     , (1, 4)
;
