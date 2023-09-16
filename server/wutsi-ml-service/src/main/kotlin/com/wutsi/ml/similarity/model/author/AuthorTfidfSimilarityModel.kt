package com.wutsi.ml.similarity.model.author

import com.wutsi.ml.embedding.model.author.AuthorTfidfEmbeddingModel
import com.wutsi.ml.similarity.model.AbstractSimilarityModel
import org.springframework.stereotype.Service

@Service
class AuthorTfidfSimilarityV1Model(
    private val embedding: AuthorTfidfEmbeddingModel
) : AbstractSimilarityModel() {
    override fun getNNIndexPath() = embedding.getNNIndexPath()
}
