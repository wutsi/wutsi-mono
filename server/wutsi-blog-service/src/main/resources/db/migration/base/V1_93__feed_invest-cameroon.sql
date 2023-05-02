INSERT INTO T_USER(site_id, blog, name, full_name, twitter_id, facebook_id, email, picture_url, biography)
    VALUES
        (
            1,
            true,
            'investcameroun',
            'Investir au Cameroun',
            'investcameroun',
            'investcameroun',
            'info@investiraucameroun.com',
            'https://pbs.twimg.com/profile_images/711934790572843008/NJW_4AXT_400x400.jpg',
            'Investir au Cameroun couvre au quotidien l''actualité économique du Cameroun'
        );

INSERT INTO T_ACCOUNT(user_fk, provider_fk, provider_user_id)
    SELECT id, 7, id FROM T_USER WHERE name='investcameroun';

INSERT INTO T_FEED(site_id, user_fk, topic_fk, url)
    SELECT 1, id, 204, 'https://www.investiraucameroun.com/component/obrss/fullrss' FROM T_USER WHERE name='investcameroun';

