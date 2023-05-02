
INSERT INTO T_USER(id, name, email, full_name, picture_url, login_count) VALUES
    (1, 'ray.sponsible', 'ray.sponsible@gmail.com', 'Ray Sponsible', 'https://picture.com/ray.sponsible', 5)
  , (2, 'login', 'login@gmail.com', 'Jane Doe', 'https://picture.com/login', 1)
  , (3, 'user3', 'user3@gmail.com', 'Jane Doe', 'https://picture.com/login', 1)
;

INSERT INTO T_CHANNEL(id, user_fk, type, provider_user_id, name, access_token, picture_url) VALUES
    (10, 1, 1, '1111', 'FB', 'fb-000010', 'https://img.com/fb-000010.png')
  , (11, 1, 2, '2222', 'TW', 'tw-000011', 'https://img.com/tw-000011.png')
  , (12, 1, 3, '3333', 'LK', 'lk-000012', null)

  , (20, 2, 1, '4444', 'delete-me', 'lk-00020', null)
  , (21, 2, 2, '5555', 'delete-me', 'lk-00021', null)
;
