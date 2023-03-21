CREATE TABLE T_DEVICE(
    id            BIGINT NOT NULL,

    token         TEXT NOT NULL,
    type          VARCHAR(30),
    model         VARCHAR(30),
    os_name       TEXT,
    os_version    TEXT,

    created       DATETIME NOT NULL DEFAULT now(),
    updated       DATETIME NOT NULL DEFAULT now(),

    PRIMARY KEY (id)
);
