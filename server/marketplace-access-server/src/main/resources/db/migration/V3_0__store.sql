CREATE TABLE T_STORE(
    id              BIGINT NOT NULL,

    account_id              BIGINT NOT NULL,
    product_count           INT NOT NULL DEFAULT 0,
    published_product_count INT NOT NULL DEFAULT 0,
    is_deleted              BOOLEAN NOT NULL DEFAULT false,

    created                DATETIME NOT NULL DEFAULT now(),
    updated                DATETIME NOT NULL DEFAULT now() ON UPDATE now(),
    deleted                DATETIME,

    PRIMARY KEY (id)
);
