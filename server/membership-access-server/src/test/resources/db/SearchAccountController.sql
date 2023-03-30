INSERT INTO T_PLACE(id, name, name_ascii, country, timezone_id, longitude, latitude)
    VALUES
        (100, 'Yaounde', 'Yaoundé', 'CM', 'Africa/Douala', 1.1, 2.2),
        (101, 'Douala', 'Douala', 'CM', 'Africa/Douala', 1.1, 2.2),
        (102, 'Baffoussam', 'Baffoussam', 'CM', 'Africa/Douala', 1.1, 2.2)
    ;

INSERT INTO T_PHONE(id, number, country)
    VALUES
        (100, '+237221234100', 'CM'),
        (101, '+237221234111', 'CM'),
        (102, '+237221234112', 'CM'),
        (103, '+237221234113', 'CM'),
        (200, '+237221234200', 'CM')
;

INSERT INTO T_NAME(id, value)
    VALUES
        (100, 'ray.sponsible')
    ;

INSERT INTO T_ACCOUNT(id, phone_fk, city_fk, name_fk, display_name, picture_url, status, language, super_user, business, country, store_id, fundraising_id)
    VALUES
        (100, 100, 100, 100, 'Ray Sponsible', 'https://me.com/12343/picture.png', 1, 'fr', true, true, 'CM', 9100, 9101),
        (101, 101, 102, null, 'John Smith', 'https://me.com/111/picture.png', 1, 'fr', false, false, 'CM', null, null),
        (102, 102, 102, null, 'Roger Milla', 'https://me.com/111/picture.png', 1, 'fr', false, true, 'CM', null, null),
        (888, 200, 100, null, 'John Smith', 'https://me.com/111/picture.png', 1, 'fr', false, false, 'CM', null, null),

        (300, 103, 100, null, 'John Smith (suspended)', 'https://me.com/111/picture.png', 2, 'fr', false, false, 'CM', null, null),
        (301, 103, 102, null, 'John Smith', 'https://me.com/111/picture.png', 1, 'fr', false, false, 'CM', null, null)
    ;
