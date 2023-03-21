CREATE TABLE T_PLACE(
    id             SERIAL NOT NULL,

    type           INT DEFAULT 0,
    name           VARCHAR(100) NOT NULL,
    name_french    VARCHAR(100) NOT NULL,
    country        VARCHAR(2) NOT NULL,
    timezone_id    VARCHAR(100) NOT NULL,
    longitude      DECIMAL(10, 4),
    latitude       DECIMAL(10, 4),
    created        DATETIME DEFAULT NOW(),
    updated        DATETIME NOT NULL DEFAULT now() ON UPDATE now(),

    PRIMARY KEY (id)
);

