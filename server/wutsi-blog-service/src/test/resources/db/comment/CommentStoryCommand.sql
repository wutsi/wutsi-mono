INSERT INTO T_USER(id, super_user, name, email, full_name) VALUES
  (111, false, 'john111', 'john111.partner@gmail.com', 'Jane Doe'),
  (211, false, 'john200', 'john200.partner@gmail.com', 'Yo Man')
;

INSERT INTO T_STORY(id, user_fk, title, tagline, summary, thumbnail_url, source_url, language, status, published_date_time) VALUES
  (100, 111, 'Story100', 'Sample Tagline', 'This is summary', 'http://www.img.com/goo.png', 'http://www.test.com/1/1/test.txt', 'en', 1, '2018-01-30'),
  (101, 111, 'Story101', 'Sample Tagline', 'This is summary', 'http://www.img.com/goo.png', 'http://www.test.com/1/1/test.txt', 'en', 1, '2018-01-30'),
  (200, 211, 'Story200', 'Sample Tagline', 'This is summary', 'http://www.img.com/goo.png', 'http://www.test.com/1/1/test.txt', 'en', 1, '2018-01-30')
;

INSERT INTO T_EVENT(id, stream_id, type, entity_id, user_id, version, payload) VALUES
    ('event-100', 3, 'urn:wutsi:command:comment-story', '100', '211', 1, '{}'),
    ('event-200', 3, 'urn:wutsi:command:comment-story', '100', '211', 2, '{}'),
    ('event-300', 3, 'urn:wutsi:command:comment-story', '100', '211', 3, '{}'),
    ('event-400', 3, 'urn:wutsi:command:comment-story', '100', '211', 4, '{}')
;

INSERT INTO T_COMMENT_V2(story_fk, user_fk, event_fk) VALUES
    (100, 211, 'event-100'),
    (100, 211, 'event-200'),
    (100, 211, 'event-300'),
    (100, 211, 'event-400')
;


INSERT INTO T_COMMENT_STORY(story_fk, count) VALUES(100, 1000);
