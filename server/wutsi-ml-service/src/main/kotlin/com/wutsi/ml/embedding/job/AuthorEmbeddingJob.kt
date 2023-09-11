package com.wutsi.ml.embedding.job

import com.wutsi.ml.document.domain.DocumentEntity
import com.wutsi.ml.document.service.DocumentLoader
import com.wutsi.ml.embedding.service.AuthorConfig
import com.wutsi.platform.core.cron.CronJobRegistry
import com.wutsi.platform.core.cron.CronLockManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * This job is executed by the Heroku scheduler as one-off
 */
@Service
class AuthorEmbeddingJob(
    private val documentLoader: DocumentLoader,

    lockManager: CronLockManager,
    registry: CronJobRegistry,
) : AbstractEmbeddingJob(lockManager, registry) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(AuthorEmbeddingJob::class.java)
    }

    override fun getLogger(): Logger = LOGGER

    override fun loadDocuments(): List<DocumentEntity> {
        val documentsByAuthor = documentLoader.load().groupBy { it.authorId }
        return documentsByAuthor.keys.map { authorId ->
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

    override fun getEmbeddingPath(): String =
        AuthorConfig.EMBEDDING_PATH

    override fun getNNIndexPath(): String =
        AuthorConfig.NN_INDEX_PATH

    override fun getJobName() = "embedding-author"
}
