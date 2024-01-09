CREATE TABLE T_BOOK
(
    id                     BIGINT      NOT NULL AUTO_INCREMENT,

    product_fk             BIGINT      NOT NULL REFERENCES T_PRODUCT (id),
    user_fk                BIGINT      NOT NULL REFERENCES T_USER (id),
    transaction_fk         VARCHAR(36) NOT NULL REFERENCES T_TRANSACTION (id),

    location               TEXT,
    creation_date_time     DATETIME    NOT NULL DEFAULT now(),
    modification_date_time DATETIME    NOT NULL DEFAULT now(),

    UNIQUE (transaction_fk),
    PRIMARY KEY (id)
) ENGINE = InnoDB;

