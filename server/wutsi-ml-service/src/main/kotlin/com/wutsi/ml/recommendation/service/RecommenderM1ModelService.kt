package com.wutsi.ml.recommendation.service

import com.wutsi.ml.matrix.Matrix
import com.wutsi.platform.core.storage.StorageService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.FileOutputStream
import java.nio.file.Files
import java.util.UUID

@Service
class RecommenderM1ModelService(
    private val storage: StorageService,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(RecommenderM1ModelService::class.java)
    }

    private var u: Matrix? = null
    private var v: Matrix? = null
    private var userIds: List<Long> = emptyList()
    private var storyIds: List<Long> = emptyList()

    fun init() {
        LOGGER.info("Initializing")

        val u0 = loadMatrix(RecommenderV1Model.U_PATH)
        u = u0.sub(n1 = 1)
        userIds = u0.sub(n1 = 0, n2 = 0).toList().map { it.toLong() }

        val v0 = loadMatrix(RecommenderV1Model.V_PATH)
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
