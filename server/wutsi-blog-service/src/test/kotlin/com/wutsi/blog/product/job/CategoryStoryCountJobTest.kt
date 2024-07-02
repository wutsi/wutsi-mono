package com.wutsi.blog.product.job

import com.wutsi.blog.product.dao.CategoryRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/product/CategoryStoryCountJob.sql"])
class CategoryCounterJobTest {
    @Autowired
    private lateinit var job: CategoryStoryCountJob

    @Autowired
    private lateinit var dao: CategoryRepository

    @Test
    fun run() {
        job.run()

        assertStoryCountId(1100, 3)
        assertStoryCountId(1110, 0)
        assertStoryCountId(1111, 0)
        assertStoryCountId(1112, 0)

        assertStoryCountId(1200, 1)
    }

    private fun assertStoryCountId(categoryId: Long, count: Long) {
        val category = dao.findById(categoryId).get()
        assertEquals(count, category.storyCount)
    }
}