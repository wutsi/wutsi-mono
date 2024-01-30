CREATE TABLE T_TRANSACTION_EVENT
(
    id                     BIGINT       NOT NULL AUTO_INCREMENT,

    transaction_id         VARCHAR(100) NOT NULL,
    status_code            INT NOT NULL,
    method                 VARCHAR(10)  NOT NULL,
    uri                    TEXT,
    request                TEXT,
    response               TEXT,
    creation_date_time     DATETIME    NOT NULL DEFAULT now(),

    PRIMARY KEY (id)
) ENGINE = InnoDB;

CREATE INDEX T_TRANSACTION_EVENT_provider ON T_TRANSACTION_EVENT(transaction_id);