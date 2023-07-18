INSERT INTO T_USER(id, super_user, name, email, full_name) VALUES
  (111, false, 'john111', 'john111.partner@gmail.com', 'Jane Doe'),
  (211, false, 'john200', 'john200.partner@gmail.com', 'Yo Man')
;

INSERT INTO T_STORY(id, user_fk, title, tagline, summary, language, status, read_count) VALUES
  (100, 111, 'Story100', 'Sample Tagline', 'This is summary', 'en', 1, 1000),
  (101, 111, 'Story101', 'Sample Tagline', 'This is summary', 'en', 1, 11),
  (200, 211, 'Story200', 'Sample Tagline', 'This is summary', 'en', 1, 2000)
;

INSERT INTO T_STORY_KPI(story_id, type, year, month, value)
    VALUES
        (200, 1, YEAR(now()), MONTH(now())+1, 11)
    ;

INSERT INTO T_USER_KPI(user_id, type, year, month, value)
    VALUES
        (211, 1, YEAR(now()), MONTH(now()), 555)
    ;
