ALTER TABLE T_USER ADD COLUMN newsletter_delivery_day_of_week INT NOT NULL DEFAULT -1;
CREATE INDEX I_USER_newsletter_delivery_day_of_week ON T_USER(newsletter_delivery_day_of_week);

UPDATE T_USER SET newsletter_delivery_day_of_week=1 WHERE blog=true;
