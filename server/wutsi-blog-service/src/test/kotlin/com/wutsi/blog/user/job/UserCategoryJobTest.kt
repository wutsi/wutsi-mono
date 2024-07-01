package com.wutsi.blog.user.job

import com.wutsi.blog.user.dao.UserRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Sql(value = ["/db/clean.sql", "/db/user/UserCategoryJob.sql"])
class UserCategoryJobTest {
    @Autowired
    private lateinit var job: UserCategoryJob

    @Autowired
    private lateinit var dao: UserRepository

    @Test
    fun run() {
        job.run()

        assertCategoryId(1L, null)
        assertCategoryId(2L, 1000)
        assertCategoryId(3L, 1100)
    }

    private fun assertCategoryId(id: Long, categoryId: Long?) {
        val user = dao.findById(id).get()
        assertEquals(categoryId, user.categoryId)
    }
}