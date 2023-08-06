package com.wutsi.ml.embedding.service

import com.wutsi.blog.similarity.dto.SearchSimilarityRequest
import com.wutsi.ml.matrix.Matrix
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.storage.StorageService
import org.slf4j.LoggerFactory
import org.springframework.cache.Cache
import org.springframework.stereotype.Service
import java.io.FileOutputStream
import java.nio.file.Files

@Service
class TfIdfSimilarityService(
    private val storage: StorageService,
    private val logger: KVLogger,
    private val cache: Cache,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(TfIdfSimilarityService::class.java)
    }

    private var nn: Matrix? = null
    private var ids = emptyList<Long>()

    fun search(request: SearchSimilarityRequest): List<Pair<Long, Double>> {
        logger.add("request_ids", request.ids)
        logger.add("request_similar_count", request.similarIds.size)
        logger.add("request_similar_ids", request.similarIds.take(10) + "...")
        logger.add("request_limit", request.limit)

        // Initialize
        if (nn == null) {
            init()
        }

        // Get column
        val pairs = mutableListOf<Pair<Long, Double>>()
        request.ids.forEach {
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
            .filter { !request.ids.contains(it.first) } // Remove the request ids
            .filter { request.similarIds.isEmpty() || request.similarIds.contains(it.first) } // Filter the similar ID provided
            .filter { resultIds.add(it.first) } // Make sure that the ID are unique
            .take(request.limit).toList()

        logger.add("result", result.take(10) + "...")
        return result
    }

    fun swapMatrix(matrix: Matrix) {
        val newIds = mutableListOf<Long>()
        matrix.sub(m1 = 0, m2 = 0).forEach { _, _, v -> newIds.add(v.toLong()) }

        // Update matrixes
        nn = matrix
        ids = newIds

        // Invalidate the cache
        try {
            cache.invalidate()
        } catch (ex: Exception) {
            LOGGER.warn("Unable to invalidate the cache", ex)
        }

        logger.add("matrix_swapped", true)
        logger.add("matrix_m", matrix.m)
        logger.add("matrix_n", matrix.n)
    }

    fun init() {
        LOGGER.info("Loading NNIndex")

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
