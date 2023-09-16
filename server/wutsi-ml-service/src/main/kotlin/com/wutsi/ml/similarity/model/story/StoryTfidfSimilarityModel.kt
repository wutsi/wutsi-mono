package com.wutsi.ml.similarity.model.story

import com.wutsi.ml.embedding.model.story.StoryTfidfEmbeddingModel
import com.wutsi.ml.similarity.model.AbstractSimilarityModel
import org.springframework.stereotype.Service

@Service
class StoryTfidfSimilarityModel(
    private val embedding: StoryTfidfEmbeddingModel,
) : AbstractSimilarityModel() {
    override fun getEmbeddingModel() = embedding
}
