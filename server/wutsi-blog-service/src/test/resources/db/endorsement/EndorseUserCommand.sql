INSERT INTO T_USER(id, subscriber_count, name, email, full_name, picture_url, website_url, login_count, biography, blog)
VALUES (1, 1, 'ray.sponsible', 'ray.sponsible@gmail.com', 'Ray Sponsible', 'https://picture.com/ray.sponsible',
        'https://me.com/ray.sponsible', 5, 'Angel investor', true)
     , (2, 0, 'jane.doe', 'login@gmail.com', 'Jane Doe', 'https://picture.com/jane.doe', null, 1, null, false)
     , (3, 1, 'login.without.email', 'login.without.email@gmail.com', 'Login Without Email',
        'https://picture.com/login.without.email', null, 1, null, false)
     , (4, 1, 'subscribe.with.email', 'subscribe.with.email@gmail.com', 'Subscribe with Email', null, null, 1, null,
        false)
;

INSERT INTO T_EVENT(id, stream_id, type, entity_id, user_id, version, payload)
VALUES ('event-100', 11, 'urn:wutsi:blog:event:user-endorsed', '1', '3', 1, '{}')
;


INSERT INTO T_ENDORSEMENT(user_fk, endorser_fk)
VALUES (1, 3)
;
