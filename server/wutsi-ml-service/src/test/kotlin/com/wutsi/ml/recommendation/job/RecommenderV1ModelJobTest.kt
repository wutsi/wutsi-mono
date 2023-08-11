package com.wutsi.ml.recommendation.job

import com.wutsi.ml.recommendation.service.RecommenderV1Model
import com.wutsi.platform.core.storage.StorageService
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.io.ByteArrayInputStream

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class RecommenderV1ModelJobTest {
    @Autowired
    private lateinit var job: RecommenderV1ModelJob

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
                """.trimIndent().toByteArray(),
            ),
            contentType = "text/csv",
        )

        storage.store(
            path = "feeds/readers.csv",
            content = ByteArrayInputStream(
                """
                    story_id,user_id,commented,liked,subscribed
                    100,1,1,,
                    100,2,,,
                    100,3,,,1
                    200,1,,,
                    200,2,,1,
                """.trimIndent().toByteArray(),
            ),
            contentType = "text/csv",
        )

        storage.store(
            path = "feeds/users.csv",
            content = ByteArrayInputStream(
                """
                    user_id
                    1
                    2
                    3
                    4
                    5
                    6
                    7
                    8
                    9
                """.trimIndent().toByteArray(),
            ),
            contentType = "text/csv",
        )

        // WHEN
        job.run()

        // THEN
        assertTrue(storage.contains(storage.toURL(RecommenderV1Model.U_PATH)))
        assertTrue(storage.contains(storage.toURL(RecommenderV1Model.V_PATH)))
    }
}
