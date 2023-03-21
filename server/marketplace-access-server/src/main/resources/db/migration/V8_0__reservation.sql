CREATE TABLE T_RESERVATION(
    id              SERIAL NOT NULL,

    order_id        VARCHAR(36) NOT NULL,

    status          INT NOT NULL DEFAULT 0,
    created         DATETIME NOT NULL DEFAULT now(),
    updated         DATETIME NOT NULL DEFAULT now() ON UPDATE now(),
    cancelled       DATETIME,
    confirmed       DATETIME,

    PRIMARY KEY (id)
);
CREATE INDEX I_RESERVATION__order_id ON T_RESERVATION(order_id);

CREATE TABLE T_RESERVATION_ITEM(
    id              SERIAL NOT NULL,

    reservation_fk  BIGINT NOT NULL REFERENCES T_RESERVATION(id),
    product_fk      BIGINT NOT NULL REFERENCES T_PRODUCT(id),

    quantity        INT NOT NULL,

    UNIQUE(reservation_fk, product_fk),
    PRIMARY KEY (id)
);


ALTER TABLE T_PRODUCT ADD CONSTRAINT quantity_min CHECK (quantity >= 0);

