alter table T_TRANSACTION add column campaign varchar(100);
create index I_TRANSACTION_campaign on T_TRANSACTION(campaign);

alter table T_ADS add column order_count BIGINT NOT NULL DEFAULT 0;
alter table T_ADS add column total_sales BIGINT NOT NULL DEFAULT 0;
