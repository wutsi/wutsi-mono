package com.wutsi.recommendation.embedding.job

import com.wutsi.platform.core.cron.AbstractCronJob
import com.wutsi.platform.core.cron.CronJobRegistry
import com.wutsi.platform.core.cron.CronLockManager
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.storage.StorageService
import com.wutsi.recommendation.document.domain.DocumentEntity
import com.wutsi.recommendation.document.service.DocumentLoader
import com.wutsi.recommendation.embedding.service.TfIdfEmbeddingGenerator
import com.wutsi.recommendation.matrix.Matrix
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.URL

@Service
class TfIdfEmbeddingJob(
    private val documentLoader: DocumentLoader,
    private val embeddingGenerator: TfIdfEmbeddingGenerator,
    private val storage: StorageService,
    private val logger: KVLogger,

    lockManager: CronLockManager,
    registry: CronJobRegistry,
) : AbstractCronJob(lockManager, registry) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(TfIdfEmbeddingJob::class.java)
    }

    override fun getJobName() = "embedding-tfidf"

    @Scheduled(cron = "\${wutsi.crontab.embedding-tfidf}")
    override fun run() {
        LOGGER.info("Generating embedding")

        super.run()

        LOGGER.info("Done")
    }

    override fun doRun(): Long {
        // Load document
        LOGGER.info(">>> Loading documents")
        val documents = documentLoader.load()
        logger.add("document_count", documents.size)

        // Generate embedding
        LOGGER.info(">>> Generating embedding")
        val file = File.createTempFile("embedding", ".csv")
        val url = generateEmbedding(documents, file)
        logger.add("embedding_url", url)

        // Generate NN index
        LOGGER.info(">>> Generating NN-Index")
        val nnUrl = generateNNIndex(file)
        logger.add("nnindex_url", nnUrl)

        return documents.size.toLong()
    }

    private fun generateEmbedding(documents: List<DocumentEntity>, file: File): URL {
        // Generate embedding in local file
        val fout = FileOutputStream(file)
        fout.use {
            embeddingGenerator.generate(documents, fout)
        }

        // Store into the cloud
        return storeToCloud(file, "ml/tfidf/embedding.csv")
    }

    private fun generateNNIndex(file: File): URL {
        val fin = FileInputStream(file)
        fin.use {
            // Generate matrix
            LOGGER.info(">>>   Loading embedding")
            val matrix = Matrix.from(fin, true)

            LOGGER.info(">>>   Generating NN Index - ${matrix.n}x${matrix.n}")
            val nn = matrix.cosineSimilarity()

            // Save matrix locally
            val out = File.createTempFile("nnindex", ".csv")
            val fout = FileOutputStream(out)
            fout.use {
                nn.save(fout)
            }

            // Store to cloud
            return storeToCloud(out, "ml/tfidf/nnindex.csv")
        }
    }

    private fun storeToCloud(file: File, path: String): URL {
        LOGGER.info(">>>   Storing $path")
        val fin = FileInputStream(file)
        return fin.use {
            storage.store(path, fin, "text/csv")
        }
    }
}
