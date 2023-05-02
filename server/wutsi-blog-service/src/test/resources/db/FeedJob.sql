INSERT INTO T_USER(site_id, name, email, full_name, picture_url)
    VALUES
        (1, 'camfoot', 'info@camfoot.com', 'Camfoot', 'https://pbs.twimg.com/profile_images/1141658972/camfoot-facebook_400x400.png'),
        (1, 'jewanda', 'info@jewanda.com', 'JeWanda', 'https://pbs.twimg.com/profile_images/1141658972/jewanda-facebook_400x400.png'),
        (1, 'investir-au-cameroon', 'info@investiraucameroon.com', 'Investir au Cameroun', 'https://pbs.twimg.com/profile_images/1141658972/jewanda-facebook_400x400.png'),
        (1, 'jdc', 'info@jdc.com', 'Journal du Cameroun', 'https://pbs.twimg.com/profile_images/1479014185971367938/MieHqD_K_400x400.jpg'),
        (1, 'nkafu', 'info@nkafu.org', 'Nkafu Policy Institute', 'https://pbs.twimg.com/profile_images/378800000743033953/1624739dcdcfc6377451028a21743977_400x400.png')
    ;


INSERT INTO T_ACCOUNT(user_fk, provider_fk, provider_user_id)
    SELECT id, 7, id FROM T_USER WHERE name IN ('camfoot', 'investcameroun', 'jewanda', 'jdc', 'nkafu');


INSERT INTO T_FEED(site_id, user_fk, topic_fk, url)
    SELECT 1, id, 117, 'https://camfoot.com/feed' FROM T_USER WHERE name='camfoot';
INSERT INTO T_FEED(site_id, user_fk, topic_fk, url)
    SELECT 1, id, 100, 'https://www.jewanda.com/feed' FROM T_USER WHERE name='jewanda';
INSERT INTO T_FEED(site_id, user_fk, topic_fk, url)
    SELECT 1, id, 204, 'https://www.investiraucameroun.com/feed' FROM T_USER WHERE name='investir-au-cameroon';
INSERT INTO T_FEED(site_id, user_fk, topic_fk, url)
    SELECT 1, id, 500, 'https://www.journalducameroun.com/feed' FROM T_USER WHERE name='jdc';
INSERT INTO T_FEED(site_id, user_fk, topic_fk, url)
    SELECT 1, id, 204, 'https://nkafu.org/feed' FROM T_USER WHERE name='nkafu';

