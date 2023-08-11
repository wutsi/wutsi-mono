package com.wutsi.ml.event

import com.nhaarman.mockitokotlin2.verify
import com.wutsi.ml.embedding.service.TfIdfSimilarityService
import com.wutsi.ml.recommendation.service.RecommenderM1ModelService
import com.wutsi.platform.core.stream.Event
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class EventHandlerTest {
    @Autowired
    private lateinit var handler: EventHandler

    @MockBean
    private lateinit var recommenderV1: RecommenderM1ModelService

    @MockBean
    private lateinit var tfIdf: TfIdfSimilarityService

    @Test
    fun recommenderV1() {
        handler.handleEvent(createEvent(EventType.RECOMMENDER_V1_MODEL_TRAINED))

        verify(recommenderV1).init()
    }

    @Test
    fun tfIds() {
        handler.handleEvent(createEvent(EventType.TFIDF_EMBEDDING_GENERATED))

        verify(tfIdf).init()
    }

    private fun createEvent(type: String) = Event(
        type = type,
        payload = "{}",
    )
}
