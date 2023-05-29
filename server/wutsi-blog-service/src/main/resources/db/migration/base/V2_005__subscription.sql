CREATE TABLE T_SUBSCRIPTION(
   id                      BIGINT NOT NULL AUTO_INCREMENT,

   user_fk                 BIGINT NOT NULL,
   subscriber_fk           BIGINT NOT NULL,

   timestamp               DATETIME DEFAULT NOW(),

   FOREIGN KEY (user_fk)        REFERENCES T_USER(id),
   FOREIGN KEY (subscriber_fk)  REFERENCES T_USER(id),
   UNIQUE(user_fk, subscriber_fk),
   PRIMARY KEY(id)
) ENGINE = InnoDB;

CREATE TABLE T_SUBSCRIPTION_USER(
    user_fk BIGINT NOT NULL,
    count INT DEFAULT 0,

    FOREIGN KEY (user_fk)  REFERENCES T_USER(id),
    PRIMARY KEY(user_fk)
)
