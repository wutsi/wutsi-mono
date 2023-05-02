INSERT INTO T_USER(id, super_user, name, email, full_name, picture_url, login_count) VALUES
    (1, false, 'ray.sponsible', 'ray.sponsible@gmail.com', 'Ray Sponsible', 'https://picture.com/ray.sponsible', 5)
  , (2, false, 'john.smith', 'john.smith@gmail.com', 'Jane Doe', 'https://images.pexels.com/photos/220453/pexels-photo-220453.jpeg', 1)
  , (3, false, 'john.partner', 'john.partner@gmail.com', 'Jane Partner', null, 1)
  , (4, false, 'no.email', null, 'Jane Doe', 'https://picture.com/login', 1)
;

INSERT INTO T_STORY(id, user_fk, title, tagline, summary, thumbnail_url, source_url, language, status, published_date_time, word_count, reading_minutes, readability_score) VALUES
    (1, 1, 'Test', null, 'Sample Summary', null, null, 'en', 0, null, 11, 1, 1)
;

INSERT INTO T_COMMENT(id, story_fk, user_fk, text) VALUES
    (10, 1, 2, 'Hello')
  , (11, 1, 3, 'World')
  , (12, 1, 4, 'Whatzupp?')
  , (13, 1, 2, 'Oups....')
;
