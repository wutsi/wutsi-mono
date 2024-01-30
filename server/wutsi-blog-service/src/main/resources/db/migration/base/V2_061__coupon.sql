CREATE TABLE T_COUPON
(
    id                     BIGINT      NOT NULL AUTO_INCREMENT,

    product_fk             BIGINT      NOT NULL REFERENCES T_PRODUCT (id),
    user_fk                BIGINT      NOT NULL REFERENCES T_USER (id),
    transaction_fk         VARCHAR(36) REFERENCES T_TRANSACTION (id),

    percentage             INT         NOT NULL DEFAULT 0,
    creation_date_time     DATETIME    NOT NULL DEFAULT now(),
    expiry_date_time       DATETIME    NOT NULL,

    PRIMARY KEY (id)
) ENGINE = InnoDB;

ALTER TABLE T_TRANSACTION ADD COLUMN coupon_fk BIGINT REFERENCES T_COUPON(id);