CREATE TABLE T_MEETING_PROVIDER(
    id        SERIAL NOT NULL,

    type      INT NOT NULL DEFAULT 0,
    name      VARCHAR(30) NOT NULL,
    logo_url  TEXT,

    PRIMARY KEY(id)
);

INSERT INTO T_MEETING_PROVIDER(id, type, name, logo_url)
    VALUES
        (1000, 1, 'Zoom', 'https://prod-wutsi.s3.amazonaws.com/static/marketplace-access-server/meeting-providers/zoom.png'),
        (1001, 2, 'Meet', 'https://prod-wutsi.s3.amazonaws.com/static/marketplace-access-server/meeting-providers/meet.png')
    ;

ALTER TABLE T_PRODUCT DROP COLUMN event_provider;
ALTER TABLE T_PRODUCT ADD COLUMN event_meeting_provider_fk BIGINT REFERENCES T_MEETING_PROVIDER(id);
ALTER TABLE T_PRODUCT ADD COLUMN event_online BOOLEAN NOT NULL DEFAULT false;
