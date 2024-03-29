package com.wutsi.ml.embedding.model

import com.wutsi.ml.document.domain.DocumentEntity
import com.wutsi.ml.embedding.service.TfIdfEmbeddingGenerator
import com.wutsi.ml.matrix.Matrix
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.storage.StorageService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.URL

abstract class AbstractTfidfEmbeddingModel : EmbeddingModel {
    @Autowired
    private lateinit var embeddingGenerator: TfIdfEmbeddingGenerator

    @Autowired
    private lateinit var storage: StorageService

    @Autowired
    private lateinit var logger: KVLogger

    protected fun getLogger(): Logger =
        LoggerFactory.getLogger(javaClass)

    abstract fun loadDocuments(): List<DocumentEntity>

    override fun build(): Long {
        // Load document
        getLogger().info(">>> Loading documents")
        val documents = loadDocuments()
        logger.add("document_count", documents.size)

        // Generate embedding
        getLogger().info(">>> Generating embedding")
        val file = File.createTempFile("embedding", ".csv")
        val url = generateEmbedding(documents, file)
        logger.add("embedding_url", url)

        // Generate NN index
        getLogger().info(">>> Generating NN-Index")
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
        return storeToCloud(file, getEmbeddingPath())
    }

    private fun generateNNIndex(file: File): URL {
        // Generate matrix
        getLogger().info(">>>   Loading embedding")
        val matrix = Matrix.from(file)

        getLogger().info(">>>   Generating NN Index - ${matrix.n + 1}x${matrix.n}")
        val nn = matrix.cosineSimilarity(true)

        // Save matrix locally
        val out = File.createTempFile("nnindex", ".csv")
        val fout = FileOutputStream(out)
        fout.use {
            nn.save(fout)
        }

        // Store to cloud
        return storeToCloud(out, getNNIndexPath())
    }

    private fun storeToCloud(file: File, path: String): URL {
        getLogger().info(">>>   Storing $path")
        val fin = FileInputStream(file)
        return fin.use {
            storage.store(path, fin, "text/csv", contentLength = file.length())
        }
    }
}
