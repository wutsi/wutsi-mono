insert into T_SUBSCRIPTION(user_fk, subscriber_fk, timestamp, referer)
    select W.user_fk, T.user_fk, T.creation_date_time, T.referer
        from T_TRANSACTION T join T_WALLET W on T.wallet_fk=W.id
        where T.type=2 and T.status=1 and T.user_fk is not null
    on duplicate key update timestamp=timestamp
;
