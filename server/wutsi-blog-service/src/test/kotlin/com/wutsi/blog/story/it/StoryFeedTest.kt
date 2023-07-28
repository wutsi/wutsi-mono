package com.wutsi.blog.story.it

import com.wutsi.blog.story.job.StoryFeedJob
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
@Sql(value = ["/db/clean.sql", "/db/story/StoryFeed.sql"])
class StoryFeedTest {
    @Autowired
    private lateinit var job: StoryFeedJob

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
        val content = Files.readString(Path("$storageDir/feeds/stories.csv"))
        assertEquals(
            """
                id,title,author_id,author,language,topic_id,topic,parent_topic_id,parent_topic,tags,url,summary
                1,CAN: Cameroon vs. Argentina: 1-0.,1,Ray Sponsible,en,101,art,100,art-entertainment,CAN-2021,http://localhost:8081/read/1/can-cameroon-vs-argentina-1-0,This is an historic day..
                2,Trump de retour,2,Roger Milla,fr,202,business,200,industry,Trump|Politics,http://localhost:8081/read/2/trump-de-retour,Trump re-elu president une fois de plus!
            """.trimIndent(),
            content.trimIndent(),
        )
    }
}
