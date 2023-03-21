INSERT INTO T_PLACE(id, name, name_ascii, country, timezone_id, longitude, latitude)
    VALUES
        (100, 'Yaoudé', 'Yaounde', 'CM', 'Africa/Douala', 1.1, 2.2);

INSERT INTO T_PHONE(id, number, country)
    VALUES
        (100, '+237221234100', 'CM'),
        (101, '+237221234101', 'CM'),
        (200, '+237221234200', 'CM'),
        (201, '+237221234201', 'CM'),
        (300, '+237221234300', 'CM')
;

INSERT INTO T_ACCOUNT(id, phone_fk, city_fk, status, display_name, country, language)
    VALUES
        (101, 101, 100, 1, 'Yo man', 'CM', 'fr'),
        (200, 200, 100, 1, 'John Doe', 'CM', 'fr'),
        (300, 300, 100, 2, 'Roger Milla - Suspended', 'CM', 'fr')
;
