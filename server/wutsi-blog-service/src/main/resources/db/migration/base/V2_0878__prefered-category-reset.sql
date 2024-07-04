drop view V_PREFERRED_CATEGORY
create view V_PREFERRED_CATEGORY as
    select
        CONCAT(R.user_id, '-', S.category_fk) as id,
        R.user_id as user_id,
        S.category_fk category_id,
        count(*) as total_reads
    from T_READER R join T_STORY S on R.story_id=S.id
	where R.liked=true OR R.subscribed=true OR R.commented=true
    group by CONCAT(R.user_id, '-', S.category_fk), R.user_id, S.category_fk;