package com.wutsi.ml.embedding.model.story

import com.wutsi.ml.document.domain.DocumentEntity
import com.wutsi.ml.document.service.DocumentLoader
import com.wutsi.ml.embedding.model.AbstractTfidfEmbeddingModel
import org.springframework.stereotype.Service

@Service
class StoryTfidfEmbeddingModel(
    private val documentLoader: DocumentLoader,
) : AbstractTfidfEmbeddingModel() {
    companion object {
        val EMBEDDING_PATH = "ml/embedding/story/embedding.csv"
        val NN_INDEX_PATH = "ml/embedding/story/nnindex.csv"
    }

    override fun getEmbeddingPath() = EMBEDDING_PATH

    override fun getNNIndexPath() = NN_INDEX_PATH

    override fun loadDocuments(): List<DocumentEntity> =
        documentLoader.load()
}
