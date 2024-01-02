DELETE
FROM T_USER_KPI
where type between(13, 22);

-- User KPI
INSERT INTO T_USER_KPI(user_id, type, source, year, month, value)
SELECT 0, 13, 0, year(creation_date_time), month(creation_date_time), count(*)
FROM T_USER
where suspended = false
GROUP BY year(creation_date_time), month(creation_date_time);

-- User Blog KPI
INSERT INTO T_USER_KPI(user_id, type, source, year, month, value)
SELECT 0, 14, 0, year(creation_date_time), month(creation_date_time), count(*)
FROM T_USER
where suspended = false
  AND blog = true
GROUP BY year(creation_date_time), month(creation_date_time);

-- User WPP KPI
INSERT INTO T_USER_KPI(user_id, type, source, year, month, value)
SELECT 0, 15, 0, year(wpp_date_time), month(wpp_date_time), count(*)
FROM T_USER
where suspended = false
  AND wpp = true
GROUP BY year(wpp_date_time), month(wpp_date_time);

-- User Store
INSERT INTO T_USER_KPI(user_id, type, source, year, month, value)
SELECT 0, 16, 0, year(creation_date_time), month(creation_date_time), count(*)
FROM T_STORE
GROUP BY year(creation_date_time), month(creation_date_time);

-- Publications
INSERT INTO T_USER_KPI(user_id, type, source, year, month, value)
SELECT 0, 17, 0, year(published_date_time), month(published_date_time), count(*)
FROM T_STORY
WHERE deleted = false
  AND status = 1
GROUP BY year(published_date_time), month(published_date_time);

-- Product
INSERT INTO T_USER_KPI(user_id, type, source, year, month, value)
SELECT 0, 18, 0, year(creation_date_time), month(creation_date_time), count(*)
FROM T_PRODUCT
WHERE status = 1
GROUP BY year(creation_date_time), month(creation_date_time);

-- Donation
INSERT INTO T_USER_KPI(user_id, type, source, year, month, value)
SELECT W.user_fk, 19, 0, year(T.creation_date_time), month(T.creation_date_time), count(*)
FROM T_TRANSACTION T
         JOIN T_WALLET W ON T.wallet_fk = W.id
WHERE T.status = 1
  AND T.type = 1
GROUP BY W.user_fk, year(T.creation_date_time), month(T.creation_date_time);

INSERT INTO T_USER_KPI(user_id, type, source, year, month, value)
SELECT W.user_fk, 20, 0, year(T.creation_date_time), month(T.creation_date_time), sum(T.amount)
FROM T_TRANSACTION T
         JOIN T_WALLET W ON T.wallet_fk = W.id
WHERE T.status = 1
  AND T.type = 1
GROUP BY W.user_fk, year(T.creation_date_time), month(T.creation_date_time);
