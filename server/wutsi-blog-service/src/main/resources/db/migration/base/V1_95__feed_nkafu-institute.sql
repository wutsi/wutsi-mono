INSERT INTO T_USER(site_id, blog, name, full_name, twitter_id, facebook_id, email, picture_url, biography)
    VALUES
        (
            1,
            true,
            'nkafu',
            'Nkafu Policy Institute',
            'NkafuPolicyInst',
            'DenisLenoraForetiaFoundation',
            'info@nkafu.org',
            'https://pbs.twimg.com/profile_images/378800000743033953/1624739dcdcfc6377451028a21743977_400x400.png',
            'As an independent pan-African think-tank, NKAFU experts produce high quality, innovative policy prescriptions to stimulate economic prosperity in Africa'
        );

INSERT INTO T_ACCOUNT(user_fk, provider_fk, provider_user_id)
    SELECT id, 7, id FROM T_USER WHERE name='nkafu';

INSERT INTO T_FEED(site_id, user_fk, topic_fk, url)
    SELECT 1, id, 204, 'https://nkafu.org/feed' FROM T_USER WHERE name='nkafu';

