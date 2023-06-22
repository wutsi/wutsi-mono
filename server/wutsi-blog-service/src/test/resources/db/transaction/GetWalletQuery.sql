INSERT INTO T_USER(id, subscriber_count, name, email, full_name, picture_url, website_url, login_count, biography, blog) VALUES
    (10, 2, 'ray.sponsible', 'ray.sponsible@gmail.com', 'Ray Sponsible', 'https://picture.com/ray.sponsible', 'https://me.com/ray.sponsible', 5, 'Angel investor', true)
;

INSERT INTO T_WALLET(id, user_fk, currency, country, balance) VALUES
    ('10', 10, 'XAF', 'CM', 10000)
;
