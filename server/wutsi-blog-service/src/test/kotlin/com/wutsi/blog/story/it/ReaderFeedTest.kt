package com.wutsi.blog.story.it

import com.wutsi.blog.story.job.ReaderFeedJob
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
@Sql(value = ["/db/clean.sql", "/db/story/ReaderFeed.sql"])
class ReaderFeedTest {
    @Autowired
    private lateinit var job: ReaderFeedJob

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
        val content = Files.readString(Path("$storageDir/feeds/readers.csv"))
        assertEquals(
            """
                story_id,user_id,commented,liked,subscribed
                1,2,1,1,
                2,2,,,
                3,1,1,1,1
            """.trimIndent(),
            content.trimIndent(),
        )
    }
}
