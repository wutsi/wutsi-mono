package com.wutsi.ml.embedding.model.author

import com.wutsi.ml.document.domain.DocumentEntity
import com.wutsi.ml.document.service.DocumentLoader
import com.wutsi.ml.embedding.model.AbstractTfidfEmbeddingModel
import org.springframework.stereotype.Service

@Service
class AuthorTfidfEmbeddingModel(
    private val documentLoader: DocumentLoader,
) : AbstractTfidfEmbeddingModel() {
    companion object {
        val EMBEDDING_PATH = "ml/embedding/author/embedding.csv"
        val NN_INDEX_PATH = "ml/embedding/author/nnindex.csv"
    }

    override fun getEmbeddingPath() = EMBEDDING_PATH

    override fun getNNIndexPath() = NN_INDEX_PATH

    override fun loadDocuments(): List<DocumentEntity> {
        val documentsByAuthor = documentLoader.load()
            .groupBy { it.authorId }

        return documentsByAuthor.keys
            .map { authorId ->
                val docs = documentsByAuthor[authorId]!!
                if (docs.size == 1) {
                    docs[0].copy(id = authorId)
                } else {
                    documentsByAuthor[authorId]!!.reduce { acc, cur ->
                        DocumentEntity(
                            id = authorId,
                            authorId = authorId,
                            language = cur.language,
                            content = "${acc.content}\n${cur.content}",
                        )
                    }
                }
            }
    }
}
