INSERT INTO T_USER(site_id, blog, name, full_name, twitter_id, facebook_id, email, picture_url, website_url, biography)
    VALUES
        (
            1,
            true,
            'people237',
            'People 237',
            'Compeople237',
            'people237com',
            'ci@cynomedia.com',
            'https://pbs.twimg.com/profile_images/1479924378062180358/kgENVQvz_400x400.jpg',
            'https://www.people237.com/',
            'Découvrez en live l''actualité people en photos et vidéos. Toutes les news et actualités de stars, les meilleurs et les pires look de vos stars.'
        );

INSERT INTO T_ACCOUNT(user_fk, provider_fk, provider_user_id)
    SELECT id, 7, id FROM T_USER WHERE name='people237';

INSERT INTO T_FEED(site_id, user_fk, topic_fk, url)
    SELECT 1, id, 100, 'https://www.people237.com/feed' FROM T_USER WHERE name='people237';

