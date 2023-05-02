INSERT INTO T_USER(id, name, email, full_name, picture_url, login_count) VALUES
    (1, 'ray.sponsible', 'ray.sponsible@gmail.com', 'Ray Sponsible', 'https://picture.com/ray.sponsible', 5)
  , (2, 'john.smith', 'john.smith@gmail.com', 'Jane Doe', 'https://picture.com/login', 1)

;

INSERT INTO T_STORY(id, user_fk, title, status, live, published_date_time) VALUES
    (1, 1, 'Test1', 1, true, '2019-05-01')
  , (2, 1, 'Test2', 1, true, '2019-06-01')
  , (3, 1, 'Test3', 1, true, '2019-07-01')
  , (4, 1, 'Test4', 1, true, '2019-08-01')
  , (5, 1, 'Test5', 1, false,'2019-09-01')
  , (6, 1, 'Test6', 0, false,null)
  , (7, 1, 'Test7', 0, false,null)
  , (8, 1, 'Test8', 1, true,'2019-10-01')
;
