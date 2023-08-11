package com.wutsi.ml.matrix

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
    private val cells = mutableMapOf<Int, MutableMap<Int, Double>>()

    val size: Int
        get() = this.m * this.n

    companion object {
        fun random(m: Int, n: Int): Matrix {
            val result = Matrix(m, n)
            for (i in 0 until m) {
                for (j in 0 until n) {
                    result.set(i, j, Math.random())
                }
            }
            return result
        }

        fun identify(n: Int): Matrix {
            val result = Matrix(n, n)
            for (i in 0 until n) {
                result.set(i, i, 1.0)
            }
            return result
        }

        fun of(m: Int, n: Int, value: Double = 0.0): Matrix {
            val result = Matrix(m, n)
            for (i in 0 until m) {
                for (j in 0 until n) {
                    result.set(i, j, value)
                }
            }
            return result
        }

        fun from(value: Array<Array<Double>>): Matrix {
            val result = Matrix(value.size, value[0].size)
            for (i in 0 until result.m) {
                for (j in 0 until result.n) {
                    result.set(i, j, value[i][j])
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
                        result.set(k, j++, cell.toDoubleOrNull())
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
                                matrix!!.set(k, j++, cell.toDoubleOrNull())
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

    fun get(i: Int, j: Int): Double {
        checkBoundary(i, j)

        return cells[i]?.get(j) ?: 0.0
    }

    fun set(i: Int, j: Int, value: Double?) {
        checkBoundary(i, j)

        if (value == null || value == 0.0) {
            cells[i]?.remove(j)
        } else {
            var row = cells[i]
            if (row == null) {
                row = mutableMapOf()
                cells[i] = row
            }
            row[j] = value
        }
    }

    private fun checkBoundary(i: Int, j: Int) {
        if (i < 0 || i >= m || j < 0 || j >= n) {
            throw IllegalStateException("Invalid boundary i,j=$i,$j vs mxn=${m}x$n")
        }
    }

    fun print() {
        println(toString())
    }

    fun infos(prefix: String = "") {
        println("$prefix ${m}x$n - size=$size - sparsity=${sparsity()}")
    }

    override fun toString(): String {
        val buff = StringBuilder()
        for (i in 0 until m) {
            for (j in 0 until n) {
                buff.append(get(i, j))
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
                result.set(i, j, f(i, j, get(i, j)))
            }
        }
        return result
    }

    fun forEach(f: (i: Int, j: Int, v: Double) -> Unit) {
        for (i in 0 until m) {
            for (j in 0 until n) {
                f(i, j, get(i, j))
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

        return apply { i, j, v -> v + rhs.get(i, j) }
    }

    private fun adjustNM(matrix: Matrix): Matrix {
        var adjusted: Matrix = matrix
        if (adjusted.n == 1 && n > 1) {
            adjusted = adjusted.repeat(n, Axis.N)
        }
        if (adjusted.m == 1 && m > 1) {
            adjusted = adjusted.repeat(m, Axis.M)
        }
        return adjusted
    }

    operator fun minus(value: Double): Matrix =
        apply { _, _, v -> v - value }

    operator fun minus(value: Matrix): Matrix {
        val rhs = adjustNM(value)
        if (rhs.m != m || rhs.n != n) {
            throw java.lang.RuntimeException("Matrix dimension mismatch. ${m}x$n vs ${rhs.m}x${rhs.n}")
        }

        return apply { i, j, v -> v - rhs.get(i, j) }
    }

    operator fun times(value: Double): Matrix =
        apply { _, _, v -> v * value }

    operator fun times(value: Matrix): Matrix {
        val rhs = adjustNM(value)
        if (rhs.m != m || rhs.n != n) {
            throw java.lang.RuntimeException("Matrix dimension mismatch. ${m}x$n vs ${rhs.m}x${rhs.n}")
        }

        return apply { i, j, v -> v * rhs.get(i, j) }
    }

    operator fun div(value: Matrix): Matrix {
        val rhs = adjustNM(value)
        if (rhs.m != m || rhs.n != n) {
            throw java.lang.RuntimeException("Matrix dimension mismatch. ${m}x$n vs ${rhs.m}x${rhs.n}")
        }

        return apply { i, j, v -> v / rhs.get(i, j) }
    }

    operator fun div(value: Double): Matrix =
        apply { _, _, v -> v / value }

    fun tanh(): Matrix =
        apply { _, _, v -> kotlin.math.tanh(v) }

    fun pow(n: Double): Matrix =
        apply { _, _, v -> v.pow(n) }

    fun sqrt(): Matrix =
        apply { _, _, v -> kotlin.math.sqrt(v) }

    fun dot(value: Matrix): Matrix {
        if (n != value.m) {
            throw RuntimeException("Matrix dimension mismatch. ${m}x$n vs ${value.m}x${value.n}")
        }

        val result = Matrix(m, value.n)
        for (i in 0 until result.m) {
            for (j in 0 until result.n) {
                var tmp = 0.0
                for (k in 0 until n) {
                    tmp += get(i, k) * value.get(k, j)
                }
                result.set(i, j, tmp)
            }
        }
        return result
    }

    fun transpose(): Matrix {
        val result = Matrix(n, m)
        for (i in 0 until m) {
            for (j in 0 until n) {
                result.set(j, i, get(i, j))
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
        return apply { i, j, v -> max(v, value.get(i, j)) }
    }

    fun min(value: Double): Matrix =
        min(of(m, n, value))

    fun min(value: Matrix): Matrix {
        if (value.m != m || value.n != n) {
            throw RuntimeException("Matrix dimension mismatch. ${m}x$n vs ${value.m}x${value.n}")
        }
        return apply { i, j, v -> min(v, value.get(i, j)) }
    }

    fun sum(): Double {
        var result = 0.0
        for (i in 0 until m) {
            for (j in 0 until n) {
                result += get(i, j)
            }
        }
        return result
    }

    fun sum(axis: Axis): Matrix {
        if (axis == Axis.M) {
            val result = Matrix(1, n)
            for (j in 0 until n) {
                for (k in 0 until m) {
                    result.set(0, j, result.get(0, j) + get(k, j))
                }
            }
            return result
        } else {
            val result = Matrix(m, 1)
            for (i in 0 until m) {
                for (j in 0 until n) {
                    result.set(i, 0, result.get(i, 0) + get(i, j))
                }
            }
            return result
        }
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
                result.set(0, k++, get(i, j))
            }
        }
        return result
    }

    fun norm(): Double =
        pow(2.0).sum()

    fun repeat(count: Int, axis: Axis): Matrix {
        if (axis == Axis.N) {
            val result = Matrix(m, n * count)
            for (i in 0 until m) {
                for (j in 0 until n) {
                    var x = 0
                    for (k in 0 until count) {
                        result.set(i, x++, get(i, j))
                    }
                }
            }
            return result
        } else {
            val result = Matrix(m * count, n)
            for (i in 0 until m) {
                for (j in 0 until n) {
                    var x = 0
                    for (k in 0 until count) {
                        result.set(x++, j, get(i, j))
                    }
                }
            }
            return result
        }
    }

    fun sub(m1: Int = 0, m2: Int = m - 1, n1: Int = 0, n2: Int = n - 1): Matrix {
        val result = Matrix(m2 - m1 + 1, n2 - n1 + 1)
        for (i in 0 until result.m) {
            for (j in 0 until result.n) {
                result.set(i, j, get(i + m1, j + n1))
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
                    writer.write(get(i, j).toString())
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
                if (get(i, j) != value.get(i, j)) {
                    return false
                }
            }
        }
        return true
    }

    fun argmax(axis: Axis): Matrix {
        if (axis == Axis.M) {
            val result = Matrix(1, n)
            for (j in 0 until n) {
                var k = -1
                var max = Double.MIN_VALUE

                for (i in 0 until m) {
                    val value = get(i, j)
                    if (value > max) {
                        max = value
                        k = i
                    }
                }
                result.set(0, j, k.toDouble())
            }
            return result
        } else {
            val result = Matrix(m, 1)

            for (i in 0 until m) {
                var k = -1
                var max = Double.MIN_VALUE
                for (j in 0 until n) {
                    val value = get(i, j)
                    if (value > max) {
                        max = value
                        k = j
                    }
                }
                result.set(i, 0, k.toDouble())
            }
            return result
        }
    }

    fun mean(): Double =
        sum() / size

    fun cosineSimilarity(withHeader: Boolean = false): Matrix {
        val result = Matrix(if (withHeader) n + 1 else n, n)
        result.forEach { i, j, _ ->
            if (i == 0 && withHeader) {
                result.set(i, j, get(i, j))
            } else {
                result.set(i, j, cosineSimilarity(i, j, withHeader))
            }
        }
        return result
    }

    fun sparsity(): Double {
        var count = 0.0
        forEach { _, _, v ->
            if (v > 0.0) {
                count++
            }
        }
        return 100.0 * count / size
    }

    fun concatenate(matrix: Matrix, axis: Axis): Matrix {
        if (axis == Axis.N) {
            if (m != matrix.m) {
                throw IllegalStateException("M don't match ${m}x$n vs ${matrix.m}x${matrix.n}")
            }
            val result = of(m, n + matrix.n)
            result.forEach { i, j, _ ->
                if (j < n) {
                    result.set(i, j, get(i, j))
                } else {
                    result.set(i, j, matrix.get(i, j - n))
                }
            }
            return result
        } else {
            if (n != matrix.n) {
                throw IllegalStateException("N don't match ${m}x$n vs ${matrix.m}x${matrix.n}")
            }
            val result = of(m + matrix.m, n)
            result.forEach { i, j, _ ->
                if (i < m) {
                    result.set(i, j, get(i, j))
                } else {
                    result.set(i, j, matrix.get(i - m, j))
                }
            }
            return result
        }
    }

    fun toList(): List<Double> {
        val result = mutableListOf<Double>()
        forEach { _, _, v -> result.add(v) }
        return result
    }

    private fun cosineSimilarity(i: Int, j: Int, withHeader: Boolean): Double {
        var i2 = if (withHeader) i - 1 else i
        if (i2 == j) {
            return 1.0
        }

        var num = 0.0
        var dA = 0.0
        var dB = 0.0
        var range = if (withHeader) {
            1 until m
        } else {
            0 until m
        }

        for (k in range) {
            val a = get(k, i2)
            val b = get(k, j)
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
