package com.wutsi.blog.story.it

import com.wutsi.blog.story.dao.StoryRepository
import com.wutsi.blog.story.job.StoryBagOfWordJob
import com.wutsi.blog.story.service.StoryNLPService
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
@Sql(value = ["/db/clean.sql", "/db/story/StoryBagOfWord.sql"])
class StoryBagOfWordTest {
    @Autowired
    private lateinit var job: StoryBagOfWordJob

    @Autowired
    private lateinit var service: StoryNLPService

    @Autowired
    private lateinit var dao: StoryRepository

    @Value("\${wutsi.platform.storage.local.directory}")
    private lateinit var storageDir: String

    @BeforeEach
    fun setUp() {
        File(storageDir).deleteRecursively()
    }

    @Test
    fun run() {
        // GIVEN
        service.generateStoryBagOfWord(dao.findById(1).get())
        service.generateStoryBagOfWord(dao.findById(2).get())

        // WHEN
        job.run()

        // THEN
        val content = Files.readString(Path("$storageDir/stories/bag-of-words.csv"))
        assertEquals(
            """
                term,tf,idf
                cameroon,,0.3010299956639812
                argentina,,0.3010299956639812
                historic,,0.3010299956639812
                day,,0.3010299956639812
                trump,,0.3010299956639812
                retour,,0.3010299956639812
                re,,0.3010299956639812
                elu,,0.3010299956639812
                president,,0.3010299956639812
            """.trimIndent(),
            content.trimIndent(),
        )
    }
}
