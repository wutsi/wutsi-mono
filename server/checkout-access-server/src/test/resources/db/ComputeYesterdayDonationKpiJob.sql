INSERT INTO T_BUSINESS(id, account_id, status, deactivated,currency, country)
    VALUES
        (1, 1, 1, null, 'XAF', 'CM'),
        (2, 2, 1, null, 'XAF', 'CM')
    ;

INSERT INTO T_ORDER(id, type, business_fk, status, total_price, created, customer_name, customer_email, currency, expires)
    VALUES
        (1000, 2, 1, 3, 4000, DATE_ADD(now(), INTERVAL -1 DAY), 'Ray Sponsible', 'ray.sponsible10@gmail.com', 'XAF', DATE_ADD(now(), INTERVAL -1 DAY)),
        (1001, 2, 1, 4, 3000, DATE_ADD(now(), INTERVAL -1 DAY), 'Ray Sponsible', 'ray.sponsible11@gmail.com', 'XAF', DATE_ADD(now(), INTERVAL -1 DAY)),
        (1002, 2, 1, 4, 1500, DATE_ADD(now(), INTERVAL -1 DAY), 'Ray Sponsible', 'ray.sponsible11@gmail.com', 'XAF', DATE_ADD(now(), INTERVAL -1 DAY)),
        (1003, 2, 1, 4, 1500, DATE_ADD(now(), INTERVAL -10 DAY), 'Ray Sponsible', 'ray.sponsible11@gmail.com', 'XAF', DATE_ADD(now(), INTERVAL -1 DAY)),
        (1444, 1, 1, 3, 4000, DATE_ADD(now(), INTERVAL -1 DAY), 'Ray Sponsible', 'ray.sponsible10@gmail.com', 'XAF', DATE_ADD(now(), INTERVAL -1 DAY)),

        (2000, 2, 2, 3, 1500, DATE_ADD(now(), INTERVAL -1 DAY), 'Ray Sponsible', 'ray.sponsible20@gmail.com', 'XAF', DATE_ADD(now(), INTERVAL -1 DAY)),

        (9997, 2, 1, 0, 5000, DATE_ADD(now(), INTERVAL -1 DAY), 'Ray Sponsible', 'ray.sponsible11@gmail.com', 'XAF', DATE_ADD(now(), INTERVAL -1 DAY)),
        (9998, 2, 1, 1, 5000, DATE_ADD(now(), INTERVAL -1 DAY), 'Ray Sponsible', 'ray.sponsible11@gmail.com', 'XAF', DATE_ADD(now(), INTERVAL -1 DAY)),
        (9999, 2, 1, 5, 5000, DATE_ADD(now(), INTERVAL -1 DAY), 'Ray Sponsible', 'ray.sponsible11@gmail.com', 'XAF', DATE_ADD(now(), INTERVAL -1 DAY))
    ;
