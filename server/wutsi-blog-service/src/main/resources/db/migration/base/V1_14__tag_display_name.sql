DELETE FROM T_TAG WHERE total_stories=0;

ALTER TABLE T_TAG ADD COLUMN display_name VARCHAR(100) NOT NULL;
UPDATE T_TAG set display_name=name, name=LOWER(name);
