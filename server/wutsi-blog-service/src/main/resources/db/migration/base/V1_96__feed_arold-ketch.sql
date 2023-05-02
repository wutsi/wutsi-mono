INSERT INTO T_USER(site_id, blog, name, full_name, twitter_id, facebook_id, email, picture_url, biography)
    VALUES
        (
            1,
            true,
            'arolketchraconte',
            'Arol Ketch Raconte',
            'arol_ketch',
            'arolketchraconte',
            'arolketchraconte@gmail.com',
            'https://pbs.twimg.com/profile_images/1479924378062180358/kgENVQvz_400x400.jpg',
            'Arol KETCH raconte des faits marquants, des histoires'
        );

INSERT INTO T_ACCOUNT(user_fk, provider_fk, provider_user_id)
    SELECT id, 7, id FROM T_USER WHERE name='arolketchraconte';

INSERT INTO T_FEED(site_id, user_fk, topic_fk, url)
    SELECT 1, id, 500, 'https://www.arolketchraconte.com/feed' FROM T_USER WHERE name='arolketchraconte';

