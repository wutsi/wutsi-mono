alter table T_PRODUCT add column deleted boolean not null default false;
alter table T_PRODUCT add column deleted_date_time datetime;
