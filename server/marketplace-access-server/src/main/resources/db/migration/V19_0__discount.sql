ALTER TABLE T_PRODUCT DROP COLUMN comparable_price;

CREATE TABLE T_DISCOUNT(
    id              SERIAL NOT NULL,

    store_fk        BIGINT NOT NULL REFERENCES T_STORE(id),
    name            VARCHAR(30) NOT NULL,
    rate            INT NOT NULL,
    starts          DATE NOT NULL,
    ends            DATE NOT NULL,
    all_products    BOOLEAN NOT NULL DEFAULT true,
    is_deleted      BOOLEAN NOT NULL DEFAULT false,

    created         DATETIME NOT NULL DEFAULT now(),
    updated         DATETIME NOT NULL DEFAULT now() ON UPDATE now(),
    deleted         DATETIME,

    PRIMARY KEY (id)
);

CREATE TABLE T_DISCOUNT_PRODUCT(
    discount_fk      BIGINT NOT NULL REFERENCES T_DISCOUNT(id),
    product_fk       BIGINT NOT NULL REFERENCES T_PRODUCT(id),

    PRIMARY KEY(discount_fk, product_fk)
);
