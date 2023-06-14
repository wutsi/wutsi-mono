INSERT INTO T_USER(id, subscriber_count, name, email, full_name, picture_url, website_url, login_count, biography, blog) VALUES
    (1, 2, 'ray.sponsible', 'ray.sponsible@gmail.com', 'Ray Sponsible', 'https://picture.com/ray.sponsible', 'https://me.com/ray.sponsible', 5, 'Angel investor', true)
  , (2, 0, 'jane.doe', 'login@gmail.com', 'Jane Doe', 'https://picture.com/jane.doe', null, 1, null, false)
  , (3, 1, 'login.without.email', 'login.without.email@gmail.com', 'Login Without Email', 'https://picture.com/login.without.email', null, 1, null, false)
;


INSERT INTO T_EVENT(id, stream_id, type, entity_id, user_id, version, payload) VALUES
    ('event-100', 4, 'urn:wutsi:blog:event:subscribed', '1', '2', 1, '{}'),
    ('event-200', 4, 'urn:wutsi:blog:event:subscribed', '1', '3', 2, '{}'),
    ('event-400', 4, 'urn:wutsi:blog:event:subscribed', '3', '2', 4, '{}')
;

INSERT INTO T_SUBSCRIPTION(user_fk, subscriber_fk) VALUES
  (1,  2),
  (1,  3),

  (3,  2)
;
