INSERT INTO T_USER(id, subscriber_count, name, email, full_name, picture_url, website_url, login_count, biography, blog)
VALUES (100, 1, 'ray.sponsible', 'ray.sponsible@gmail.com', 'Ray Sponsible', 'https://picture.com/ray.sponsible',
        'https://me.com/ray.sponsible', 5, 'Angel investor', true)
     , (200, 0, 'jane.doe', 'login@gmail.com', 'Jane Doe', 'https://picture.com/jane.doe', null, 1, null, false)
     , (300, 1, 'login.without.email', 'login.without.email@gmail.com', 'Login Without Email',
        'https://picture.com/login.without.email', null, 1, null, false)
;

INSERT INTO T_READER(id, user_id, story_id, commented, liked, subscribed, email)
VALUES (1001, 100, 10, true, true, true, false),
       (1002, 100, 11, false, true, true, false),
       (2001, 200, 11, false, false, true, true),
       (3001, 300, 12, false, false, false, true)
;

INSERT INTO T_SUBSCRIPTION(user_fk, subscriber_fk, timestamp)
VALUES (100, 200, now()),
       (100, 300, now())
;
