package com.wutsi.ml.embedding.service

import com.wutsi.ml.embedding.dto.SearchSimilarStoryRequest
import com.wutsi.ml.matrix.Matrix
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.storage.StorageService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.FileOutputStream
import java.nio.file.Files

@Deprecated("")
@Service
class TfIdfEmbeddingService(
    private val storage: StorageService,
    private val logger: KVLogger,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(TfIdfEmbeddingService::class.java)
    }

    private var nn: Matrix? = null
    private var ids = emptyList<Long>()

    fun search(request: SearchSimilarStoryRequest): List<Pair<Long, Double>> {
        logger.add("request_story_ids", request.storyIds)
        logger.add("request_limit", request.limit)

        // Initialize
        if (nn == null) {
            init()
        }

        // Get column
        val pairs = mutableListOf<Pair<Long, Double>>()
        request.storyIds.forEach {
            val index = ids.indexOf(it)
            if (index >= 0) {
                ids.indices.forEach { i ->
                    pairs.add(
                        Pair(
                            ids[i],
                            nn!!.get(i + 1, index),
                        ),
                    )
                }
            }
        }

        logger.add("pairs_size", pairs.size)

        // Sort
        val resultIds = mutableSetOf<Long>()
        val result = pairs.asSequence().sortedByDescending { it.second }
            .filter { !request.storyIds.contains(it.first) } // Remove the request ids
            .filter { resultIds.add(it.first) } // Make sure that the ID are unique
            .take(request.limit).toList()

        logger.add("result", result.take(10) + "...")
        return result
    }

    @Synchronized
    fun init() {
        LOGGER.info("Initializing")

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

    private fun swapMatrix(matrix: Matrix) {
        val newIds = mutableListOf<Long>()
        matrix.sub(m1 = 0, m2 = 0).forEach { _, _, v -> newIds.add(v.toLong()) }

        // Update matrices
        nn = matrix
        ids = newIds

        logger.add("matrix_swapped", true)
        logger.add("matrix_m", matrix.m)
        logger.add("matrix_n", matrix.n)
    }
}
