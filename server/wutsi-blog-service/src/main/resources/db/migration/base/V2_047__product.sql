CREATE TABLE T_PRODUCT
(
    id                     BIGINT       NOT NULL AUTO_INCREMENT,

    external_id            VARCHAR(36)  NOT NULL,
    user_fk                BIGINT       NOT NULL REFERENCES T_USER (id),

    title                  VARCHAR(255) NOT NULL,
    description            TEXT,
    image_url              TEXT,
    file_url               TEXT,
    price                  BIGINT       NOT NULL DEFAULT 0,
    currency               VARCHAR(3)   NOT NULL,
    available              BOOLEAN      NOT NULL DEFAULT true,

    order_count            BIGINT                DEFAULT 0,
    total_sales            BIGINT                DEFAULT 0,

    creation_date_time     DATETIME              DEFAULT NOW(),
    modification_date_time DATETIME     NOT NULL DEFAULT now() ON UPDATE now(),

    UNIQUE (user_fk, external_id),
    PRIMARY KEY (id)
) ENGINE = InnoDB;

ALTER TABLE T_USER
    ADD COLUMN product_count BIGINT DEFAULT 0;
