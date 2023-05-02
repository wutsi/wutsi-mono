INSERT INTO T_USER(id, name, email, full_name, picture_url, login_count) VALUES
    (1, 'ray.sponsible', 'ray.sponsible@gmail.com', 'Ray Sponsible', 'https://picture.com/ray.sponsible', 5)
  , (2, 'john.smith', 'john.smith@gmail.com', 'Jane Doe', 'https://picture.com/login', 1)

;


INSERT INTO T_STORY(id, user_fk, title, summary, thumbnail_url, source_url, language, status, published_date_time, word_count, reading_minutes) VALUES
    (1, 1, 'Test', null, null, null, 'en', 0, null, 11, 1)
  , (2, 1, 'Sample Story', 'This is summary', 'http://www.img.com/goo.png', 'http://www.test.com/1/1/test.txt', 'en', 1, '2018-01-30', 1430, 7)

  , (10, 2, 'Sample Story 10', 'This is summary 10', 'http://www.img.com/10.png', null, 'en', 0, null, 1434, 7)
;

INSERT INTO T_TAG(id, name, display_name, total_stories) VALUES
    (1, 'covid-19', 'COVID-19', 0)
  , (2, 'github', 'Github', 0)
  , (3, 'git', 'Git', 0)
  , (4, 'gitflow', 'GitFlow', 0)
;

INSERT INTO T_STORY_TAG(story_fk, tag_fk) VALUES
    (2, 1)
  , (2, 4)

  , (1, 1)
  , (1, 4)

  , (10, 1)
  , (10, 3)
;
