INSERT INTO T_CATEGORY(id, title, long_title) VALUES
    (1000, 'category1', 'category1'),
    (1100, 'category2', 'category2')
;

INSERT INTO T_USER(id, name, full_name, blog, last_publication_date_time) VALUES
    (1, 'ray1', 'Ray', false, null),
    (2, 'ray2', 'Ray', true, '2020-01-01'),
    (3, 'ray3', 'Ray', true, now()),
    (4, 'ray4', 'Ray', true, now()),
    (5, 'ray4', 'Ray', true,  now())
;

INSERT INTO T_STORY(id, user_fk, category_fk, status) VALUES
    (20, 2, 1000, 1),
    (21, 2, 1000, 1),
    (22, 2, 1000, 1),
    (23, 2, 1000, 1),
    (24, 2, 1100, 1),

    (30, 3, 1100, 1)
;