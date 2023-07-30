package com.wutsi.recommendation.embedding.service

import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.storage.StorageService
import com.wutsi.recommendation.matrix.Matrix
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.FileOutputStream
import java.nio.file.Files

@Service
class TfIdfSimilarityService(
    private val storage: StorageService,
    private val logger: KVLogger,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(TfIdfSimilarityService::class.java)
    }

    private var nn: Matrix? = null
    private var ids = emptyList<Long>()

    fun findSimilar(id: Long, limit: Int = 1000): List<Pair<Long, Double>> {
        logger.add("request_id", id)
        logger.add("request_limit", limit)

        // Initialize
        if (nn == null) {
            init()
        }

        // Get column
        val n = ids.indexOf(id)
        logger.add("n", n)
        if (n < 0) {
            return emptyList()
        }

        // Return results
        val result = mutableListOf<Pair<Long, Double>>()
        ids.indices.forEach { i ->
            result.add(
                Pair(
                    ids[i],
                    nn!!.get(i, n)
                )
            )
        }

        return result.sortedByDescending { it.second }
            .filter { it.first != id } // Remove requested id
            .take(limit)
    }

    fun swapMatrix(matrix: Matrix) {
        val newIds = mutableListOf<Long>()
        matrix.sub(m1 = 0, m2 = 0).forEach { _, _, v -> newIds.add(v.toLong()) }

        nn = matrix
        ids = newIds

        logger.add("matrix_m", matrix.m)
        logger.add("matrix_n", matrix.n)
    }

    @Synchronized
    private fun init() {
        LOGGER.info("Initializing NNIndex")

        // Downloading
        val file = Files.createTempFile("nnindex", ".csv").toFile()
        val fout = FileOutputStream(file)
        fout.use {
            val path = TfIdfConfig.NN_INDEX_PATH
            LOGGER.info(">>> Downloading NNIndex from $path to $file")
            val url = storage.toURL(path)
            storage.get(url, fout)
        }

        // Init index
        LOGGER.info(">>> Loading in NNIndex from $file")
        val matrix = Matrix.from(file)
        swapMatrix(matrix)
        LOGGER.info("Loaded")
    }
}
