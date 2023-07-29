package com.wutsi.recommendation.matrix

import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.OutputStreamWriter
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.streams.toList

class Matrix(val m: Int, val n: Int) {
    val cell = Array(m) { Array(n) { 0.0 } }
    val size: Int
        get() = this.m * this.n

    companion object {
        fun random(m: Int, n: Int): Matrix {
            val result = Matrix(m, n)
            for (i in 0 until m) {
                for (j in 0 until n) {
                    result.cell[i][j] = Math.random()
                }
            }
            return result
        }

        fun identify(n: Int): Matrix {
            val result = Matrix(n, n)
            for (i in 0 until n) {
                result.cell[i][i] = 1.0
            }
            return result
        }

        fun of(m: Int, n: Int, value: Double = 0.0): Matrix {
            val result = Matrix(m, n)
            for (i in 0 until m) {
                for (j in 0 until n) {
                    result.cell[i][j] = value
                }
            }
            return result
        }

        fun from(value: Array<Array<Double>>): Matrix {
            val result = Matrix(value.size, value[0].size)
            for (i in 0 until result.m) {
                for (j in 0 until result.n) {
                    result.cell[i][j] = value[i][j]
                }
            }
            return result
        }

        fun from(input: InputStream, ignoreFirstRow: Boolean = false): Matrix {
            val lines = input.bufferedReader().lines().toList()
            val m = lines.size
            val n = lines[0].split(",").size
            val result = if (ignoreFirstRow) {
                Matrix(m - 1, n)
            } else {
                Matrix(m, n)
            }

            var k = 0
            for (i in 0 until m) {
                if (i == 0 && ignoreFirstRow) {
                    continue
                }

                var j = 0
                lines[i].split(",")
                    .forEach { cell ->
                        result.cell[k][j++] = if (cell.isNullOrEmpty()) {
                            0.0
                        } else {
                            cell.trim().toDouble()
                        }
                    }
                k++
            }
            return result
        }

        fun from(file: File, ignoreFirstRow: Boolean = false): Matrix {
            var matrix: Matrix? = null
            val lc = countLines(file)
            val m = if (ignoreFirstRow) lc - 1 else lc

            val fin = FileInputStream(file)
            fin.use {
                val reader = BufferedReader(InputStreamReader(fin))
                var row = 0
                var k = 0
                reader.use {
                    while (true) {
                        val line = reader.readLine() ?: break
                        val cells = line.split(",")

                        // Create the matrix
                        if (matrix == null) {
                            val n = cells.size
                            matrix = of(m, n)
                        }

                        // Ignore the first row
                        if (row == 0 && ignoreFirstRow) {
                            // Skip
                        } else {
                            // fill
                            var j = 0
                            cells.forEach { cell ->
                                matrix!!.cell[k][j++] = if (cell.isNullOrEmpty()) {
                                    0.0
                                } else {
                                    cell.trim().toDouble()
                                }
                            }
                            k++
                        }

                        // Next
                        row++
                    }
                }
            }
            return matrix ?: Matrix(1, 1)
        }

        private fun countLines(file: File): Int {
            BufferedInputStream(FileInputStream(file)).use { `is` ->
                val c = ByteArray(1024)
                var count = 0
                var readChars = 0
                var endsWithoutNewLine = false
                var len = 0
                while (`is`.read(c).also { readChars = it } != -1) {
                    for (i in 0 until readChars) {
                        len++
                        if (c[i] == '\n'.code.toByte()) ++count
                    }
                    endsWithoutNewLine = c[readChars - 1] != '\n'.code.toByte()
                }
                if (endsWithoutNewLine) {
                    ++count
                }
                if (len == 0) { // Last line is empty
                    --count
                }
                return count
            }
        }
    }

    fun print() {
        println(toString())
    }

    fun printDimension(prefix: String = "") {
        println("$prefix ${m}x$n")
    }

    override fun toString(): String {
        val buff = StringBuilder()
        for (i in 0 until m) {
            for (j in 0 until n) {
                buff.append(cell[i][j])
                if (j < n - 1) {
                    buff.append(" ")
                }
            }
            if (i < m - 1) {
                buff.append("\n")
            }
        }
        return buff.toString()
    }

