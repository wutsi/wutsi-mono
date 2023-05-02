INSERT INTO T_USER(site_id, name, email, full_name, picture_url)
    VALUES
        (1, 'camfoot', 'info@camfoot.com', 'Camfoot', 'https://pbs.twimg.com/profile_images/1141658972/camfoot-facebook_400x400.png');

INSERT INTO T_ACCOUNT(user_fk, provider_fk, provider_user_id)
    SELECT id, 7, id FROM T_USER WHERE name='camfoot';

INSERT INTO T_FEED(site_id, user_fk, topic_fk, url)
    SELECT 1, id, 117, 'https://camfoot.com/feed' FROM T_USER WHERE name='camfoot';

