update T_TAG set total_stories= (SELECT COUNT(*) FROM T_STORY_TAG WHERE tag_fk=id);
