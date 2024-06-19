update T_TRANSACTION T, T_USER U
    SET T.user_fk=U.id
    WHERE U.email=T.email and status=1 and T.type in (1,2);

DROP VIEW V_SUPER_FAN;
CREATE VIEW V_SUPER_FAN AS
	SELECT DISTINCT
	    IF(user_fk is null, wallet_fk, CONCAT(wallet_fk, '-', user_fk)) as id,
		wallet_fk as wallet_id, 
		user_fk as user_id, 
		count(*) as transaction_count, 
		sum(amount) as value
	FROM T_TRANSACTION
	where wallet_fk IS NOT NULL and status=1 and type in (1,2)
	group by wallet_fk, user_fk;

UPDATE T_USER U
    SET U.super_fan_count = (SELECT count(*) FROM V_SUPER_FAN F WHERE F.wallet_id=U.wallet_id)
    WHERE U.wallet_id IS NOT NULL;
