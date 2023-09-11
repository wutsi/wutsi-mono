package com.wutsi.ml.embedding.job

import com.wutsi.ml.embedding.service.AuthorConfig
import com.wutsi.platform.core.storage.StorageService
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.io.ByteArrayInputStream

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class AuthorEmbeddingJobTest {
    @Autowired
    private lateinit var job: AuthorEmbeddingJob

    @Autowired
    private lateinit var storage: StorageService

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
                    201,Obama en campagne,2,Roger Milla,fr,202,business,200,industry,Trump|Obama|Politics,http://localhost:8081/read/3/obama-en-campagne,Obama est de retour en campagne.
                """.trimIndent().toByteArray(),
            ),
            contentType = "text/csv",
        )

        // WHEN
        job.run()

        // THEN
        assertTrue(storage.contains(storage.toURL(AuthorConfig.EMBEDDING_PATH)))
        assertTrue(storage.contains(storage.toURL(AuthorConfig.NN_INDEX_PATH)))
    }
}
