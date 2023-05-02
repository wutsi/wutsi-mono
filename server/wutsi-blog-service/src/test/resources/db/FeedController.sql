INSERT INTO T_USER(site_id, name, email, full_name, picture_url)
    VALUES
        (1, 'camfoot', 'info@camfoot.com', 'Camfoot', 'https://pbs.twimg.com/profile_images/1141658972/camfoot-facebook_400x400.png'),
        (1, 'jewanda', 'info@jewanda.com', 'JeWanda', 'https://pbs.twimg.com/profile_images/1141658972/jewanda-facebook_400x400.png'),
        (1, 'investcameroun','info@investiraucameroun.com', 'Investir au Cameroun','https://pbs.twimg.com/profile_images/711934790572843008/NJW_4AXT_400x400.jpg'),
        (1, 'jdc', 'info@jdc.com', 'Journal du Cameroun', 'https://pbs.twimg.com/profile_images/1479014185971367938/MieHqD_K_400x400.jpg'),
        (1, 'nkafu', 'info@nkafu.org', 'Nkafu Policy Institute', 'https://pbs.twimg.com/profile_images/378800000743033953/1624739dcdcfc6377451028a21743977_400x400.png'),
        (1, 'people237', 'info@people237.org', 'People 237', null)
    ;


INSERT INTO T_ACCOUNT(user_fk, provider_fk, provider_user_id)
    SELECT id, 7, id FROM T_USER WHERE name IN ('camfoot', 'investcameroun', 'jewanda', 'jdc', 'nkafu', 'people237');


INSERT INTO T_FEED(id, site_id, user_fk, topic_fk, url)
    SELECT 100, 1, id, 117, 'https://camfoot.com/feed' FROM T_USER WHERE name='camfoot';
INSERT INTO T_FEED(id, site_id, user_fk, topic_fk, url)
    SELECT 101, 1, id, 204, 'https://www.investiraucameroun.com/component/obrss/fullrss' FROM T_USER WHERE name='investcameroun';
INSERT INTO T_FEED(id, site_id, user_fk, topic_fk, url)
    SELECT 102, 1, id, 100, 'https://www.jewanda.com/feed' FROM T_USER WHERE name='jewanda';
INSERT INTO T_FEED(id, site_id, user_fk, topic_fk, url)
    SELECT 103, 1, id, 100, 'https://www.people237.com/feed' FROM T_USER WHERE name='people237';

