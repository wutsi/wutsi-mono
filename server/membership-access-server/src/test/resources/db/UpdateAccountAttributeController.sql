INSERT INTO T_CATEGORY(id,title,title_french) VALUES (1000,'Advertising/Marketing','Marketing publicitaire');
INSERT INTO T_CATEGORY(id,title,title_french) VALUES (1001,'Agriculture','Agriculture');

INSERT INTO T_PLACE(id, name, name_ascii, country, timezone_id, longitude, latitude)
    VALUES
        (100, 'Yaounde', 'Yaoude_e_', 'CM', 'Africa/Douala', 1.1, 2.2),
        (200, 'Douala', 'Douala', 'CM', 'Africa/Douala', null, null)
    ;

INSERT INTO T_PHONE(id, number, country)
    VALUES
        (100, '+237221234100', 'CM'),
        (101, '+237221234101', 'CM'),
        (199, '+237221234199', 'CM'),
        (200, '+237221234200', 'CM'),
        (201, '+237221234201', 'CM'),
        (202, '+237221234202', 'CM')
;

INSERT INTO T_NAME(id, value)
    VALUES
        (200, 'yo-man'),
        (201, 'duplicatename'),
        (202, 'clear-me')
;

INSERT INTO T_ACCOUNT(id, phone_fk, city_fk, category_fk, name_fk, status, display_name, picture_url, country, language)
    VALUES
        (100, 100, 100, 1000, null, 1, 'Ray Sponsible', 'https://me.com/12343/picture.png', 'CM', 'fr'),
        (101, 101, 100, null, null, 1, 'Thomas Nkono', 'https://me.com/101/picture.png', 'CM', 'fr'),
        (199, 199, 100, null, null, 2, 'Suspended', null, 'CM', 'fr'),
        (200, 200, 100, null, 200, 1, 'Yo Man', null, 'CM', 'fr'),
        (201, 201, 100, null, 201, 1, 'Duplicate Name', null, 'CM', 'fr'),
        (202, 202, 100, null, 202, 1, 'clear me', null, 'CM', 'fr')
    ;
