INSERT INTO T_BUSINESS(id, account_id, status, deactivated,currency, country)
    VALUES
        (1, 1, 1, null, 'XAF', 'CM'),
        (2, 2, 1, null, 'XAF', 'CM')
    ;

INSERT INTO T_ORDER(id, type, business_fk, status, total_price, created, customer_name, customer_email, currency, expires)
    VALUES
        (1000, 1, 1, 3, 4000, DATE_ADD(now(), INTERVAL -1 DAY), 'Ray Sponsible', 'ray.sponsible10@gmail.com', 'XAF', DATE_ADD(now(), INTERVAL -1 DAY)),
        (1001, 1, 1, 4, 3000, DATE_ADD(now(), INTERVAL -1 DAY), 'Ray Sponsible', 'ray.sponsible11@gmail.com', 'XAF', DATE_ADD(now(), INTERVAL -1 DAY)),
        (1002, 1, 1, 4, 1500, DATE_ADD(now(), INTERVAL -1 DAY), 'Ray Sponsible', 'ray.sponsible11@gmail.com', 'XAF', DATE_ADD(now(), INTERVAL -1 DAY)),
        (1003, 1, 1, 4, 1500, DATE_ADD(now(), INTERVAL -10 DAY), 'Ray Sponsible', 'ray.sponsible11@gmail.com', 'XAF', DATE_ADD(now(), INTERVAL -1 DAY)),
        (1222, 2, 1, 3, 4000, DATE_ADD(now(), INTERVAL -1 DAY), 'Ray Sponsible', 'ray.sponsible10@gmail.com', 'XAF', DATE_ADD(now(), INTERVAL -1 DAY)),

        (2000, 1, 2, 3, 1500, DATE_ADD(now(), INTERVAL -1 DAY), 'Ray Sponsible', 'ray.sponsible20@gmail.com', 'XAF', DATE_ADD(now(), INTERVAL -1 DAY)),

        (9997, 1, 1, 0, 5000, DATE_ADD(now(), INTERVAL -1 DAY), 'Ray Sponsible', 'ray.sponsible11@gmail.com', 'XAF', DATE_ADD(now(), INTERVAL -1 DAY)),
        (9998, 1, 1, 1, 5000, DATE_ADD(now(), INTERVAL -1 DAY), 'Ray Sponsible', 'ray.sponsible11@gmail.com', 'XAF', DATE_ADD(now(), INTERVAL -1 DAY)),
        (9999, 1, 1, 5, 5000, DATE_ADD(now(), INTERVAL -1 DAY), 'Ray Sponsible', 'ray.sponsible11@gmail.com', 'XAF', DATE_ADD(now(), INTERVAL -1 DAY))
    ;

INSERT INTO T_ORDER_ITEM(order_fk, product_id, quantity, total_price, title)
    VALUES
        (1000, 100, 3, 4500, 'Product'),
        (1000, 101, 1, 0500, 'Product'),
        (1001, 100, 2, 3000, 'Product'),
        (1002, 100, 1, 1500, 'Product'),
        (1003, 100, 10, 15000, 'Product'),
        (2000, 200, 1, 1500, 'Product'),

        (9997, 100, 1, 1500, 'Product'),
        (9998, 100, 1, 1500, 'Product'),
        (9999, 100, 1, 1500, 'Product')
    ;
