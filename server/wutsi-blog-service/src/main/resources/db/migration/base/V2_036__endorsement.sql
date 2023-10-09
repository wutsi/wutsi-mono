CREATE TABLE T_ENDORSEMENT
(
    id                     BIGINT   NOT NULL AUTO_INCREMENT,

    user_fk                BIGINT   NOT NULL REFERENCES T_USER (id),
    endorser_fk            BIGINT   NOT NULL REFERENCES T_USER (id),
    blurb                  VARCHAR(255),
    creation_date_time     DATETIME          DEFAULT NOW(),
    modification_date_time DATETIME NOT NULL DEFAULT now() ON UPDATE now(),

    UNIQUE (user_fk, endorser_fk),
    PRIMARY KEY (id)
) ENGINE = InnoDB;

ALTER TABLE T_USER
    ADD COLUMN endorser_count BIGINT DEFAULT 0;

