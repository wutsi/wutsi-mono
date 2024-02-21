INSERT INTO T_USER(id, subscriber_count, name, email, full_name, picture_url, website_url, login_count, biography, blog, language) VALUES
    (1, 1, 'ray.sponsible', 'ray.sponsible@gmail.com', 'Ray Sponsible', 'https://picture.com/ray.sponsible', 'https://me.com/ray.sponsible', 5, 'Angel investor', true, null)
  , (2, 0, 'jane.doe', 'herve.tchepannou@gmail.com', 'Jane Doe', 'https://picture.com/jane.doe', null, 1, null, false, 'fr')
  , (3, 1, 'login.without.email', 'login.without.email@gmail.com', 'Login Without Email', 'https://picture.com/login.without.email', null, 1, null, false, null)
  , (4, 1, 'subscribe.with.email', 'subscribe.with.email@gmail.com', 'Subscribe with Email', null, null, 1, null, false, null)
;


INSERT INTO T_EVENT(id, stream_id, type, entity_id, user_id, version, payload) VALUES
    ('event-100', 4, 'urn:wutsi:blog:event:subscribed', '1', '3', 1, '{}'),
    ('event-400', 4, 'urn:wutsi:blog:event:subscribed', '3', '2', 4, '{}')
;

INSERT INTO T_SUBSCRIPTION(user_fk, subscriber_fk) VALUES
  (1,  3),

  (3,  2)
;

INSERT INTO T_STORY(id, user_fk, topic_fk, language, status, deleted, title, summary, thumbnail_url, published_date_time) VALUES
    (1,  1, 101, 'en', 1,  false, 'CAN: Cameroon vs. Argentina: 1-0.', 'This is an historic day..', 'https://img.com/1.png', '2020-08-04 08:07:44'),
    (2,  1, 101, 'en', 1,  false, 'Putin win the War', 'This is an historic day..', 'https://img.com/1.png', '2020-08-04 08:07:44'),
    (3,  1, 101, 'en', 1,  false, 'Biden love wars', 'This is an historic day..', 'https://img.com/1.png', '2020-08-04 08:07:44'),
    (4,  1, 101, 'en', 1,  false, 'Roger milla scores 10 goals vs Brazil', 'This is an historic day..', 'https://img.com/1.png', '2020-08-04 08:07:44'),
    (5,  1, 101, 'en', 1,  false, 'More is Less', 'This is an historic day..', 'https://img.com/1.png', '2020-08-04 08:07:44'),
    (6,  1, 101, 'en', 1,  false, 'Les joies de la censure', 'This is an historic day..', 'https://img.com/1.png', '2020-08-04 08:07:44')
;
