package com.wutsi.ml.similarity.model

import com.wutsi.ml.matrix.Matrix
import com.wutsi.ml.similarity.dto.Item
import com.wutsi.ml.similarity.dto.SearchSimilarityRequest
import com.wutsi.ml.similarity.dto.SearchSimilarityResponse
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.storage.StorageService
import org.springframework.beans.factory.annotation.Autowired
import java.io.FileOutputStream
import java.nio.file.Files

abstract class AbstractSimilarityModel : SimilarityModel {
    @Autowired
    private lateinit var storage: StorageService

    @Autowired
    private lateinit var logger: KVLogger

    private var nn: Matrix? = null
    private var ids = emptyList<Long>()

    override fun search(request: SearchSimilarityRequest): SearchSimilarityResponse {
        logger.add("request_item_ids", request.itemIds)
        logger.add("request_model", request.model)
        logger.add("request_limit", request.limit)

        // Initialize
        if (nn == null) {
            init()
        }

        // Get column
        val pairs = mutableListOf<Pair<Long, Double>>()
        request.itemIds.forEach {
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
            .filter { !request.itemIds.contains(it.first) } // Remove the request ids
            .filter { resultIds.add(it.first) } // Make sure that the ID are unique
            .take(request.limit).toList()

        logger.add("result", result.take(10) + "...")
        return SearchSimilarityResponse(
            items = result.map {
                Item(id = it.first, score = it.second)
            },
        )
    }

    @Synchronized
    override fun reload() {
        init()
    }

    private fun init() {
        logger.add("initializing", true)

        // Downloading
        val file = Files.createTempFile("nnindex", ".csv").toFile()
        val fout = FileOutputStream(file)
        fout.use {
            val path = getEmbeddingModel().getNNIndexPath()
            val url = storage.toURL(path)
            storage.get(url, fout)
        }

        // Init index
        val matrix = Matrix.from(file)
        swapMatrix(matrix)
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
