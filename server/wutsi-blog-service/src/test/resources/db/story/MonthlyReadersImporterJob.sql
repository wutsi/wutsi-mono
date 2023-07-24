INSERT INTO T_USER(id, super_user, name, email, full_name, picture_url, login_count) VALUES
  (1, false, 'ray.sponsible', 'ray.sponsible@gmail.com', 'Ray Sponsible', 'https://picture.com/ray.sponsible', 5),
  (2, false, 'john.smith', 'john.smith@gmail.com', 'Jane Doe', 'https://picture.com/login', 1),
  (3, false, 'john.partner', 'john.partner@gmail.com', 'Jane Doe', 'https://picture.com/login', 1),
  (4, false, 'john.partner4', 'john.partner4@gmail.com', 'Jane Doe', 'https://picture.com/login', 1),
  (5, false, 'john.partner5', 'john.partner5@gmail.com', 'Jane Doe', 'https://picture.com/login', 1),
  (99, true, 'ze.god', 'ze.god@gmail.com', 'Ze Got', 'https://picture.com/login', 1)
;

INSERT INTO T_STORY(id, user_fk, title, tagline, summary, thumbnail_url, source_url, source_url_hash, language, status, published_date_time, word_count, reading_minutes, readability_score, deleted_date_time) VALUES
  (100, 1, 'Already-Imported', null, null, 'https://www.img.com/100.png', 'https://nkowa.com/podcast-jokkolabs-douala-les-valeurs-qui-nous-tiennent-a-coeur-sont-la-culture-lagriculture-et-linnovation', 'bac453c63189f63093efe20cc991b127', 'en', 1, '2015-01-30', 1200, 6, 30, null),
  (110, 1, 'Already-Imported', null, null, 'https://www.img.com/200.png', 'https://nkowa.com/podcast-jokkolabs-douala-les-valeurs-qui-nous-tiennent-a-coeur-sont-la-culture-lagriculture-et-linnovation', 'bac453c63189f63093efe20cc991b127', 'en', 1, '2015-01-30', 1200, 6, 30, null),
  (120, 1, 'Already-Imported', null, null, 'https://www.img.com/300.png', 'https://nkowa.com/podcast-jokkolabs-douala-les-valeurs-qui-nous-tiennent-a-coeur-sont-la-culture-lagriculture-et-linnovation', 'bac453c63189f63093efe20cc991b127', 'en', 1, '2015-01-30', 1200, 6, 30, null),
  (200, 2, 'Already-Imported', null, null, 'https://www.img.com/400.png', 'https://nkowa.com/podcast-jokkolabs-douala-les-valeurs-qui-nous-tiennent-a-coeur-sont-la-culture-lagriculture-et-linnovation', 'bac453c63189f63093efe20cc991b127', 'en', 1, '2015-01-30', 1200, 6, 30, null),
  (90, 1, 'Delete Me', null, null, null, null, null, 'en', 1, '2015-01-30', 1200, 6, 30, null),
  (99, 1, 'Deleted', null, null, null, null, null, 'en', 1, '2015-01-30', 1200, 6, 30, now())
;

INSERT INTO T_SUBSCRIPTION(user_fk, subscriber_fk) VALUES
    (1, 2),
    (1, 3)
;
