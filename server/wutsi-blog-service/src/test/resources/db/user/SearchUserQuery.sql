INSERT INTO T_USER(id, name, email, full_name, picture_url, website_url, login_count, biography, blog, story_count,
                   publish_story_count, wpp)
VALUES (1, 'ray.sponsible', 'ray.sponsible@gmail.com', 'Ray Sponsible', 'https://picture.com/ray.sponsible',
        'https://me.com/ray.sponsible', 5, 'Angel investor', true, 1, 1, false)
     , (2, 'jane.doe', 'login@gmail.com', 'Jane Doe', 'https://picture.com/jane.doe', null, 1, null, false, 2, 1, false)
     , (3, 'login.without.email', 'login.without.email@gmail.com', 'Login Without Email',
        'https://picture.com/login.without.email', null, 1, null, false, 3, 3, false)
     , (4, 'logout', 'logout@gmail.com', 'Logout', null, null, 1, null, false, 4, 0, false)
     , (5, 'update.account', 'update.account@gmail.com', '-', null, null, 1, null, false, 5, 0, false)
     , (6, 'update.user', 'update.user@gmail.com', '-', null, null, 13, null, false, 6, 0, false)

     , (10, 'rename.user', 'rename.update@gmail.com', '-', null, null, 13, null, false, 7, 0, true)
     , (11, 'duplicate.name', 'duplicate.name@gmail.com', '-', null, null, 13, null, false, 8, 0, true)

     , (20, 'change.email', 'change.email@gmail.com', '-', null, null, 13, null, false, 9, 0, false)
     , (21, 'duplicate.email', 'duplicate.email@gmail.com', '-', null, null, 13, null, false, 10, 10, false)

     , (30, 'update.fullname', 'update.fullname@gmail.com', '-', null, null, 13, null, false, 11, 1, false)

     , (40, 'update.picture', 'update.picture@gmail.com', '-', null, null, 13, null, false, 12, 0, false)

     , (50, 'set.wallet', 'update.wallet@gmail.com', '-', null, null, 13, null, false, 12, 0, false)
     , (51, 'update.wallet', 'update.wallet@gmail.com', '-', null, null, 13, null, false, 12, 0, false)
     , (52, 'update.wallet.duplicate-number', 'update.wallet@gmail.com', '-', null, null, 13, null, false, 12, 0, false)
;

INSERT INTO T_ACCOUNT(id, provider_fk, user_fk, provider_user_id, login_count, last_login_date_time)
VALUES (10, 1, 1, 'ray.sponsible', 1, '2018-01-01')
     , (11, 2, 1, 'ray-sponsible', 4, '2018-01-01')
;

UPDATE T_USER
set super_user= true,
    blog= true,
    telegram_id='ray.sponsible',
    whatsapp_id='23799505555',
    last_publication_date_time=now()
WHERE id = 1;

UPDATE T_USER
SET active= false
WHERE id = 1
   OR id = 2;

UPDATE T_USER
SET country='cm'
where id in (1, 6, 10);

INSERT INTO T_WALLET(id, user_fk, currency, country)
VALUES ('1', 1, 'XAF', 'CM');
UPDATE T_USER
set wallet_id='1'
WHERE id = 1;

INSERT INTO T_STORE(id, user_fk, currency, product_count, order_count, total_sales)
VALUES ('1', 1, 'XAF', 11, 111, 111000);
UPDATE T_USER
set store_id='1'
WHERE id = 1;

