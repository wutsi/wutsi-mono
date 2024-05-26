CREATE VIEW V_PREFERRED_CATEGORY AS
    SELECT
        CONCAT(R.user_id, '-', S.category_fk) as id,
        R.user_id as user_id,
        S.category_fk category_id,
        count(*) as total_reads
    from T_READER R join T_STORY S on R.story_id=S.id
    group by CONCAT(R.user_id, '-', S.category_fk), R.user_id, S.category_fk