    fun apply(f: (i: Int, j: Int, v: Double) -> Double): Matrix {
        val result = Matrix(m, n)
        for (i in 0 until m) {
            for (j in 0 until n) {
                result.cell[i][j] = f(i, j, cell[i][j])
            }
        }
        return result
    }

    fun forEach(f: (i: Int, j: Int, v: Double) -> Unit) {
        for (i in 0 until m) {
            for (j in 0 until n) {
                f(i, j, cell[i][j])
            }
        }
    }

    operator fun plus(value: Double): Matrix =
        apply { _, _, v -> v + value }

    operator fun plus(value: Matrix): Matrix {
        val rhs = adjustNM(value)
        if (rhs.m != m || rhs.n != n) {
            throw java.lang.RuntimeException("Matrix dimension mismatch. ${m}x$n vs ${rhs.m}x${rhs.n}")
        }

        return apply { i, j, v -> v + rhs.cell[i][j] }
    }

    private fun adjustNM(matrix: Matrix): Matrix {
        var adjusted: Matrix = matrix
        if (adjusted.n == 1 && n > 1) {
            adjusted = adjusted.repeatN(n)
        }
        if (adjusted.m == 1 && m > 1) {
            adjusted = adjusted.repeatM(m)
        }
        return adjusted
    }

    operator fun minus(value: Double): Matrix =
        apply { i, j, v -> v - value }

    operator fun minus(value: Matrix): Matrix {
        val rhs = adjustNM(value)
        if (rhs.m != m || rhs.n != n) {
            throw java.lang.RuntimeException("Matrix dimension mismatch. ${m}x$n vs ${rhs.m}x${rhs.n}")
        }

        return apply { i, j, v -> v - rhs.cell[i][j] }
    }

    operator fun times(value: Double): Matrix =
        apply { i, j, v -> v * value }

    operator fun times(value: Matrix): Matrix {
        val rhs = adjustNM(value)
        if (rhs.m != m || rhs.n != n) {
            throw java.lang.RuntimeException("Matrix dimension mismatch. ${m}x$n vs ${rhs.m}x${rhs.n}")
        }

        return apply { i, j, v -> v * rhs.cell[i][j] }
    }

    operator fun div(value: Matrix): Matrix {
        val rhs = adjustNM(value)
        if (rhs.m != m || rhs.n != n) {
            throw java.lang.RuntimeException("Matrix dimension mismatch. ${m}x$n vs ${rhs.m}x${rhs.n}")
        }

        return apply { i, j, v -> v / rhs.cell[i][j] }
    }

    operator fun div(value: Double): Matrix =
        apply { i, j, v -> v / value }

    fun tanh(): Matrix =
        apply { _, _, v -> Math.tanh(v) }

    fun pow(n: Double): Matrix =
        apply { _, _, v -> Math.pow(v, n) }

    fun dot(value: Matrix): Matrix {
        if (n != value.m) {
            throw RuntimeException("Matrix dimension mismatch. ${m}x$n vs ${value.m}x${value.n}")
        }

        val result = Matrix(m, value.n)
        for (i in 0 until result.m) {
            for (j in 0 until result.n) {
                var tmp = 0.0
                for (k in 0 until n) {
                    tmp += cell[i][k] * value.cell[k][j]
                }
                result.cell[i][j] = tmp
            }
        }
        return result
    }

    fun transpose(): Matrix {
        val result = Matrix(n, m)
        for (i in 0 until m) {
            for (j in 0 until n) {
                result.cell[j][i] = cell[i][j]
            }
        }
        return result
    }

    fun max(value: Double): Matrix =
        max(of(m, n, value))

    fun max(value: Matrix): Matrix {
        if (value.m != m || value.n != n) {
            throw RuntimeException("Matrix dimension mismatch")
        }
        return apply { i, j, v -> max(v, value.cell[i][j]) }
    }

    fun min(value: Double): Matrix =
        min(of(m, n, value))

    fun min(value: Matrix): Matrix {
        if (value.m != m || value.n != n) {
            throw RuntimeException("Matrix dimension mismatch. ${m}x$n vs ${value.m}x${value.n}")
        }
        return apply { i, j, v -> min(v, value.cell[i][j]) }
    }

    fun sum(): Double {
        var result = 0.0
        for (i in 0 until m) {
            for (j in 0 until n) {
                result += cell[i][j]
            }
        }
        return result
    }

