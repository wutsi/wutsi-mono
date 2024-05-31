DELETE FROM T_USER_KPI where type in (25, 26, 27);

INSERT INTO T_USER_KPI(user_id, type, year, month, value, source)
    SELECT 0, 25, year(creation_date_time), month(creation_date_time), count(*), 0
    FROM T_TRANSACTION
    GROUP BY year(creation_date_time), month(creation_date_time);

INSERT INTO T_USER_KPI(user_id, type, year, month, value, source)
    SELECT 0, 26, year(creation_date_time), month(creation_date_time), count(*), 0
    FROM T_TRANSACTION
    WHERE status=1
    GROUP BY year(creation_date_time), month(creation_date_time);

CREATE TEMPORARY TABLE TMP_PSR(
    year INT,
    month INT,
    total INT,
    success INT,
    psr INT,

    PRIMARY KEY (year, month)
);

INSERT INTO TMP_PSR(year, month, total)
    SELECT year, month, value FROM T_USER_KPI where type=25;

INSERT INTO TMP_PSR(year, month, success)
    SELECT year, month, value FROM T_USER_KPI where type=26
    ON DUPLICATE KEY UPDATE success=value;

UPDATE TMP_PSR SET psr=0 WHERE success IS NULL or total=0;
UPDATE TMP_PSR SET psr=10000*success/total WHERE success IS NOT NULL AND total<>0;

INSERT INTO T_USER_KPI(user_id, type, year, month, value, source)
    SELECT 0, 27, year, month, psr, 0 FROM TMP_PSR;
