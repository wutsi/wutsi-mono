ALTER TABLE T_USER MODIFY COLUMN read_all_languages BOOL DEFAULT null;
UPDATE T_USER SET read_all_languages=null;
