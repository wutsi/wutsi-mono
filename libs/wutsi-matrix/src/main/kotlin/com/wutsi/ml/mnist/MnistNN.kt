package com.wutsi.matrix

import java.io.InputStream
import java.io.OutputStream
import java.io.OutputStreamWriter
import kotlin.streams.toList

class Matrix(val m: Int, val n: Int) {
    val cell = Array(m) { Array(n) { 0.0 } }

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

        fun create(m: Int, n: Int, value: Double): Matrix {
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

        fun from(input: InputStream): Matrix {
            val lines = input.bufferedReader().lines().toList()
            val m = lines.size
            val n = lines[0].split(",").size
            val result = Matrix(m, n)

            for (i in 0 until m) {
                var j = 0
                lines[i].split(",")
                    .forEach { cell ->
                        result.cell[i][j++] = if (cell.isNullOrEmpty()) {
                            0.0
                        } else {
                            cell.trim().toDouble()
                        }
                    }
            }
            return result
        }
    }

    fun print() {
        for (i in 0 until m) {
            for (j in 0 until n) {
                print(cell[i][j])
                print(" ")
            }
            println()
        }
    }

    fun plus(value: Double): Matrix {
        val result = Matrix(n, n)
        for (i in 0 until m) {
            for (j in 0 until n) {
                result.cell[i][j] = cell[i][j] + value
            }
        }
        return result
    }

    fun plus(value: Matrix): Matrix {
        if (value.m != m || value.n != n) {
            throw java.lang.RuntimeException("Matrix dimension mismatch")
        }

        val result = Matrix(n, n)
        for (i in 0 until m) {
            for (j in 0 until n) {
                result.cell[i][j] = cell[i][j] + value.cell[i][j]
            }
        }
        return result
    }

    fun minus(value: Double): Matrix {
        val result = Matrix(n, n)
        for (i in 0 until m) {
            for (j in 0 until n) {
                result.cell[i][j] = cell[i][j] - value
            }
        }
        return result
    }

    fun minus(value: Matrix): Matrix {
        if (value.m != m || value.n != n) {
            throw RuntimeException("Matrix dimension mismatch")
        }

        val result = Matrix(n, n)
        for (i in 0 until m) {
            for (j in 0 until n) {
                result.cell[i][j] = cell[i][j] - value.cell[i][j]
            }
        }
        return result
    }

    fun dot(value: Double): Matrix {
        val result = Matrix(n, n)
        for (i in 0 until m) {
            for (j in 0 until n) {
                result.cell[i][j] = cell[i][j] * value
            }
        }
        return result
    }

    fun dot(value: Matrix): Matrix {
        if (n != value.m) {
            throw RuntimeException("Matrix dimension mismatch")
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
        max(create(m, n, value))

    fun max(value: Matrix): Matrix {
        if (value.m != m || value.n != n) {
            throw RuntimeException("Matrix dimension mismatch")
        }
        val result = Matrix(m, n)
        for (i in 0 until m) {
            for (j in 0 until n) {
                result.cell[i][j] = Math.max(cell[i][j], value.cell[i][j])
            }
        }
        return result
    }

    fun min(value: Double): Matrix =
        min(create(m, n, value))

    fun min(value: Matrix): Matrix {
        if (value.m != m || value.n != n) {
            throw RuntimeException("Matrix dimension mismatch")
        }
        val result = Matrix(m, n)
        for (i in 0 until m) {
            for (j in 0 until n) {
                result.cell[i][j] = Math.min(cell[i][j], value.cell[i][j])
            }
        }
        return result
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

    fun exp(): Matrix {
        val result = Matrix(m, n)
        for (i in 0 until m) {
            for (j in 0 until n) {
                result.cell[i][j] = Math.exp(cell[i][j])
            }
        }
        return result
    }

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

    fun div(by: Double): Matrix {
        val result = Matrix(m, n)
        for (i in 0 until m) {
            for (j in 0 until n) {
                result.cell[i][j] = cell[i][j] / by
            }
        }
        return result
    }

    fun sub(m1: Int = 0, m2: Int = -1, n1: Int = 0, n2: Int = -1): Matrix {
        val xm1 = m1
        val xn1 = n1
        val xm2 = if (m2 < 0) m - 1 else m2
        val xn2 = if (n2 <= 0) n - 1 else n2

        val result = Matrix(xm2 - xm1 + 1, xn2 - xn1 + 1)
        for (i in 0 until result.m) {
            for (j in 0 until result.n) {
                result.cell[i][j] = cell[i + xm1][j + xn1]
            }
        }
        return result
    }

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
}
