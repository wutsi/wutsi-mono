-- Professional resources -> Business
UPDATE T_STORY set category_fk=2314 WHERE category_fk=1212;
UPDATE T_STORY set category_fk=2310 WHERE category_fk=1213;
UPDATE T_STORY set category_fk=2328 WHERE category_fk=1214;
UPDATE T_STORY set category_fk=2329 WHERE category_fk=1215;
UPDATE T_STORY set category_fk=2313 WHERE category_fk=1216;
UPDATE T_STORY set category_fk=2317 WHERE category_fk=1218;
UPDATE T_STORY set category_fk=2330 WHERE category_fk=1220;
UPDATE T_STORY set category_fk=2331 WHERE category_fk=1221;
UPDATE T_STORY set category_fk=2327 WHERE category_fk=1222;
UPDATE T_PRODUCT set category_fk=2314 WHERE category_fk=1212;
UPDATE T_PRODUCT set category_fk=2310 WHERE category_fk=1213;
UPDATE T_PRODUCT set category_fk=2328 WHERE category_fk=1214;
UPDATE T_PRODUCT set category_fk=2329 WHERE category_fk=1215;
UPDATE T_PRODUCT set category_fk=2313 WHERE category_fk=1216;
UPDATE T_PRODUCT set category_fk=2317 WHERE category_fk=1218;
UPDATE T_PRODUCT set category_fk=2330 WHERE category_fk=1220;
UPDATE T_PRODUCT set category_fk=2331 WHERE category_fk=1221;
UPDATE T_PRODUCT set category_fk=2327 WHERE category_fk=1222;

DELETE FROM T_CATEGORY where parent_fk=1200;
DELETE FROM T_CATEGORY where id=1200;

-- Literature > Manga -> Comics > Manga
UPDATE T_STORY set category_fk=2427 WHERE category_fk=1124;
UPDATE T_PRODUCT set category_fk=2427 WHERE category_fk=1124;

DELETE FROM T_CATEGORY where id=1124;
