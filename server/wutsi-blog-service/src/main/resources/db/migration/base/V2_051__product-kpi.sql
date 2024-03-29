CREATE TABLE T_PRODUCT_KPI
(
    id         BIGINT NOT NULL AUTO_INCREMENT,

    product_id BIGINT NOT NULL,
    type       INT    NOT NULL DEFAULT 0,
    source     INT    NOT NULL DEFAULT 0,
    year       INT    NOT NULL DEFAULT 0,
    month      INT    NOT NULL DEFAULT 0,
    value      BIGINT,

    UNIQUE (product_id, type, source, year, month),
    PRIMARY KEY (id)
) ENGINE = InnoDB;

ALTER TABLE T_PRODUCT
    ADD COLUMN view_count BIGINT NOT NULL DEFAULT 0;
