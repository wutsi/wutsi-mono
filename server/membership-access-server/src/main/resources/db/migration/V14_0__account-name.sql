CREATE TABLE T_NAME(
    id            SERIAL NOT NULL,

    value         VARCHAR(36) NOT NULL,
    created       DATETIME NOT NULL DEFAULT now(),
    updated       DATETIME NOT NULL DEFAULT now(),

    UNIQUE(value),
    PRIMARY KEY (id)
);

ALTER TABLE T_ACCOUNT ADD COLUMN name_fk BIGINT REFERENCES T_ACCOUNT_NAME(id);
