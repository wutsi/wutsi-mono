package com.wutsi.ml.document.service

import com.wutsi.platform.core.storage.StorageService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.io.ByteArrayInputStream
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class DocumentLoaderTest {
    @Autowired
    private lateinit var loader: DocumentLoader

    @Autowired
    private lateinit var storage: StorageService

    @Test
    fun load() {
        // GIVEN
        storage.store(
            path = "feeds/stories.csv",
            content = ByteArrayInputStream(
                """
                    id,title,author_id,author,language,topic_id,topic,parent_topic_id,parent_topic,tags,url,summary,published_date
                    1,CAN: Cameroon vs. Argentina: 1-0.,1,Ray Sponsible,en,101,art,100,art-entertainment,CAN-2021,http://localhost:8081/read/1/can-cameroon-vs-argentina-1-0,This is an historic day..,2020-08-04
                    2,Trump de retour,2,Roger Milla,fr,202,business,200,industry,Trump|Politics,http://localhost:8081/read/2/trump-de-retour,Trump re-elu president une fois de plus!,2020-09-04
                """.trimIndent().toByteArray(),
            ),
            contentType = "text/csv",
        )

        // WHEN
        val docs = loader.load()

        // THEN
        assertEquals(2, docs.size)

        assertEquals(1L, docs[0].id)
        assertEquals("en", docs[0].language)
        assertEquals(
            """
                CAN: Cameroon vs. Argentina: 1-0.
                art
                art-entertainment
                CAN-2021
                This is an historic day..
            """.trimIndent(),
            docs[0].content.trimIndent(),
        )

        assertEquals(2L, docs[1].id)
        assertEquals("fr", docs[1].language)
        assertEquals(
            """
                Trump de retour
                business
                industry
                Trump,Politics
                Trump re-elu president une fois de plus!
            """.trimIndent(),
            docs[1].content.trimIndent(),
        )
    }
}
