package com.wutsi.ml.similarity.model.author

import com.wutsi.ml.embedding.model.author.AuthorTfidfEmbeddingModel
import com.wutsi.ml.similarity.model.AbstractSimilarityModel
import org.springframework.stereotype.Service

@Service
class AuthorTfidfSimilarityModel(
    private val embedding: AuthorTfidfEmbeddingModel,
) : AbstractSimilarityModel() {
    override fun getEmbeddingModel() = embedding
}
