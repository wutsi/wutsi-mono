package com.wutsi.ml.embedding.job

import com.wutsi.ml.document.domain.DocumentEntity
import com.wutsi.ml.document.service.DocumentLoader
import com.wutsi.ml.embedding.service.TfIdfConfig
import com.wutsi.platform.core.cron.CronJobRegistry
import com.wutsi.platform.core.cron.CronLockManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * This job is executed by the Heroku scheduler as one-off
 */
@Service
class TfIdfEmbeddingJob(
    private val documentLoader: DocumentLoader,
    lockManager: CronLockManager,
    registry: CronJobRegistry,
) : AbstractEmbeddingJob(lockManager, registry) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(TfIdfEmbeddingJob::class.java)
    }

    override fun getLogger(): Logger = LOGGER

    override fun loadDocuments(): List<DocumentEntity> =
        documentLoader.load()

    override fun getEmbeddingPath(): String =
        TfIdfConfig.EMBEDDING_PATH

    override fun getNNIndexPath(): String =
        TfIdfConfig.NN_INDEX_PATH

    override fun getJobName() = "embedding-tfidf"
}
