CREATE TABLE T_PHONE(
    id            SERIAL NOT NULL,
    number        VARCHAR(30) NOT NULL,
    country       VARCHAR(2) NOT NULL,
    created       DATETIME NOT NULL DEFAULT now(),

    UNIQUE(number),
    PRIMARY KEY (id)
);

CREATE TABLE T_ACCOUNT(
    id            SERIAL NOT NULL,

    phone_fk      BIGINT NOT NULL REFERENCES T_PHONE(id),
    city_fk       BIGINT NOT NULL REFERENCES T_PLACE(id),
    category_fk   BIGINT REFERENCES T_CATEGORY(id),

    super_user    BOOLEAN NOT NULL DEFAULT false,
    business      BOOL NOT NULL DEFAULT false,
    display_name  VARCHAR(50) NOT NULL,
    picture_url   TEXT,
    status        INT NOT NULL DEFAULT 0,
    country       VARCHAR(2) NOT NULL,
    language      VARCHAR(2) NOT NULL,
    biography     VARCHAR(160),
    website       TEXT,
    whatsapp      VARCHAR(30),
    street        VARCHAR(160),
    email         VARCHAR(255),
    facebook_id   VARCHAR(30),
    instagram_id  VARCHAR(30),
    twitter_id    VARCHAR(30),
    youtube_id    VARCHAR(30),
    timezone_id   VARCHAR(100),

    created       DATETIME NOT NULL DEFAULT now(),
    updated       DATETIME NOT NULL DEFAULT now(),
    suspended     DATETIME,

    PRIMARY KEY (id)
);
