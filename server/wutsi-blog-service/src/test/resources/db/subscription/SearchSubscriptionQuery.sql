INSERT INTO T_USER(id, subscriber_count, name, email, full_name, picture_url, website_url, login_count, biography, blog)
VALUES (1, 1, 'ray.sponsible', 'ray.sponsible@gmail.com', 'Ray Sponsible', 'https://picture.com/ray.sponsible',
        'https://me.com/ray.sponsible', 5, 'Angel investor', true)
     , (2, 0, 'jane.doe', 'login@gmail.com', 'Jane Doe', 'https://picture.com/jane.doe', null, 1, null, false)
     , (3, 1, 'login.without.email', 'login.without.email@gmail.com', 'Login Without Email',
        'https://picture.com/login.without.email', null, 1, null, false)
;


INSERT INTO T_EVENT(id, stream_id, type, entity_id, user_id, version, payload)
VALUES ('event-100', 4, 'urn:wutsi:blog:event:subscribed', '1', '3', 1, '{}'),
       ('event-400', 4, 'urn:wutsi:blog:event:subscribed', '3', '2', 4, '{}')
;

INSERT INTO T_SUBSCRIPTION(user_fk, subscriber_fk, timestamp)
VALUES (1, 3, now()),
       (1, 2, DATE_SUB(now(), interval 1 day)),

       (3, 2, DATE_SUB(now(), interval 2 day))
;
