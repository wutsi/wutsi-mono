ALTER TABLE T_STORY
    ADD COLUMN recipient_count BIGINT DEFAULT 0;

CREATE TEMPORARY TABLE TMP_RECIPIENT_COUNT
SELECT entity_id, COUNT(*) AS recipient_count
FROM T_EVENT
WHERE stream_id = 7
  AND type = 'urn:wutsi:blog:event:story-daily-email-sent'
GROUP BY entity_id;

UPDATE T_STORY S, T_USER U
SET S.recipient_count = (SELECT recipient_count FROM TMP_RECIPIENT_COUNT TMP WHERE TMP.entity_id = S.id)
WHERE S.user_fk = U.id
  AND U.follower_count > 0;

DROP TABLE TMP_RECIPIENT_COUNT;
