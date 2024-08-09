drop view V_SUPER_FAN;
create view V_SUPER_FAN as
	select distinct
	    IF(user_fk is null, wallet_fk, CONCAT(wallet_fk, '-', user_fk)) as id,
		wallet_fk as wallet_id,
		user_fk as user_id,
		count(*) as transaction_count,
		sum(amount) as value
	from T_TRANSACTION
	where wallet_fk IS NOT NULL and status=1 and amount>0
	group by wallet_fk, user_fk;

update T_USER U
    set U.super_fan_count = (SELECT count(*) FROM V_SUPER_FAN F WHERE F.wallet_id=U.wallet_id)
    where U.wallet_id IS NOT NULL;
