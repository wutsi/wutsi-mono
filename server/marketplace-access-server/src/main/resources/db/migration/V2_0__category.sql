CREATE TABLE T_CATEGORY(
    id              BIGINT NOT NULL,

    title           VARCHAR(100) NOT NULL,
    title_french    VARCHAR(100),
    title_french_ascii VARCHAR(100),

    PRIMARY KEY (id)
);
ALTER TABLE T_CATEGORY ADD COLUMN parent_fk BIGINT REFERENCES T_CATEGORY(id);
