INSERT INTO T_KEY(id, algorithm, public_key, private_key, expires)
    VALUES
        (100, 'RSA', 'public-key-1', 'private-key-1', '2100-01-01'),
        (200, 'RSA', 'public-key-2-expired', 'private-key-2', '2000-01-01')
;
