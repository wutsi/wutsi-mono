create view V_BLOG_CATEGORY as
	select U.id as user_id, ifnull(C.parent_fk, C.id) as category_id, count(*) as count
		from T_STORY S join T_CATEGORY C on S.category_fk=C.id join T_USER U on S.user_fk=U.id
		where U.active=true
		    and U.suspended=false
		    and U.blog=true
		    and S.status=1
		    and S.deleted=false
		group by user_id, category_id;

alter table T_USER add column category_fk BIGINT references T_CATEGORY(id);
update T_USER U set U.category_fk=(
    select V.category_id from V_BLOG_CATEGORY V where U.id=V.user_id order by V.count desc limit 1
) where U.active=true
    and U.suspended=false
    and U.blog=true;

alter table T_CATEGORY add column story_count BIGINT NOT NULL default 0;
update T_CATEGORY C set story_count=(
    select ifnull(sum(V.count), 0) from V_BLOG_CATEGORY V where V.category_id=C.id
) where level=0