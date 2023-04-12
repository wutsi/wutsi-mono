CREATE TABLE T_KPI_DONATION(
    id               SERIAL NOT NULL,

    date             DATE NOT NULL,
    business_fk      BIGINT NOT NULL REFERENCES T_BUSINESS(id),

    total_donations  BIGINT NOT NULL DEFAULT 0,
    total_value      BIGINT NOT NULL DEFAULT 0,

    UNIQUE(date, business_fk),
    PRIMARY KEY (id)
);

ALTER TABLE T_BUSINESS ADD total_donations BIGINT DEFAULT 0;
ALTER TABLE T_BUSINESS ADD total_donation_value BIGINT DEFAULT 0;

-- Compute KPI Donation
INSERT INTO T_KPI_DONATION(date, business_fk, total_donations, total_value)
    SELECT DATE(O.created), O.business_fk, COUNT(O.id), SUM(O.total_price)
        FROM T_ORDER O
        WHERE
            O.status NOT IN (0, 1, 5)
            AND O.type=2
        GROUP BY DATE(O.created), O.business_fk;


-- Compute KPI Sales
DELETE FROM T_KPI_SALES;
INSERT INTO T_KPI_SALES(date, business_fk, product_id, total_orders, total_units, total_value)
    SELECT DATE(O.created), O.business_fk, I.product_id, COUNT(I.product_id), SUM(I.quantity), SUM(I.total_price)
        FROM T_ORDER O
            JOIN T_ORDER_ITEM I ON I.order_fk=O.id
        WHERE
            O.status NOT IN (0, 1, 5)
            AND O.type=1
        GROUP BY DATE(O.created), O.business_fk, I.product_id;
