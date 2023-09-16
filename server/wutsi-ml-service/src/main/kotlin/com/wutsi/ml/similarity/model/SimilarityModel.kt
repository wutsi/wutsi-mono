package com.wutsi.ml.similarity.model

import com.wutsi.ml.embedding.model.EmbeddingModel
import com.wutsi.ml.similarity.dto.SearchSimilarityRequest
import com.wutsi.ml.similarity.dto.SearchSimilarityResponse

interface SimilarityModel {
    fun getEmbeddingModel(): EmbeddingModel

    fun search(request: SearchSimilarityRequest): SearchSimilarityResponse

    fun reload()
}
