package com.wutsi.ml.recommendation

import com.wutsi.ml.matrix.Matrix
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.io.InputStream

/**
 * See http://www.quuxlabs.com/blog/2010/09/matrix-factorization-a-simple-tutorial-and-implementation-in-python/
 */
class MatrixFactorization {
    companion object {
        const val SCORE_READ = 1
        const val SCORE_LIKE = 2
    }

    private var users: List<Long> = emptyList()
    private var stories: List<Long> = emptyList()
    private var data = mutableMapOf<String, Matrix>()

    fun train(features: Int, iterations: Int, learningRate: Double) {
        init()

        val y = data["y"]!!

        val N = y.gt(0.0).sum().toInt()
        println("N=$N")
        println("parsity=" + 100.0 * N.toDouble() / y.size + "%")

        var u = Matrix.random(y.m, features)
        var v = Matrix.random(features, y.n)
        for (iteration in 0 until iterations) {
            val predicted = predict(y, u, v)
            val error = y - predicted

            val cost = cost(error, N)
            println("$iteration cost=$cost")

            val du = derivU(error, v, learningRate, N)
            val dv = derivV(error, u, learningRate, N)
            u += du
            v += dv
        }
        data["u"] = u
        data["v"] = v
    }

    private fun derivU(error: Matrix, v: Matrix, alpha: Double, N: Int): Double {
        var result = 0.0
        error.forEach { i, j, value ->
            for (k in 0 until v.m) {
                result += value * v.get(k, j)
            }
        }
        return result * (2.0 * alpha) / N
    }

    private fun derivV(error: Matrix, u: Matrix, alpha: Double, N: Int): Double {
        var result = 0.0
        error.forEach { i, j, value ->
            for (k in 0 until u.n) {
                result += value * u.get(i, k)
            }
        }
        return result * (2.0 * alpha) / N
    }

    private fun predict(y: Matrix, u: Matrix, v: Matrix): Matrix =
        u.dot(v).apply { i, j, v ->
            if (y.get(i, j) == 0.0) {
                0.0
            } else {
                v
            }
        }

    private fun cost(error: Matrix, N: Int): Double =
        error.pow(2.0).sum() / N

    private fun buildY(m: Int, n: Int): Matrix {
        val matrixR = Matrix.of(m, n)
        loadReads(matrixR, MatrixFactorization::class.java.getResourceAsStream("/recommendation/readers.csv"))
        loadLikes(matrixR, MatrixFactorization::class.java.getResourceAsStream("/recommendation/likes.csv"))
        return matrixR
    }

    private fun loadReads(matrix: Matrix, input: InputStream) {
        var count = 0
        val parser = CSVParser.parse(
            input,
            Charsets.UTF_8,
            CSVFormat.Builder.create()
                .setDelimiter(",")
                .setHeader("story_id", "user_id")
                .setSkipHeaderRecord(true)
                .build(),
        )
        parser.records.forEach { record ->
            val storyId = record.get("story_id").toLong()
            val userId = record.get("user_id").toLong()
            set(matrix, userId, storyId, SCORE_READ)
            count++
        }
        println("$count reads")
    }

    private fun loadLikes(matrix: Matrix, input: InputStream) {
        var count = 0
        val parser = CSVParser.parse(
            input,
            Charsets.UTF_8,
            CSVFormat.Builder.create()
                .setDelimiter(",")
                .setHeader("story_id", "user_id", "device_id", "like_date")
                .setSkipHeaderRecord(true)
                .build(),
        )
        parser.records.forEach { record ->
            val storyId = record.get("story_id").toLong()
            val userId = record.get("user_id").toLongOrNull()
            if (userId != null) {
                set(matrix, userId, storyId, SCORE_LIKE)
                count++
            }
        }
        println("$count likes")
    }

    private fun set(matrix: Matrix, userId: Long, storyId: Long, value: Int) {
        val m = users.indexOf(userId)
        val n = stories.indexOf(storyId)
        if (m >= 0 && n >= 0) {
            matrix.set(m, n, value.toDouble())
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

    private fun init() {
        users = loadIds(MatrixFactorization::class.java.getResourceAsStream("/recommendation/users.csv"))
        println("users: ${users.size}")

        stories = loadIds(MatrixFactorization::class.java.getResourceAsStream("/recommendation/stories.csv"))
        println("stories: ${stories.size}")

        val y = buildY(users.size, stories.size)
        data["y"] = y
        println("Y: ${y.m}x${y.n} - size=${y.size}")
    }
}
// fun main(args: Array<String>) {
//    val mf = MatrixFactorization()
//
//    mf.train(features = 3, iterations = 1000, learningRate = 0.01)
// }
