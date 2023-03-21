INSERT INTO T_PLACE(id, name, name_ascii, country, timezone_id, longitude, latitude)
    VALUES
        (100, 'Yaounde', 'Yaoude_e_', 'CM', 'Africa/Douala', 1.1, 2.2),
        (200, 'Douable', 'Douala', 'CM', 'Africa/Douala', 1.1, 2.2);

INSERT INTO T_PHONE(id, number, country)
    VALUES
        (100, '+237221234100', 'CM'),
        (101, '+237221234101', 'CM')
;

INSERT INTO T_ACCOUNT(id, phone_fk, city_fk, business_id, store_id, business, display_name, picture_url, status, country, language)
    VALUES
        (100, 100, 100, true, 11, 22, 'Ray Sponsible', 'https://me.com/12343/picture.png', 1, 'CM', 'fr'),
        (199, 101, 100, true, 33, 44, 'Deleted', null, 2, 'CM', 'fr')
;
