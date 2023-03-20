CREATE TABLE T_KEY(
    id            SERIAL NOT NULL,
    algorithm     TEXT NOT NULL,
    public_key    TEXT NOT NULL,
    private_key   TEXT NOT NULL,
    created       DATETIME NOT NULL DEFAULT now(),
    expires       DATETIME NOT NULL,

    PRIMARY KEY (id)
);