    fun sumN(): Matrix {
        val result = Matrix(1, n)
        for (j in 0 until n) {
            for (k in 0 until m) {
                result.cell[0][j] += cell[k][j]
            }
        }
        return result
    }

    fun sumM(): Matrix {
        val result = Matrix(m, 1)
        for (i in 0 until m) {
            for (j in 0 until n) {
                result.cell[i][0] += cell[i][j]
            }
        }
        return result
    }

    fun exp(): Matrix =
        apply { _, _, v -> kotlin.math.exp(v) }

    fun log(base: Double = 10.0): Matrix =
        apply { _, _, v -> kotlin.math.log(v, base) }

    fun flatten(): Matrix {
        var k = 0
        val result = Matrix(1, n * m)
        for (i in 0 until m) {
            for (j in 0 until n) {
                result.cell[0][k++] = cell[i][j]
            }
        }
        return result
    }

    fun repeatN(count: Int): Matrix {
        val result = Matrix(m, n * count)
        for (i in 0 until m) {
            for (j in 0 until n) {
                var x = 0
                for (k in 0 until count) {
                    result.cell[i][x++] = cell[i][j]
                }
            }
        }
        return result
    }

    fun repeatM(count: Int): Matrix {
        val result = Matrix(m * count, n)
        for (i in 0 until m) {
            for (j in 0 until n) {
                var x = 0
                for (k in 0 until count) {
                    result.cell[x++][j] = cell[i][j]
                }
            }
        }
        return result
    }

    fun sub(m1: Int = 0, m2: Int = m - 1, n1: Int = 0, n2: Int = n - 1): Matrix {
        val result = Matrix(m2 - m1 + 1, n2 - n1 + 1)
        for (i in 0 until result.m) {
            for (j in 0 until result.n) {
                result.cell[i][j] = cell[i + m1][j + n1]
            }
        }
        return result
    }

    fun gt(value: Double): Matrix =
        apply { _, _, v -> if (v > value) 1.0 else 0.0 }

    fun save(out: OutputStream) {
        val writer = OutputStreamWriter(out)
        writer.use {
            for (i in 0 until m) {
                for (j in 0 until n) {
                    writer.write(cell[i][j].toString())
                    if (j == n - 1) {
                        if (i < m - 1) {
                            writer.write("\n")
                        }
                    } else {
                        writer.write(",")
                    }
                }
            }
        }
    }

    fun equals(value: Matrix): Boolean {
        if (m != value.m || n != value.n) {
            return false
        }

        for (i in 0 until value.m) {
            for (j in 0 until value.n) {
                if (cell[i][j] != value.cell[i][j]) {
                    return false
                }
            }
        }
        return true
    }

    fun argmaxN(): Matrix {
        val result = Matrix(1, n)
        for (j in 0 until n) {
            var k = -1
            var max = Double.MIN_VALUE

            for (i in 0 until m) {
                if (cell[i][j] > max) {
                    max = cell[i][j]
                    k = i
                }
            }
            result.cell[0][j] = k.toDouble()
        }
        return result
    }

    fun argmaxM(): Matrix {
        val result = Matrix(m, 1)

        for (i in 0 until m) {
            var k = -1
            var max = Double.MIN_VALUE
            for (j in 0 until n) {
                if (cell[i][j] > max) {
                    max = cell[i][j]
                    k = j
                }
            }
            result.cell[i][0] = k.toDouble()
        }
        return result
    }

    fun mean(): Double =
        sum() / size

    fun cosineSimilarity(): Matrix {
        val result = Matrix(n, n)
        result.forEach { i, j, _ -> result.cell[i][j] = cosineSimilarity(i, j) }
        return result
    }

    private fun cosineSimilarity(i: Int, j: Int): Double {
        if (i == j) {
            return 1.0
        }

        var num = 0.0
        var dA = 0.0
        var dB = 0.0
        for (k in 0 until m) {
            val a = cell[k][i]
            val b = cell[k][j]
            num += a * b
            dA += a.pow(2.0)
            dB += b.pow(2.0)
        }
        return if (dA == 0.0 || dB == 0.0) {
            Double.MAX_VALUE
        } else {
            num / (sqrt(dA) * sqrt(dB))
        }
    }
}
