INSERT INTO T_USER(id, super_user, name, email, full_name, picture_url, login_count) VALUES
    (1, false, 'ray.sponsible', 'ray.sponsible@gmail.com', 'Ray Sponsible', 'https://picture.com/ray.sponsible', 5)
  , (2, false, 'john.smith', 'john.smith@gmail.com', 'Jane Doe', 'https://picture.com/login', 1)
  , (3, false, 'john.partner', 'john.partner@gmail.com', 'Jane Doe', 'https://picture.com/login', 1)

  , (99, true, 'ze.god', 'ze.god@gmail.com', 'Ze Got', 'https://picture.com/login', 1)
;

INSERT INTO T_STORY(id, user_fk, title, tagline, summary, thumbnail_url, source_url, language, status, published_date_time, word_count, reading_minutes, readability_score) VALUES
    (10, 1, 'This is a story.', null, 'This is the toolkit library from which all other modules inherit functionality. It also includes the core facades for the Tika API.', null, null, 'en', 0, null, 11, 1, 1)
  , (11, 1, 'Sample Story', 'Sample Tagline', 'This is summary', 'https://www.img.com/goo.png', 'https://www.test.com/1/1/test.txt', 'en', 1, '2018-01-30', 1430, 7, 2)
  , (12, 1, 'Translate me', 'Sample Tagline', 'This is summary', 'https://www.img.com/goo.png', 'https://www.test.com/1/1/test.txt', 'en', 1, '2018-01-30', 1430, 7, 2)
  , (13, 1, null, null, null, null, null, 'en', 0, null, 150, 1, 13)
  , (14, 1, null, null, null, null, null, 'en', 0, null, 150, 1, 14)

  , (20, 2, 'To Publish', null, 'This is summary 20', 'https://www.img.com/20.png', null, 'en', 0, null, 1200, 6, 20)
  , (21, 2, 'To Re-Publish', null, 'This is summary 21', 'https://www.img.com/21.png', null, 'en', 1, '2015-01-30', 1200, 6, 21)
  , (22, 2, null, null, 'This is summary 21', 'https://www.img.com/21.png', null, 'en', 1, '2015-01-30', 1200, 6, 22)
  , (23, 2, 'To Re-Publish', null, null, 'https://www.img.com/21.png', null, 'en', 1, '2015-01-30', 1200, 6, 23)
  , (24, 2, 'To Publish - WPP', null, 'This is summary 20', 'https://www.img.com/20.png', null, 'en', 0, null, 1200, 6, 20)
  , (25, 2, 'Schedule Publish #1', null, null, null, null, 'en', 0, null, 1200, 6, 20)

  , (30, 3, 'Already-Imported', null, null, 'https://www.img.com/21.png', 'https://nkowa.com/podcast-jokkolabs-douala-les-valeurs-qui-nous-tiennent-a-coeur-sont-la-culture-lagriculture-et-linnovation', 'en', 1, '2015-01-30', 1200, 6, 30)
  , (31, 3, 'Already-Imported', null, null, 'https://www.img.com/21.png', 'https://nkowa.com/podcast-jokkolabs-douala-les-valeurs-qui-nous-tiennent-a-coeur-sont-la-culture-lagriculture-et-linnovation', 'en', 1, '2015-01-30', 1200, 6, 30)
;
