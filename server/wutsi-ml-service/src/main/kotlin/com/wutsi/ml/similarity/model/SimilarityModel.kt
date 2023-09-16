package com.wutsi.ml.embedding.service

import com.wutsi.ml.similarity.dto.SearchSimilarityRequest
import com.wutsi.ml.similarity.dto.SearchSimilarityResponse

interface SimilarityModel {
    fun search(request: SearchSimilarityRequest): SearchSimilarityResponse
}
