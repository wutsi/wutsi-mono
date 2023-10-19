CREATE TABLE T_XEMAIL
(
    id                 varchar(36)  NOT NULL,
    email              varchar(255) NOT NULL,
    type               INT                   DEFAULT 0,
    creation_date_time DATETIME     NOT NULL DEFAULT now(),

    PRIMARY KEY (id),
    UNIQUE (email)
) ENGINE = InnoDB;
