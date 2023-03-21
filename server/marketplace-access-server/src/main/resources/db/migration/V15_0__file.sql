CREATE TABLE T_FILE(
    id              SERIAL NOT NULL,

    product_fk      BIGINT NOT NULL REFERENCES T_PRODUCT(id),

    name            VARCHAR(255) NOT NULL,
    url             TEXT,
    content_size    INT NOT NULL DEFAULT 0,
    content_type    VARCHAR(255) NOT NULL,
    is_deleted      BOOL NOT NULL DEFAULT false,
    created         DATETIME NOT NULL DEFAULT now(),
    deleted         DATETIME,

    PRIMARY KEY(id)
);
