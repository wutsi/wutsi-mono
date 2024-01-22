ALTER TABLE T_USER ADD COLUMN order_count BIGINT DEFAULT 0;
ALTER TABLE T_USER ADD COLUMN donation_count BIGINT DEFAULT 0;
ALTER TABLE T_USER ADD COLUMN total_sales BIGINT DEFAULT 0;

UPDATE T_USER U JOIN T_STORE S on U.store_id=S.id SET U.total_sales=S.total_sales, U.order_count=S.order_count;
UPDATE T_USER U JOIN T_WALLET W on U.wallet_id=W.id SET U.donation_count=W.donation_count;