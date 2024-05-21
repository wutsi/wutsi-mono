INSERT INTO T_USER(id, super_user, name, email, full_name, picture_url, login_count, active)
VALUES (1, false, 'ray.sponsible', 'ray.sponsible@gmail.com', 'Ray Sponsible', 'https://picture.com/ray.sponsible', 5,
        true),
       (2, false, 'roger.milla', 'roger.milla@gmail.com', 'Roger Milla', 'https://picture.com/roger.milla', 5, true),
       (99, false, 'user99', 'roger.milla99@gmail.com', 'Roger Milla', 'https://picture.com/roger.milla', 5, false)
;

INSERT INTO T_TAG(id, name, display_name)
VALUES (1, 'trump', 'Trump'),
       (2, 'politics', 'Politics'),
       (3, 'CAN', 'CAN-2021')
;

INSERT INTO T_CATEGORY(id, level, title, long_title, parent_fk)
VALUES (1000, 0, 'Literature', 'Literature', null),
       (1001, 1, 'Autobiography', 'Literature > Autobiography', 1000),
       (1002, 1, 'Romance', 'Literature > Romance', 1000)
;

INSERT INTO T_STORY(id, user_fk, category_fk, language, status, deleted, title, summary, thumbnail_url, published_date_time)
VALUES (1, 1, 1001, 'en', 1, false, 'CAN: Cameroon vs. Argentina: 1-0.', 'This is an historic day..','https://img.com/1.png', '2020-08-04 08:07:44'),
       (2, 2, 1002, 'fr', 1, false, 'Trump de retour', 'Trump re-elu president une fois de plus!','https://img.com/2.png', '2020-09-04 08:07:44'),
       (3, 2, null, null, 0, false, 'Draft stories', null, null, '2021-08-04 08:07:44'),
       (9, 1, null, 'en', 1, true, 'Deleted', '', null, '2022-08-04 08:07:44'),
       (99, 99, null, 'fr', 1, false, 'Not active', '', 'https://img.com/2.png', '2020-09-04 08:07:44')
;

INSERT INTO T_STORY_TAG(story_fk, tag_fk)
VALUES (1, 3),
       (2, 1),
       (2, 2)
;

