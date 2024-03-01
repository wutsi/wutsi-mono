CREATE TABLE T_ADS
(
    id                     VARCHAR(36) NOT NULL,

    user_fk                BIGINT NOT NULL REFERENCES T_USER (id),

    title                  VARCHAR(255) NOT NULL,
    type                   INT NOT NULL DEFAULT 0,
    cta_type               INT NOT NULL DEFAULT 0,
    status                 INT NOT NULL DEFAULT 0,
    image_url              TEXT,
    url                    TEXT,
    duration_days          INT NOT NULL DEFAULT 1,
    total_impressions      BIGINT NOT NULL DEFAULT 0,
    total_clicks           BIGINT NOT NULL DEFAULT 0,
    start_date             DATETIME,
    end_date               DATETIME,
    creation_date_time     DATETIME NOT NULL DEFAULT now(),
    modification_date_time DATETIME NOT NULL DEFAULT now(),
    completed_date_time    DATETIME,

    PRIMARY KEY (id)
) ENGINE = InnoDB;