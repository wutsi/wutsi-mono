package com.wutsi.recommendation.embedding.job

import com.wutsi.platform.core.cron.AbstractCronJob
import com.wutsi.platform.core.cron.CronLockManager
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.storage.StorageService
import com.wutsi.recommendation.document.domain.DocumentEntity
import com.wutsi.recommendation.document.service.DocumentLoader
import com.wutsi.recommendation.embedding.service.TfIdfEmbeddingGenerator
import com.wutsi.recommendation.matrix.Matrix
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.URL

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
        val url = generateEmbedding(documents, file)
        logger.add("embedding_url", url)

        // Generate NN index
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
            val matrix = Matrix.from(fin, true)
            val nn = matrix.cosineSimilarity()

            // Save matrix locally
            val out = File.createTempFile("nn", ".csv")
            val fout = FileOutputStream(out)
            fout.use {
                nn.save(fout)
            }

            // Store to cloud
            return storeToCloud(out, "ml/tfidf/nnindex.csv")
        }
    }

    private fun storeToCloud(file: File, path: String): URL {
        val fin = FileInputStream(file)
        return fin.use {
            storage.store(path, fin, "text/csv")
        }
    }
}
