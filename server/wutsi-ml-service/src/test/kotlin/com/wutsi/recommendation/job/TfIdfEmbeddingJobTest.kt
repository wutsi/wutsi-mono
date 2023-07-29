package com.wutsi.recommendation.job

import com.wutsi.platform.core.storage.StorageService
import com.wutsi.recommendation.embedding.job.TfIdfEmbeddingJob
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.io.ByteArrayInputStream
import kotlin.test.Ignore

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class TfIdfEmbeddingJobTest {
    @Autowired
    private lateinit var job: TfIdfEmbeddingJob

    @Autowired
    private lateinit var storage: StorageService

    @Test
    fun load() {
        // GIVEN
        storage.store(
            path = "feeds/stories.csv",
            content = ByteArrayInputStream(
                """
                    id,title,author_id,author,language,topic_id,topic,parent_topic_id,parent_topic,tags,url,summary
                    1,CAN: Cameroon vs. Argentina: 1-0.,1,Ray Sponsible,en,101,art,100,art-entertainment,CAN-2021,http://localhost:8081/read/1/can-cameroon-vs-argentina-1-0,This is an historic day..
                    2,Trump de retour,2,Roger Milla,fr,202,business,200,industry,Trump|Politics,http://localhost:8081/read/2/trump-de-retour,Trump re-elu president une fois de plus!
                """.trimIndent().toByteArray(),
            ),
            contentType = "text/csv",
        )

        // WHEN
        job.run()

        // THEN
        assertTrue(storage.contains(storage.toURL("ml/tfidf/embedding.csv")))
        assertTrue(storage.contains(storage.toURL("ml/tfidf/nnindex.csv")))
    }

    @Test
    @Ignore
    fun loadLargeFile() {
        // GIVEN
        storage.store(
            path = "feeds/stories.csv",
            content = TfIdfEmbeddingJobTest::class.java.getResourceAsStream("/stories.csv"),
            contentType = "text/csv",
        )

        // WHEN
        job.run()

        // THEN
        assertTrue(storage.contains(storage.toURL("ml/tfidf/embedding.csv")))
        assertTrue(storage.contains(storage.toURL("ml/tfidf/nnindex.csv")))
    }
}
