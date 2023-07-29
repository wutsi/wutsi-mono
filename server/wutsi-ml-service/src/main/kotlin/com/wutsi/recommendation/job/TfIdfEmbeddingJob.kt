package com.wutsi.recommendation.job

import com.wutsi.platform.core.cron.AbstractCronJob
import com.wutsi.platform.core.cron.CronLockManager
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.storage.StorageService
import com.wutsi.recommendation.service.DocumentLoader
import com.wutsi.recommendation.service.TfIdfEmbeddingGenerator
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

@Service
class TfIdfEmbeddingJob(
    lockManager: CronLockManager,
    private val documentLoader: DocumentLoader,
    private val embeddingGenerator: TfIdfEmbeddingGenerator,
    private val storage: StorageService,
    private val logger: KVLogger,
) : AbstractCronJob(lockManager) {
    override fun getJobName() = "embedding-tfidf"

    @Scheduled(cron = "\${wutsi.crontab.embedding-tfidf}")
    override fun run() {
        super.run()
    }

    override fun doRun(): Long {
        // Load document
        val documents = documentLoader.load()
        logger.add("document_count", documents.size)

        // Generate embedding
        val file = File.createTempFile("embedding", ".csv")
        try {
            // Generate embedding
            val fout = FileOutputStream(file)
            fout.use {
                embeddingGenerator.generate(documents, fout)
            }

            // Store embedding to ml/tfidf/embedding.csv
            val fin = FileInputStream(file)
            fin.use {
                val url = storage.store("ml/tfidf/embedding.csv", fin, "text/csv")
                logger.add("embedding_url", url)
            }

            return documents.size.toLong()
        } finally {
            file.delete()
        }
    }
}
