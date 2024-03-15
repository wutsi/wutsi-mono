CREATE TABLE T_ADS_KPI
(
    id         BIGINT NOT NULL AUTO_INCREMENT,

    ads_id     VARCHAR(36) NOT NULL,
    type       INT    NOT NULL DEFAULT 0,
    source     INT    NOT NULL DEFAULT 0,
    year       INT    NOT NULL DEFAULT 0,
    month      INT    NOT NULL DEFAULT 0,
    value      BIGINT,

    UNIQUE (ads_id, type, source, year, month),
    PRIMARY KEY (id)
) ENGINE = InnoDB;
