package com.wutsi.blog.story.it

import com.wutsi.blog.story.dao.TagRepository
import com.wutsi.blog.story.job.TagTotalStoriesCompilerJob
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Sql(value = ["/db/clean.sql", "/db/TagCronTab.sql"])
class TagTotalStoriesCompilerJobTest {
    @Autowired
    private lateinit var cron: TagTotalStoriesCompilerJob

    @Autowired
    private lateinit var tagDao: TagRepository

    @Test
    fun updateTotalStories() {
        cron.compile()

        assertTotalStories(1L, 3L)
        assertTotalStories(2L, 0L)
        assertTotalStories(3L, 1L)
        assertTotalStories(4L, 2L)
    }

    private fun assertTotalStories(tagId: Long, count: Long) {
        val tag = tagDao.findById(tagId).get()
        assertEquals(count, tag.totalStories)
    }
}
