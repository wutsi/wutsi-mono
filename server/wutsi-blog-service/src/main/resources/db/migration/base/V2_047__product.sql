CREATE TABLE T_STORE
(
    id                     VARCHAR(36) NOT NULL,

    user_fk                BIGINT      NOT NULL REFERENCES T_USER (id),

    feed_url               TEXT        NOT NULL,
    currency               VARCHAR(3)  NOT NULL,
    product_count          BIGINT      NOT NULL DEFAULT 0,
    publish_product_count  BIGINT      NOT NULL DEFAULT 0,
    order_count            BIGINT      NOT NULL DEFAULT 0,
    total_sales            BIGINT      NOT NULL DEFAULT 0,

    creation_date_time     DATETIME    NOT NULL DEFAULT now(),
    modification_date_time DATETIME    NOT NULL DEFAULT now() ON UPDATE now(),

    UNIQUE (user_fk),
    PRIMARY KEY (id)
) ENGINE = InnoDB;

CREATE TABLE T_PRODUCT
(
    id                     BIGINT       NOT NULL AUTO_INCREMENT,

    external_id            VARCHAR(36)  NOT NULL,
    store_fk               VARCHAR(36)  NOT NULL REFERENCES T_USER (id),

    title                  VARCHAR(255) NOT NULL,
    description            TEXT,
    status                 INT          NOT NULL DEFAULT 0,
    image_url              TEXT,
    file_url               TEXT,
    price                  BIGINT       NOT NULL DEFAULT 0,
    available              BOOLEAN      NOT NULL DEFAULT true,

    order_count            BIGINT                DEFAULT 0,
    total_sales            BIGINT                DEFAULT 0,

    creation_date_time     DATETIME              DEFAULT NOW(),
    modification_date_time DATETIME     NOT NULL DEFAULT now() ON UPDATE now(),
    published_date_time    DATETIME,

    UNIQUE (store_fk, external_id),
    PRIMARY KEY (id)
) ENGINE = InnoDB;

ALTER TABLE T_USER
    ADD COLUMN store_id VARCHAR(36);
