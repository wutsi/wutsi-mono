INSERT INTO T_USER(id, subscriber_count, name, email, full_name, picture_url, website_url, login_count, biography, blog)
VALUES (1, 2, 'ray.sponsible', 'herve.tchepannou@gmail.com', 'Ray Sponsible', 'https://picture.com/ray.sponsible','https://me.com/ray.sponsible', 5, 'Angel investor', true),
       (2, 0, 'jane.doe', 'login@gmail.com', 'Jane Doe', 'https://picture.com/jane.doe', null, 1, null, false),
       (3, 1, 'login.without.email', 'login.without.email@gmail.com', 'Login Without Email','https://picture.com/login.without.email', null, 1, null, false),
       (4, 1, 'u4', 'tchbansi@hotmail.com', 'U4', null, null, 1, null, false),
       (5, 1, 'u5', 'u5@gmail.com', 'U5', null, null, 1, null, false),
       (6, 1, 'u6', 'u6@gmail.com', 'U6', null, null, 1, null, false)
;

INSERT INTO T_WALLET(id, user_fk, balance, currency, country, donation_count) VALUES
    ('1', 1, 0, 'XAF', 'CM', 1),
    ('2', 2, 450, 'XAF', 'CM', 1),
    ('3', 3, 0, 'XAF', 'CM', 1)
;

UPDATE T_USER set wallet_id='1' where id=1;

INSERT INTO T_TRANSACTION(id, idempotency_key, status, type, wallet_fk, amount, fees, net, currency, payment_method_owner, payment_method_number, payment_method_type, gateway_type, user_fk, email, campaign)
    VALUES
        ('100', 'pending-2-success', 2, 1,'1', 10000, 0, 0, 'XAF', 'Roger Milla', '+237911111111', 1, 1, 4, 'tchbansi@hotmail.com', 'ads-100'),
        ('101', 'success-101', 1, 1,'1', 5000, 500, 4500, 'XAF', 'Roger Milla', '+237911111111', 1, 1, 5, 'u5@gmail.com', null),
        ('102', 'success-102', 1, 3,'1', 1000, 0, 1000, 'XAF', 'Roger Milla', '+237911111111', 1, 1, 4, 'tchbansi@hotmail.com', null),

        ('200', 'pending-2-failed', 2, 1, '2', 10000, 0, 0, 'XAF', 'Roger Milla', '+237911111111', 1, 1, 5, 'u5@gmail.com', null),
        ('201', 'success-201', 1, 1, '2', 500, 50, 450, 'XAF', 'Roger Milla', '+237911111111', 1, 1, 5, 'u5@gmail.com', null),

        ('300', 'success', 1, 1, '3', 10000, 1000, 9000, 'XAF', 'Roger Milla', '+237911111111', 1, 1, 5, 'u5@gmail.com', 'ads-100'),
        ('310', 'failed', 3, 1, '3', 10000, 0, 0, 'XAF', 'Roger Milla', '+237911111111', 1, 1, 5, 'u5@gmail.com', 'ads-100')
    ;

INSERT INTO T_ADS(id, user_fk, title)
VALUES
    ('ads-100', 1, 'Ads 100')
;
