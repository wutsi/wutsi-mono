DELETE FROM T_COMMENT_V2;
DELETE FROM T_COMMENT_STORY;

DELETE FROM T_EVENT;
CREATE INDEX I_EVENT_stream_entity_type_user ON T_EVENT(stream_id, entity_id, type, user_id);
CREATE INDEX I_EVENT_stream_entity_type_device ON T_EVENT(stream_id, entity_id, type, device_id);
