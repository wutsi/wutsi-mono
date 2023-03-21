CREATE TABLE T_PRODUCT(
    id               SERIAL NOT NULL,

    store_fk         BIGINT NOT NULL REFERENCES T_STORE(id),
    category_fk      BIGINT REFERENCES T_CATEGORY(id),

    title            VARCHAR(100),
    summary          VARCHAR(160),
    description      TEXT,
    price            BIGINT,
    comparable_price BIGINT,
    currency         VARCHAR(3),
    quantity         INT,
    status           INT NOT NULL DEFAULT 0,
    is_deleted       BOOL NOT NULL DEFAULT false,
    created          DATETIME NOT NULL DEFAULT now(),
    updated          DATETIME NOT NULL DEFAULT now() ON UPDATE now(),
    published        DATETIME,
    deleted          DATETIME,

    PRIMARY KEY (id)
);

CREATE TABLE T_PICTURE(
    id              SERIAL NOT NULL,
    product_fk      BIGINT NOT NULL REFERENCES T_PRODUCT(id),
    url             TEXT,
    hash            VARCHAR(32),
    is_deleted      BOOL NOT NULL DEFAULT false,
    created         DATETIME NOT NULL DEFAULT now(),
    deleted         DATETIME,

    PRIMARY KEY (id)
);
CREATE INDEX T_PICTURE__hash ON T_PICTURE(product_fk, hash);

ALTER TABLE T_PRODUCT ADD COLUMN thumbnail_fk BIGINT REFERENCES T_PICTURE(id);
