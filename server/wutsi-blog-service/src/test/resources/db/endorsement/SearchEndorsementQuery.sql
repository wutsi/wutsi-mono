INSERT INTO T_USER(id, subscriber_count, name, email, full_name, picture_url, website_url, login_count, biography, blog)
VALUES (1, 1, 'ray.sponsible', 'ray.sponsible@gmail.com', 'Ray Sponsible', 'https://picture.com/ray.sponsible',
        'https://me.com/ray.sponsible', 5, 'Angel investor', true)
     , (2, 0, 'jane.doe', 'login@gmail.com', 'Jane Doe', 'https://picture.com/jane.doe', null, 1, null, false)
     , (3, 1, 'login.without.email', 'login.without.email@gmail.com', 'Login Without Email',
        'https://picture.com/login.without.email', null, 1, null, false)
;


INSERT INTO T_ENDORSEMENT(user_fk, endorser_fk, blurb)
VALUES (1, 3, 'Yo'),
       (1, 2, 'Man'),

       (3, 2, null)
;
