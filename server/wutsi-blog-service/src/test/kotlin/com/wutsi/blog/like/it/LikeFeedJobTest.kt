package com.wutsi.blog.like.it

import com.wutsi.blog.like.job.LikeFeedJob
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
@Sql(value = ["/db/clean.sql", "/db/like/LikeFeedJob.sql"])
class LikeFeedJobTest {
    @Autowired
    private lateinit var job: LikeFeedJob

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
        val content = Files.readString(Path("$storageDir/feeds/likes.csv"))
        assertEquals(
            """
                story_id,user_id,device_id,like_date
                100,111,,2023-08-06
                100,,device-search,2023-08-06
                101,,device-search,2023-08-06
            """.trimIndent(),
            content.trimIndent(),
        )
    }
}
