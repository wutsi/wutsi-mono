CREATE TABLE T_CATEGORY
(
    id                 BIGINT       NOT NULL,

    title              VARCHAR(100) NOT NULL,
    title_french       VARCHAR(100),
    title_french_ascii VARCHAR(100),
    level              INT          NOT NULL DEFAULT 0,
    long_title         TEXT         NOT NULL,
    long_title_french  TEXT,

    PRIMARY KEY (id)
);
ALTER TABLE T_CATEGORY
    ADD COLUMN parent_fk BIGINT REFERENCES T_CATEGORY (id);

ALTER TABLE T_PRODUCT
    ADD COLUMN category_fk BIGINT REFERENCES T_CATEGORY (id);
