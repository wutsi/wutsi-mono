INSERT INTO T_USER_KPI(user_id, type, year, month, value, source)
SELECT user_fk, 3, YEAR(timestamp), MONTH(timestamp), COUNT(*), 0
FROM T_SUBSCRIPTION
GROUP BY user_fk, YEAR(timestamp), MONTH(timestamp);
