package com.wutsi.ml.similarity.model

import com.wutsi.ml.similarity.dto.SearchSimilarityRequest
import com.wutsi.ml.similarity.dto.SearchSimilarityResponse

interface SimilarityModel {
    fun search(request: SearchSimilarityRequest): SearchSimilarityResponse
}
