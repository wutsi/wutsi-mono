package com.wutsi.ml.event

import com.wutsi.ml.embedding.service.TfIdfSimilarityService
import com.wutsi.ml.recommendation.service.RecommenderM1ModelService
import com.wutsi.platform.core.stream.Event
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@Service
class EventHandler(
    private val recommenderV1: RecommenderM1ModelService,
    private val tfIdf: TfIdfSimilarityService,
) {
    @EventListener
    fun handleEvent(event: Event) {
        when (event.type) {
            EventType.RECOMMENDER_V1_MODEL_TRAINED -> recommenderV1.init()
            EventType.TFIDF_EMBEDDING_GENERATED -> tfIdf.init()
            else -> {}
        }
    }
}
