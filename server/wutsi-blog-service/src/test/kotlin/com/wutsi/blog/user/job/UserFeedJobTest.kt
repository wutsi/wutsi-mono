package com.wutsi.blog.user.job

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.jdbc.Sql
import java.io.File
import java.nio.file.Files
import kotlin.io.path.Path
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Sql(value = ["/db/clean.sql", "/db/user/UserFeedJob.sql"])
class UserFeedJobTest {
    @Autowired
    private lateinit var job: UserFeedJob

    @Value("\${wutsi.platform.storage.local.directory}")
    private lateinit var storageDir: String

    @BeforeEach
    fun setUp() {
        File(storageDir).deleteRecursively()
    }

    @Test
    fun run() {
        // WHEN
        job.run()

        // THEN
        val content = Files.readString(Path("$storageDir/feeds/users.csv"))
        assertEquals(
            """
                id
                1
                2
                3
                4
                5
                6
                10
                11
                20
                21
                30
                40
                50
                51
            """.trimIndent(),
            content.trimIndent(),
        )
    }
}
