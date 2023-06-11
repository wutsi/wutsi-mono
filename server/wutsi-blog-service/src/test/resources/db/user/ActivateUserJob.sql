INSERT INTO T_USER(id, name, full_name, blog, suspended, active, last_publication_date_time) VALUES
    (1, 'ray1', 'Ray', false, false, false, null),
    (2, 'ray2', 'Ray', true,  false, false, '2020-01-01'),
    (3, 'ray3', 'Ray', true,  false, false, now()),
    (4, 'ray4', 'Ray', true,  true,  false, now()),
    (5, 'ray4', 'Ray', true,  false, true,  now())
;
