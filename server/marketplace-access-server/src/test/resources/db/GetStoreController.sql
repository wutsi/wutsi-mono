INSERT INTO T_STORE(id, account_id, business_id, product_count, published_product_count, currency, status, deactivated)
    VALUES
        (100, 100, 333, 10, 5, 'XAF', 3, now())
    ;

UPDATE T_STORE
    SET
        return_accepted=true,
        return_contact_window=5,
        return_ship_back_window=10,
        return_message='Yo!',
        cancellation_accepted=true,
        cancellation_window=3,
        cancellation_message='Hurry up'
    WHERE id=100;
