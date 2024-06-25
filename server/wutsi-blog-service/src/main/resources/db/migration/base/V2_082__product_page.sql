CREATE TABLE T_PAGE(
    id                     BIGINT      NOT NULL AUTO_INCREMENT,

    product_fk             BIGINT      NOT NULL REFERENCES T_PRODUCT (id),
    number                 INT         NOT NULL,
    content_type           TEXT        NOT NULL,
    content_url            TEXT        NOT NULL,
    creation_date_time     DATETIME NOT NULL DEFAULT now(),

    PRIMARY KEY (id),
    UNIQUE (product_fk, number)
) ENGINE = InnoDB;