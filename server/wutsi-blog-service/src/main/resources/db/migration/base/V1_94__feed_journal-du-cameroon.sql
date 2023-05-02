INSERT INTO T_USER(site_id, blog, name, full_name, twitter_id, facebook_id, email, picture_url, biography)
    VALUES
        (
            1,
            true,
            'jdc',
            'Journal du Cameroun',
            'jdc_fr',
            'JournalDuCamerounOfficiel',
            'info@journalducameroun.com',
            'https://pbs.twimg.com/profile_images/1479014185971367938/MieHqD_K_400x400.jpg',
            'Journal sur l''actualité politique, socio-économique et démographique du #Cameroun '
        );

INSERT INTO T_ACCOUNT(user_fk, provider_fk, provider_user_id)
    SELECT id, 7, id FROM T_USER WHERE name='jdc';

INSERT INTO T_FEED(site_id, user_fk, topic_fk, url)
    SELECT 1, id, 500, 'https://www.journalducameroun.com/feed' FROM T_USER WHERE name='jdc';

