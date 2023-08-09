INSERT INTO T_USER(id, super_user, name, email, full_name, picture_url, login_count) VALUES
    (1, false, 'ray.sponsible', 'ray.sponsible@gmail.com', 'Ray Sponsible', 'https://picture.com/ray.sponsible', 5),
    (2, false, 'roger.milla', 'roger.milla@gmail.com', 'Roger Milla', 'https://picture.com/roger.milla', 5)
;

INSERT INTO T_STORY(id, user_fk, topic_fk, language, status, deleted, title, summary, thumbnail_url, published_date_time) VALUES
    (1,  1, 101, 'en', 1,  false, 'CAN: Cameroon vs. Argentina: 1-0.', 'This is an historic day..', 'https://img.com/1.png', '2020-08-04 08:07:44'),
    (2,  2, 202, 'fr', 1,  false, 'Trump de retour', 'Trump re-elu president une fois de plus!', 'https://img.com/2.png', '2020-09-04 08:07:44'),
    (3,  2, null, null, 0, false, 'Draft stories', null, null, '2021-08-04 08:07:44'),
    (99, 1, 201, 'en', 1,  true,  'Deleted', '', null, '2022-08-04 08:07:44')
;

INSERT INTO T_READER(story_id, user_id, commented, liked, subscribed) VALUES
    (1, 2, true, true, false),
    (2, 2, false, false, false),
    (3, 1, true, true, true)
;
