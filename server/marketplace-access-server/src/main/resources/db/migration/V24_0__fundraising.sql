CREATE TABLE T_FUNDRAISING(
    id              SERIAL NOT NULL,

    account_id      BIGINT NOT NULL,
    business_id     BIGINT NOT NULL,
    currency        VARCHAR(3),
    status          INT NOT NULL DEFAULT 0,

    created         DATETIME NOT NULL DEFAULT now(),
    updated         DATETIME NOT NULL DEFAULT now() ON UPDATE now(),
    deactivated     DATETIME,

    PRIMARY KEY (id)
);
