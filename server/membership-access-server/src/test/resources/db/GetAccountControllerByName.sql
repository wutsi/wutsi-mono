INSERT INTO T_CATEGORY(id,title,title_french) VALUES (1000,'Advertising/Marketing','Marketing publicitaire');
INSERT INTO T_CATEGORY(id,title,title_french) VALUES (1001,'Agriculture','Agriculture');

INSERT INTO T_PLACE(id, type, name, name_ascii, country, timezone_id, longitude, latitude)
    VALUES
        (100, 1, 'Yaounde', 'Yaounde', 'CM', 'Africa/Douala', 1.1, 2.2),
        (200, 1, 'Douala', 'Douala', 'CM', 'Africa/Douala', 1.1, 2.2)
;

INSERT INTO T_PHONE(id, number, country)
    VALUES
        (100, '+237221234100', 'CM'),
        (101, '+237221234101', 'CM'),
        (102, '+237221234102', 'CM'),
        (103, '+237221234103', 'CM'),
        (199, '+237221234199', 'CM')
;

INSERT INTO T_NAME(id, value)
    VALUES
        (103, 'yo-name')
;

INSERT INTO T_ACCOUNT(id, phone_fk, category_fk, city_fk, business_id, store_id, name_fk, display_name, picture_url, status, language, super_user, business, email, deactivated, country)
    VALUES
        (100, 100, 1000, 100, 10000, 10001, null, 'Ray Sponsible', 'https://me.com/12343/picture.png', 1, 'fr', true, true, 'ray.sponsible@gmail.com', null, 'GB'),
        (101, 101, null, 100, null, null, null, 'No Category', 'https://me.com/12343/picture.png', 1, 'fr', true, true, null, null, 'CM'),
        (102, 102, -1, 100, null, null, null, 'Invalid Category', 'https://me.com/12343/picture.png', 1, 'fr', false,  false, null, null, 'CM'),
        (103, 103, 1001, 100, null, null, 103, 'Yo Name', null, 1, 'en', false,  false, null, null, 'CM'),
        (199, 199, 1001, 100, null, null, null, 'Deleted', null, 2, 'en', false, false, null, now(), 'CM')
    ;
