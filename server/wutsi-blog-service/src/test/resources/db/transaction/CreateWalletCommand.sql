INSERT INTO T_USER(id, subscriber_count, name, email, full_name, picture_url, website_url, login_count, biography, blog) VALUES
    (1, 2, 'ray.sponsible', 'ray.sponsible@gmail.com', 'Ray Sponsible', 'https://picture.com/ray.sponsible', 'https://me.com/ray.sponsible', 5, 'Angel investor', true)
  , (2, 0, 'jane.doe', 'login@gmail.com', 'Jane Doe', 'https://picture.com/jane.doe', null, 1, null, false)
  , (3, 1, 'login.without.email', 'login.without.email@gmail.com', 'Login Without Email', 'https://picture.com/login.without.email', null, 1, null, false)
;

INSERT INTO T_WALLET(id, user_fk, currency, country) VALUES
    ('1', 1, 'XAF', 'CM'),
    ('2', 2, 'XAF', 'CM')
;

INSERT INTO T_TRANSACTION(id, idempotency_key, status, type, wallet_fk, amount, net, currency, payment_method_owner, payment_method_number, payment_method_type, gateway_type)
    VALUES
        ('100', 'donation-100', 2, 1, 1, 1000, 1000, 'XAF', 'Roger Milla', '+237911111111', 1, 1)
    ;
