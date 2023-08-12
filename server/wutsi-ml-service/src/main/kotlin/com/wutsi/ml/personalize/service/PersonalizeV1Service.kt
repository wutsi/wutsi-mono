package com.wutsi.ml.personalize.service

import com.wutsi.ml.matrix.Matrix
import com.wutsi.ml.personalize.dto.RecommendStoryRequest
import com.wutsi.ml.personalize.dto.SortStoryRequest
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.storage.StorageService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.FileOutputStream
import java.nio.file.Files
import java.util.UUID

@Service
class PersonalizeV1Service(
    private val storage: StorageService,
    private val logger: KVLogger,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(PersonalizeV1Service::class.java)
    }

    private var u: Matrix? = null
    private var v: Matrix? = null
    private var userIds: List<Long> = emptyList()
    private var storyIds: List<Long> = emptyList()

    fun recommend(request: RecommendStoryRequest): List<Pair<Long, Double>> {
        logger.add("request_user_id", request.userId)
        logger.add("request_limit", request.limit)

        if (u == null || v == null) {
            init()
        }

        val i = userIds.indexOf(request.userId)
        if (i < 0) {
            return emptyList()
        }

        val predictions = storyIds.indices.map { j ->
            Pair(
                first = storyIds[j],
                second = u!!.dot(v!!, i, j),
            )
        }.sortedByDescending { it.second }
            .take(request.limit)

        logger.add("result_top10", predictions.take(10))
        return predictions
    }

    fun sort(request: SortStoryRequest): List<Pair<Long, Double>> {
        logger.add("request_story_ids", request.storyIds)
        logger.add("request_user_id", request.userId)

        if (u == null || v == null) {
            init()
        }

        val i = userIds.indexOf(request.userId)
        if (i < 0) {
            return request.storyIds.map { storyId ->
                Pair(
                    first = storyId,
                    second = Double.MIN_VALUE,
                )
            }
        }

        val predictions = request.storyIds.map { storyId ->
            val j = storyIds.indexOf(storyId)
            if (j >= 0) {
                Pair(
                    first = storyId,
                    second = u!!.dot(v!!, i, j),
                )
            } else {
                Pair(
                    first = storyId,
                    second = Double.MIN_VALUE,
                )
            }
        }.sortedByDescending { it.second }

        logger.add("result_top10", predictions.take(10))
        return predictions
    }

    @Synchronized
    fun init() {
        LOGGER.info("Initializing")

        val u0 = loadMatrix(PersonalizeV1.U_PATH)
        val v0 = loadMatrix(PersonalizeV1.V_PATH)

        u = u0.sub(n1 = 1)
        userIds = u0.sub(n1 = 0, n2 = 0).toList().map { it.toLong() }

        v = v0.sub(m1 = 1)
        storyIds = v0.sub(m1 = 0, m2 = 0).toList().map { it.toLong() }
    }

    private fun loadMatrix(path: String): Matrix {
        val file = Files.createTempFile(UUID.randomUUID().toString(), ".csv").toFile()
        val fout = FileOutputStream(file)
        fout.use {
            val url = storage.toURL(path)
            storage.get(url, fout)
        }

        return Matrix.from(file, false)
    }
}
