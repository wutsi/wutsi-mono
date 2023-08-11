package com.wutsi.ml.recommendation.service

import com.wutsi.ml.matrix.Axis
import com.wutsi.ml.matrix.Matrix
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.slf4j.LoggerFactory
import java.io.InputStream
import kotlin.math.pow

/**
 * Content Based Filtering training based on matrix factorization
 *
 * See https://everdark.github.io/k9/notebooks/ml/matrix_factorization/matrix_factorization.nb.html#22_binary_matrix_factorization
 */
class RecommenderV1ModelTrainer {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(RecommenderV1ModelTrainer::class.java)
        const val MIN_LOSS = 0.1
        const val SCORE_READ = 1
        const val SCORE_COMMENTED = 2
        const val SCORE_LIKED = 3
        const val SCORE_SUBSCRIBED = 4
    }

    private var userIds: List<Long> = emptyList()
    private var storyIds: List<Long> = emptyList()
    private var data = mutableMapOf<String, Matrix>()

    fun u(): Matrix = data["u"] ?: Matrix.of(1, 1)

    fun v(): Matrix = data["v"] ?: Matrix.of(1, 1)

    fun init(userIn: InputStream, storiesIn: InputStream, readIn: InputStream) {
        LOGGER.info(">>> Loading users")
        userIds = loadIds(userIn)

        LOGGER.info(">>> Loading stories")
        storyIds = loadIds(storiesIn)

        LOGGER.info(">>> Loading reads")
        val y = Matrix.of(userIds.size, storyIds.size)
        loadReads(y, readIn)
        data["y"] = y
    }

    fun train(features: Int, iterations: Int, lr: Double, l2: Double): Double {
        val y = data["y"]!!
        val u = Matrix.random(y.m, features)
        val v = Matrix.random(features, y.n)
        
        y.infos(">>>   Y:")
        u.infos(">>>   U:")
        v.infos(">>>   V:")

        var loss = Double.MAX_VALUE
        for (iteration in 0..iterations) {
            for (i in 0 until y.m) {
                for (j in 0 until y.n) {
                    if (y.get(i, j) == 0.0) {
                        continue
                    }

                    for (k in 0 until features) {
                        val err = y.get(i, j) - u.dot(v, i, j)
                        val du = lr * (2 * err * v.get(k, j) - l2 / 2 * u.get(i, k))
                        val dv = lr * (2 * err * u.get(i, k) - l2 / 2 * v.get(k, j))
                        u.set(i, k, u.get(i, k) + du)
                        v.set(k, j, v.get(k, j) + dv)
                    }
                }
            }

            loss = computeLoss(y, u, v, lr)
            if (iteration % 50 == 0 || loss == Double.NaN || loss <= MIN_LOSS) {
                LOGGER.info("$iteration - loss=$loss")
                if (loss == Double.NaN || loss <= MIN_LOSS) {
                    break
                }
            }
        }

        data["u"] = addUserIdColumn(u)
        data["v"] = addStoryIdRow(v)
        return loss
    }

    private fun addUserIdColumn(u: Matrix): Matrix {
        val u0 = Matrix.of(u.m, 1)
        u0.forEach { i, _, _ -> userIds[i].toDouble() }
        return u0.concatenate(u, Axis.N)
    }

    private fun addStoryIdRow(v: Matrix): Matrix {
        val v0 = Matrix.of(1, v.n)
        v0.forEach { _, j, _ -> storyIds[j].toDouble() }
        return v0.concatenate(v, Axis.M)
    }

    private fun computeLoss(y: Matrix, u: Matrix, v: Matrix, lr: Double): Double {
        var err = 0.0
        y.forEach { i, j, value ->
            if (y.get(i, j) != 0.0) {
                err += (value - u.dot(v, i, j)).pow(2.0)
            }
        }
        return err + lr * (u.norm() + v.norm())
    }

    private fun set(matrix: Matrix, userId: Long, storyId: Long, flag: Boolean, value: Int) {
        val m = userIds.indexOf(userId)
        val n = storyIds.indexOf(storyId)
        if (m >= 0 && n >= 0 && flag) {
            matrix.set(m, n, value.toDouble())
        }
    }

    private fun loadReads(matrix: Matrix, input: InputStream) {
        var count = 0
        val parser = CSVParser.parse(
            input,
            Charsets.UTF_8,
            CSVFormat.Builder.create()
                .setDelimiter(",")
                .setHeader("story_id", "user_id", "commented", "liked", "subscribed")
                .setSkipHeaderRecord(true)
                .build(),
        )
        parser.records.forEach { record ->
            val storyId = record.get("story_id").toLong()
            val userId = record.get("user_id").toLong()
            set(matrix, userId, storyId, true, SCORE_READ)
            set(matrix, userId, storyId, record.get("commented") == "1", SCORE_COMMENTED)
            set(matrix, userId, storyId, record.get("liked") == "1", SCORE_LIKED)
            set(matrix, userId, storyId, record.get("subscribed") == "1", SCORE_SUBSCRIBED)

            count++
        }
    }

    private fun loadIds(input: InputStream): List<Long> {
        val parser = CSVParser.parse(
            input,
            Charsets.UTF_8,
            CSVFormat.Builder.create()
                .setDelimiter(",")
                .build(),
        )

        val result = mutableListOf<Long?>()
        var row = 0
        parser.records.forEach { record ->
            if (row > 0) {
                result.add(record.get(0).toLongOrNull())
            }
            row++
        }
        return result.filterNotNull()
    }
}
