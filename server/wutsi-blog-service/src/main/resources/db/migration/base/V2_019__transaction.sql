DROP TABLE IF EXISTS T_TRANSACTION;
CREATE TABLE T_TRANSACTION(
    id                          VARCHAR(36) NOT NULL,
    idempotency_key             VARCHAR(36) NOT NULL,

    type                        INT NOT NULL,
    status                      INT NOT NULL,
    wallet_fk                   VARCHAR(36) NOT NULL REFERENCES T_WALLET(id),
    user_fk                     BIGINT NULL REFERENCES T_USER(id),
    email                       VARCHAR(100),
    anonymous                   BOOLEAN NOT NULL DEFAULT 0,

    amount                      BIGINT NOT NULL DEFAULT 0,
    fees                        BIGINT NOT NULL DEFAULT 0,
    net                         BIGINT NOT NULL DEFAULT 0,
    gateway_fees                BIGINT NOT NULL DEFAULT 0,
    currency                    VARCHAR(3) NOT NULL,
    description                 VARCHAR(100),

    payment_method_owner        VARCHAR(100) NOT NULL,
    payment_method_number       VARCHAR(30) NOT NULL,
    payment_method_type         INT NOT NULL,
    gateway_type                INT NOT NULL,
    gateway_transaction_id      VARCHAR(100),
    error_code                  VARCHAR(100),
    error_message               TEXT,
    supplier_error_code         VARCHAR(100),

    creation_date_time          DATETIME NOT NULL DEFAULT now(),
    last_modification_date_time DATETIME NOT NULL DEFAULT now() ON UPDATE now(),

    UNIQUE(idempotency_key),
    PRIMARY KEY(id)
);

CREATE INDEX I_TRANSACTION_wallet_type_status ON T_TRANSACTION(wallet_fk, type, status);

ALTER TABLE T_WALLET ADD COLUMN donation_count BIGINT NOT NULL DEFAULT 0;
