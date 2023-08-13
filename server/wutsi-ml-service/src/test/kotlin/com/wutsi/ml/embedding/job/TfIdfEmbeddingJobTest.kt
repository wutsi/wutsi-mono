package com.wutsi.ml.embedding.job

import com.wutsi.ml.embedding.service.TfIdfConfig
import com.wutsi.platform.core.storage.StorageService
import com.wutsi.platform.core.stream.EventStream
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import java.io.ByteArrayInputStream
import kotlin.test.Ignore

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class TfIdfEmbeddingJobTest {
    @Autowired
    private lateinit var job: TfIdfEmbeddingJob

    @Autowired
    private lateinit var storage: StorageService

    @MockBean
    private lateinit var eventStream: EventStream

    @Test
    fun run() {
        // GIVEN
        storage.store(
            path = "feeds/stories.csv",
            content = ByteArrayInputStream(
                """
                    id,title,author_id,author,language,topic_id,topic,parent_topic_id,parent_topic,tags,url,summary
                    100,CAN: Cameroon vs. Argentina: 1-0.,1,Ray Sponsible,en,101,art,100,art-entertainment,CAN-2021,http://localhost:8081/read/1/can-cameroon-vs-argentina-1-0,This is an historic day..
                    200,Trump de retour,2,Roger Milla,fr,202,business,200,industry,Trump|Politics,http://localhost:8081/read/2/trump-de-retour,Trump re-elu president une fois de plus!
                """.trimIndent().toByteArray(),
            ),
            contentType = "text/csv",
        )

        // WHEN
        job.run()

        // THEN
        assertTrue(storage.contains(storage.toURL(TfIdfConfig.EMBEDDING_PATH)))
        assertTrue(storage.contains(storage.toURL(TfIdfConfig.NN_INDEX_PATH)))
    }

    @Test
    @Ignore
    fun runWithLargeFile() {
        // GIVEN
        storage.store(
            path = "feeds/stories.csv",
            content = TfIdfEmbeddingJobTest::class.java.getResourceAsStream("/stories.csv"),
            contentType = "text/csv",
        )

        // WHEN
        job.run()

        // THEN
        assertTrue(storage.contains(storage.toURL(TfIdfConfig.EMBEDDING_PATH)))
        assertTrue(storage.contains(storage.toURL(TfIdfConfig.NN_INDEX_PATH)))
    }
}
