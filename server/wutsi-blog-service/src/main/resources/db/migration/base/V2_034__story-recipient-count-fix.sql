CREATE TEMPORARY TABLE TMP_RECIPIENT_COUNT
SELECT entity_id, COUNT(*) AS recipient_count
FROM T_EVENT
WHERE stream_id = 7
  AND type = 'urn:wutsi:blog:event:story-daily-email-sent'
GROUP BY entity_id;

UPDATE T_STORY
set recipient_count=0
WHERE recipient_count IS NULL;

UPDATE T_STORY S JOIN TMP_RECIPIENT_COUNT TMP ON S.id = TMP.entity_id
SET S.recipient_count = TMP.recipient_count;


DROP TABLE TMP_RECIPIENT_COUNT;
