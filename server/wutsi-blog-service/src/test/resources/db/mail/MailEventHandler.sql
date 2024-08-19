INSERT INTO T_USER(id, name, email, full_name, picture_url, login_count, country)
VALUES (1, 'ray.sponsible', 'ray.sponsible@gmail.com', 'Ray Sponsible', 'https://picsum.photos/256/256', 5, 'cm'),
       (2, 'john.smith', 'herve.tchepannou@gmail.com', 'John Smith', 'https://picture.com/login', 1, 'cm'),
       (3, 'not-whitelisted', 'user-not-whitelisted@gmail.com', 'John Smith', 'https://picture.com/login', 1, 'cm'),
       (4, 'no-email', null, 'John Smith', 'https://picture.com/login', 1, 'cm'),
       (5, 'alread-sent', 'already-sent@gmail.com', 'Jane Doe', 'https://picture.com/login', 1, 'cm'),
       (6, 'blackisted', 'blackisted@gmail.com', 'Hacker', null, 0, 'cm'),
       (7, 'cold-user', 'tchbansi@hotmail.com', 'Cold User', null, 0, 'cm'),
       (8, 'recent-user', 'herve.tchepannou.ci@gmail.com', 'Recent User', null, 0, 'cm')
;

INSERT INTO T_ACCOUNT(id, provider_fk, user_fk, provider_user_id, login_count, last_login_date_time)
VALUES (10, 1, 1, 'ray.sponsible', 1, '2018-01-01')
;

INSERT INTO T_STORY(id, user_fk, topic_fk, status, published_date_time, title, tagline, summary, language,thumbnail_url)
VALUES (10, 1, 101, 1, '2020-02-19', 'The war in Ukraine create a new front in world domination', 'This is an exemple of tagline', 'This is summary', 'en', 'https://picsum.photos/200/300'),
       (11, 1, 101, 1, '2020-02-18', 'Encode des histoire!', null, 'This is the summary of story #11', 'fr', 'https://picsum.photos/400/400'),
       (12, 1, 100, 1, '2020-02-17', 'Putin va sur la lune!', null, 'This is the summary of story #12', 'fr', 'https://picsum.photos/400/200'),
       (13, 1, 100, 0, '2020-02-16', 'This story is not published!', null, 'This is the summary of story #13', 'fr', 'https://picsum.photos/400/300'),
       (14, 1, 100, 1, '2020-02-19', 'Story without thumbnail', null, 'This is the summary of story #14', 'fr', null),
       (15, 1, 101, 1, '2020-02-19', 'Story for my supporter!', null, 'For my donors only', 'en','https://picsum.photos/200/300'),
       (20, 2, 101, 1, '2020-02-19', 'Roger Milla marque 10 buts!', null, 'This is summary', 'fr','https://picsum.photos/300/300'),
       (30, 1, 101, 0, null, 'Sample Story', 'Sample Tagline', 'This is summary', 'en', 'https://picsum.photos/400/200')
;

INSERT INTO T_SUBSCRIPTION(user_fk, subscriber_fk, last_email_sent_date_time, last_email_opened_date_time, timestamp)
VALUES (1, 2, adddate(now(), interval -2 day), adddate(now(), interval -2 day ), adddate(now(), interval -2 month)),
       (1, 3, adddate(now(), interval -2 day), adddate(now(), interval -2 day ), adddate(now(), interval -2 month)),
       (1, 4, adddate(now(), interval -2 day), adddate(now(), interval -2 day ), adddate(now(), interval -2 month)),
       (1, 5, adddate(now(), interval -2 day), adddate(now(), interval -2 day ), adddate(now(), interval -2 month)),
       (1, 6, adddate(now(), interval -2 day), adddate(now(), interval -2 day ), adddate(now(), interval -2 month)),
       (1, 7, adddate(now(), interval -2 day), null, adddate(now(), interval -2 year)),
       (1, 8, adddate(now(), interval -2 day), null, adddate(now(), interval -1 month))
;
