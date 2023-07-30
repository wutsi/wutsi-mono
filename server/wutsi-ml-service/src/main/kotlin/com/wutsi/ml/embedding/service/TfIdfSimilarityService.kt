package com.wutsi.ml.embedding.service

import com.wutsi.blog.ml.dto.SearchSimilarityRequest
import com.wutsi.ml.matrix.Matrix
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.storage.StorageService
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

    fun search(request: SearchSimilarityRequest): List<Pair<Long, Double>> {
        logger.add("request_id", request.id)
        logger.add("request_limit", request.limit)

        // Initialize
        if (nn == null) {
            init()
        }

        // Get column
        val idIndex = ids.indexOf(request.id)
        logger.add("id_index", idIndex)
        if (idIndex < 0) {
            return emptyList()
        }

        // Return results
        val result = mutableListOf<Pair<Long, Double>>()
        ids.indices.forEach { i ->
            result.add(
                Pair(
                    ids[i],
                    nn!!.get(i + 1, idIndex),
                ),
            )
        }

        return result.sortedByDescending { it.second }
            .filter { it.first != request.id } // Remove requested id
            .take(request.limit)
    }

    fun swapMatrix(matrix: Matrix) {
        val newIds = mutableListOf<Long>()
        matrix.sub(m1 = 0, m2 = 0).forEach { _, _, v -> newIds.add(v.toLong()) }

        nn = matrix
        ids = newIds

        logger.add("matrix_swapped", true)
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
