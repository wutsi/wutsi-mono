INSERT INTO T_USER(id, super_user, name, email, full_name, picture_url, login_count) VALUES
    (1, false, 'ray.sponsible', 'ray.sponsible@gmail.com', 'Ray Sponsible', 'https://picture.com/ray.sponsible', 5),
    (2, false, 'roger.milla', 'roger.milla@gmail.com', 'Roger Milla', 'https://picture.com/roger.milla', 5)
;

INSERT INTO T_TAG(id, name, display_name) VALUES
    (1, 'trump', 'Trump'),
    (2, 'politics', 'Politics'),
    (3, 'CAN', 'CAN-2021')
;

INSERT INTO T_STORY(id, user_fk, topic_fk, language, status, deleted, title, summary, thumbnail_url) VALUES
    (1,  1, 101, 'en', 1,  false, 'CAN: Cameroon vs. Argentina: 1-0.', 'This is an historic day..', 'https://img.com/1.png'),
    (2,  2, 202, 'fr', 1,  false, 'Trump de retour', 'Trump re-elu president une fois de plus!', 'https://img.com/2.png'),
    (3,  2, null, null, 0, false, 'Draft stories', null, null),
    (99, 1, 201, 'en', 1,  true,  'Deleted', '', null)
;

INSERT INTO T_STORY_TAG(story_fk, tag_fk) VALUES
    (1, 3),
    (2, 1),
    (2, 2)
;

