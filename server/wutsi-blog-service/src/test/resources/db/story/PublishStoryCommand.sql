INSERT INTO T_USER(id, super_user, name, email, full_name, picture_url, login_count, wpp)
VALUES (1, false, 'ray.sponsible', 'ray.sponsible@gmail.com', 'Ray Sponsible', 'https://picture.com/ray.sponsible', 5,
        false)
     , (2, false, 'john.smith', 'john.smith@gmail.com', 'Jane Doe', 'https://picture.com/login', 1, false)
     , (10, false, 'wpp', 'wpp@gmail.com', 'Jane Doe', 'https://picture.com/login', 1, true)

     , (99, true, 'ze.god', 'ze.god@gmail.com', 'Ze Got', 'https://picture.com/login', 1, false)
;

INSERT INTO T_ACCOUNT(id, provider_fk, user_fk, provider_user_id, login_count, last_login_date_time)
VALUES (10, 1, 1, 'ray.sponsible', 1, '2018-01-01')
     , (20, 1, 2, 'jane.doe', 4, '2018-01-01')
     , (30, 1, 10, 'wpp', 4, '2018-01-01')
     , (99, 1, 99, 'ze.god', 4, '2018-01-01')
;

INSERT INTO T_SESSION(account_fk, run_as_user_fk, access_token, refresh_token, login_date_time, logout_date_time)
VALUES (10, null, 'session-ray', null, now(), null)
     , (20, null, 'session-john', null, now(), null)
     , (30, null, 'session-wpp', null, now(), null)
     , (99, 1, 'session-ze', null, now(), null)
;

INSERT INTO T_STORY(id, user_fk, topic_fk, title, tagline, summary, thumbnail_url, source_url, language, status,published_date_time, word_count, reading_minutes, readability_score)
VALUES (1, 1, 100, 'Draft', null, null, null, null, 'en', 0, null, 11, 1, 0)
     , (2, 1, 100, 'Published', 'Sample Tagline', 'This is summary', 'https://www.img.com/goo.png','https://www.test.com/1/1/test.txt', 'en', 1, '2018-01-30', 1430, 7, 2)
     , (3, 1, 100, 'Schedule', 'Sample Tagline', 'This is summary', 'https://www.img.com/goo.png','https://www.test.com/1/1/test.txt', 'en', 0, '2018-01-30', 1430, 7, 2)
     , (4, 1, 100, 'Draft', 'Sample Tagline', 'This is summary', 'https://www.img.com/goo.png','https://www.test.com/1/1/test.txt', 'en', 0, '2018-01-30', 1430, 7, 2)
     , (5, 1, 100, 'Draft', 'Sample Tagline', null, 'https://www.img.com/goo.png','https://www.test.com/1/1/test.txt', 'en', 0, '2018-01-30', 1430, 7, 2)
     , (20, 2, 100, 'Published', 'Sample Tagline', 'This is summary', 'https://www.img.com/goo.png','https://www.test.com/1/1/test.txt', 'en', 1, '2018-01-30', 1430, 7, 2)
     , (99, 1, 100, 'Deleted', null, null, null, null, 'en', 1, '2015-01-30', 1200, 6, 30)
;

UPDATE T_STORY
SET deleted= true,
    deleted_date_time=NOW()
WHERE id = 99;


INSERT INTO T_STORY_CONTENT(id, story_fk, content_type, language, content, modification_date_time)
VALUES (1, 1, 'application/editorjs', 'en', '{"time":1584718404278, "blocks":[]}', now())
     , (2, 2, 'application/editorjs', 'en', '{"time":1584718404278, "blocks":[]}', now())
     , (5, 5, 'application/editorjs', 'en', '{"time":1584718404278, "blocks":[]}', now())
     , (30, 3, 'application/editorjs', 'en', '{"time":1584718404278, "blocks":[]}', now())
     , (31, 3, 'application/editorjs', 'fr', '{"time":1584718404278, "blocks":[]}', '2010-10-01')
     , (32, 3, 'application/editorjs', 'es', '{"time":1584718404278, "blocks":[]}', now())
;

INSERT INTO T_TAG(id, name, display_name, total_stories)
VALUES (1, 'covid-19', 'COVID-19', 100)
     , (2, 'github', 'Github', 1)
     , (3, 'git', 'Git', 102)
     , (4, 'gitflow', 'GitFlow', 7)
     , (6, 'computer', 'Computer', 0)
;

INSERT INTO T_STORY_TAG(story_fk, tag_fk)
VALUES (2, 1)
     , (2, 4)

     , (1, 4)

     , (4, 1)
;

INSERT INTO T_CATEGORY(id, parent_fk, level, title, long_title, title_french, title_french_ascii)
VALUES (1100, null, 0, 'Electronics', '', 'Électronique', 'Electronique'),
       (1110, 1100, 1, 'Computers', '', 'Ordinateurs', null),
       (1111, 1100, 1, 'Laptops', '', 'Portables', null),
       (1112, 1100, 1, 'Tablets', '', 'Tablettes', null),
       (1114, 1100, 1, 'Monitors', '', 'Écrans', 'Ecrans'),
       (1115, 1100, 1, 'Printers', '', 'Imprimantes', null),
       (1116, 1100, 1, 'Routers', '', 'Routeurs', null),
       (1117, 1100, 1, 'Projectors', '', 'Projecteurs', null),
       (1130, 1100, 1, 'Cameras', '', 'Appareil Photos', null),
       (1140, 1100, 1, 'TV', '', 'TV', null),
       (1141, 1100, 1, 'Speakers', '', 'Haut-Parleurs', null),
       (1150, 1100, 1, 'Cell Phones', '', 'Téléphones portables', 'Telephones portables'),
       (1151, 1100, 1, 'Headphones', '', 'Écouteurs', 'Ecouteurs'),
       (1152, 1100, 1, 'Cases and Covers', '', 'Étuis et Housses', 'Etuis et Housses'),
       (1153, 1100, 1, 'SIM Cards', '', 'Cartes SIM', null),
       (1154, 1100, 1, 'Chargers', '', 'Chargeurs', null),
       (1160, 1100, 1, 'Electronic Accessories', '', 'Accessoires Électronique', 'Accessoires Electronique'),

       (1200, null, 0, 'Beauty', '', 'Beauté', 'Beaute'),
       (1210, 1200, 1, 'Makeup', '', 'Maquillage', null),
       (1220, 1200, 1, 'Nails', '', 'Ongles', null)
