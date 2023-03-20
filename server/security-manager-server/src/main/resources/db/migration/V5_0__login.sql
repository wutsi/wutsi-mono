CREATE TABLE T_LOGIN(
    id              SERIAL,
    account_id      BIGINT NOT NULL REFERENCES T_PASSWORD(id),
    hash            VARCHAR(32) NOT NULL,
    access_token    TEXT NOT NULL,
    created         DATETIME NOT NULL DEFAULT now(),
    expired         DATETIME,
    expires         DATETIME NOT NULL DEFAULT now(),

    UNIQUE(hash),
    PRIMARY KEY (id)
);

