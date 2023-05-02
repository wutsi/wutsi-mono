CREATE TABLE T_FOLLOW(
        id                              BIGINT NOT NULL AUTO_INCREMENT,

        follower_fk                     BIGINT NOT NULL,
        user_fk                         BIGINT NOT NULL,

        follow_date_time                DATETIME NOT NULL DEFAULT NOW(),

        UNIQUE(follower_fk, user_fk),
        FOREIGN KEY (follower_fk)       REFERENCES T_USER(id),
        FOREIGN KEY (user_fk)           REFERENCES T_USER(id),
        PRIMARY KEY(id)
) ENGINE = InnoDB;

