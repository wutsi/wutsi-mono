INSERT INTO T_USER(id, super_user, name, email, full_name, picture_url, login_count) VALUES
  (111, false, 'john111', 'john111.partner@gmail.com', 'Jane Doe', 'https://picture.com/login', 1)
;

INSERT INTO T_STORY(id, user_fk, title, tagline, summary, thumbnail_url, source_url, language, status, published_date_time, word_count, reading_minutes, readability_score) VALUES
  (100, 111, 'Story100', 'Sample Tagline', 'This is summary', 'http://www.img.com/goo.png', 'http://www.test.com/1/1/test.txt', 'en', 1, '2018-01-30', 1430, 7, 2),
  (200, 111, 'Story101', 'Sample Tagline', 'This is summary', 'http://www.img.com/goo.png', 'http://www.test.com/1/1/test.txt', 'en', 1, '2018-01-30', 1430, 7, 2)
;

INSERT INTO T_EVENT(id, stream_id, type, entity_id, user_id, device_id, version) VALUES
    ('event-100', 1, 'urn:wutsi:blog:event:story-liked', '100', null, 'device-1',  1),
    ('event-101', 1, 'urn:wutsi:blog:event:story-liked', '100', null, 'device-2',  2),
    ('event-102', 1, 'urn:wutsi:blog:event:story-liked', '100', null, 'device-3',  3),
    ('event-103', 1, 'urn:wutsi:blog:event:story-liked', '100', null, 'device-4',  4),
    ('event-104', 1, 'urn:wutsi:blog:event:story-liked', '200', '211', null,  5),
    ('event-105', 1, 'urn:wutsi:blog:event:story-liked', '200', null, 'device-unlike',  6),
    ('event-106', 1, 'urn:wutsi:blog:event:story-liked', '200', null, 'device-2',  7)
;

INSERT INTO T_LIKE_V2(story_fk, user_fk, device_id) VALUES
    (100, 111, null),
    (100, null, 'device-1'),
    (100, null, 'device-2'),
    (100, null, 'device-3'),
    (100, null, 'device-4'),
    (200, null, 'device-1'),
    (200, null, 'device-unlike')
;

INSERT INTO T_LIKE_STORY(story_fk, count)
    VALUES(100, 1000);
