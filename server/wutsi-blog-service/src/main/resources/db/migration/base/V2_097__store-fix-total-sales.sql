alter table T_PRODUCT add column cvr DECIMAL(10, 4) NOT NULL default 0;
update T_PRODUCT set cvr=if(view_count=0, 0.0, cast(order_count as DOUBLE )/cast(view_count as DOUBLE ));

update T_STORE S set S.total_sales = (SELECT sum(T.amount) from T_TRANSACTION T where T.status=1 and T.type=2 and S.id=T.store_fk);
update T_USER U set U.total_sales = (SELECT sum(T.amount) from T_TRANSACTION T where T.status=1 and T.type=2 and U.store_id=T.store_fk) where U.store_id is not null;

alter table T_STORE add column view_count BIGINT NOT NULL default 0;
update T_STORE S set S.view_count = (SELECT sum(P.view_count) from T_PRODUCT P where P.store_fk=S.id);

alter table T_STORE add column cvr DECIMAL(10, 4) NOT NULL default 0;
update T_STORE set cvr=if(view_count=0, 0.0, cast(order_count as DOUBLE )/cast(view_count as DOUBLE ));
