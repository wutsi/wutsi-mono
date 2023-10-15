UPDATE T_USER
set country=LOWER(country)
WHERE country is not null;
