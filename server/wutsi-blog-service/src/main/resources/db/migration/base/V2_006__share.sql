CREATE TABLE T_SHARE_STORY(
    story_fk BIGINT NOT NULL,
    count INT DEFAULT 0,

    FOREIGN KEY (story_fk)  REFERENCES T_STORY(id),
    PRIMARY KEY(story_fk)
)
