INSERT INTO T_CATEGORY(id,title,title_french) VALUES (1000,'Advertising/Marketing','Marketing publicitaire');
INSERT INTO T_CATEGORY(id,title,title_french) VALUES (1001,'Agriculture','Agriculture');

INSERT INTO T_PLACE(id, name, name_ascii, country, timezone_id, longitude, latitude)
    VALUES
        (100, 'Yaounde', 'Yaoude_e_', 'CM', 'Africa/Douala', 1.1, 2.2),
        (200, 'Douable', 'Douala', 'CM', 'Africa/Douala', 1.1, 2.2);

INSERT INTO T_PHONE(id, number, country)
    VALUES
        (100, '+237221234100', 'CM'),
        (101, '+237221234101', 'CM'),
        (102, '+237221234102', 'CM'),
        (199, '+237221234199', 'CM')
;

INSERT INTO T_NAME(id, value)
    VALUES
        (101, 'yoman')
;

INSERT INTO T_ACCOUNT(id, phone_fk, city_fk, name_fk, business, display_name, picture_url, status, country, language)
    VALUES
        (100, 100, 100, null, false, 'Ray Sponsible', 'https://me.com/12343/picture.png', 1, 'CM', 'fr'),
        (101, 101, 100, 101, false, 'Yo Man', 'https://me.com/12343/picture.png', 1, 'CM', 'fr'),
        (102, 102, 100, null, false, 'Yo Man', 'https://me.com/12343/picture.png', 1, 'CM', 'fr'),
        (199, 199, 100, null, false, 'Deleted', null, 2, 'CM', 'fr')
;
