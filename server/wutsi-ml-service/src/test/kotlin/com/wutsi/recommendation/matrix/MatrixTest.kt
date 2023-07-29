package com.wutsi.recommendation.matrix

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import kotlin.test.assertFalse

internal class MatrixTest {
    @Test
    fun random() {
        val result = Matrix.random(10, 11)
        for (i in 0 until 10) {
            for (j in 0 until 11) {
                assertTrue(result.cell[i][j] >= 0)
                assertTrue(result.cell[i][j] <= 1)
            }
        }
    }

    @Test
    fun identity() {
        val result = Matrix.identify(10)
        for (i in 0 until 10) {
            for (j in 0 until 10) {
                if (i == j) {
                    assertEquals(1.0, result.cell[i][j])
                } else {
                    assertEquals(0.0, result.cell[i][j])
                }
            }
        }
    }

    @Test
    fun plusScalar() {
        val result = Matrix(2, 2).plus(10.0)
        for (i in 0 until 2) {
            for (j in 0 until 2) {
                assertEquals(10.0, result.cell[i][j])
            }
        }
    }

    @Test
    fun plus() {
        val result = Matrix.of(2, 2, 1.0)
            .plus(
                Matrix.of(2, 2, 2.0),
            )
        for (i in 0 until 2) {
            for (j in 0 until 2) {
                assertEquals(3.0, result.cell[i][j])
            }
        }
    }

    @Test
    fun plusRadjustN() {
        val result = Matrix.from(
            arrayOf(
                arrayOf(1.0, 0.0, 1.0),
                arrayOf(2.0, 1.0, 1.0),
                arrayOf(0.0, 1.0, 1.0),
            ),
        ).plus(
            Matrix.from(
                arrayOf(
                    arrayOf(1.0),
                    arrayOf(0.5),
                    arrayOf(2.0),
                ),
            ),
        )

        assertTrue(
            result.equals(
                Matrix.from(
                    arrayOf(
                        arrayOf(2.0, 1.0, 2.0),
                        arrayOf(2.5, 1.5, 1.5),
                        arrayOf(2.0, 3.0, 3.0),
                    ),
                ),
            ),
        )
    }

    @Test
    fun minusScalar() {
        val result = Matrix(2, 2).minus(10.0)
        for (i in 0 until 2) {
            for (j in 0 until 2) {
                assertEquals(-10.0, result.cell[i][j])
            }
        }
    }

    @Test
    fun minusRadjustN() {
        val result = Matrix.from(
            arrayOf(
                arrayOf(1.0, 0.0, 1.0),
                arrayOf(2.0, 1.0, 1.0),
                arrayOf(0.0, 1.0, 1.0),
            ),
        ).minus(
            Matrix.from(
                arrayOf(
                    arrayOf(1.0),
                    arrayOf(0.5),
                    arrayOf(2.0),
                ),
            ),
        )

        assertTrue(
            result.equals(
                Matrix.from(
                    arrayOf(
                        arrayOf(0.0, -1.0, 0.0),
                        arrayOf(1.5, 0.5, 0.5),
                        arrayOf(-2.0, -1.0, -1.0),
                    ),
                ),
            ),
        )
    }

    @Test
    fun minus() {
        val result = Matrix.of(2, 2, 1.0)
            .minus(
                Matrix.of(2, 2, 2.0),
            )
        for (i in 0 until 2) {
            for (j in 0 until 2) {
                assertEquals(-1.0, result.cell[i][j])
            }
        }
    }

    @Test
    fun timesScalar() {
        val result = Matrix.of(2, 2, 2.0).times(10.0)
        for (i in 0 until 2) {
            for (j in 0 until 2) {
                assertEquals(20.0, result.cell[i][j])
            }
        }
    }

    @Test
    fun times() {
        val result = Matrix.from(
            arrayOf(
                arrayOf(1.0, 0.0, 1.0),
                arrayOf(2.0, 1.0, 1.0),
                arrayOf(0.0, 1.0, 1.0),
            ),
        ).times(
            Matrix.from(
                arrayOf(
                    arrayOf(1.0, 2.0, 1.0),
                    arrayOf(2.0, 3.0, 1.0),
                    arrayOf(4.0, 2.0, 2.0),
                ),
            ),
        )
//        result.print()
        assertTrue(
            result.equals(
                Matrix.from(
                    arrayOf(
                        arrayOf(1.0, 0.0, 1.0),
                        arrayOf(4.0, 3.0, 1.0),
                        arrayOf(0.0, 2.0, 2.0),
                    ),
                ),
            ),
        )
    }

    @Test
    fun dot() {
        val result = Matrix.from(
            arrayOf(
                arrayOf(1.0, 0.0, 1.0),
                arrayOf(2.0, 1.0, 1.0),
                arrayOf(0.0, 1.0, 1.0),
                arrayOf(1.0, 1.0, 2.0),
            ),
        ).dot(
            Matrix.from(
                arrayOf(
                    arrayOf(1.0, 2.0, 1.0),
                    arrayOf(2.0, 3.0, 1.0),
                    arrayOf(4.0, 2.0, 2.0),
                ),
            ),
        )
        assertTrue(
            result.equals(
                Matrix.from(
                    arrayOf(
                        arrayOf(5.0, 4.0, 3.0),
                        arrayOf(8.0, 9.0, 5.0),
                        arrayOf(6.0, 5.0, 3.0),
                        arrayOf(11.0, 9.0, 6.0),
                    ),
                ),
            ),
        )
    }

    @Test
    fun transpose() {
        val result = Matrix.from(
            arrayOf(
                arrayOf(1.0, 2.0),
                arrayOf(3.0, 4.0),
                arrayOf(5.0, 6.0),
            ),
        ).transpose()
        assertTrue(
            result.equals(
                Matrix.from(
                    arrayOf(
                        arrayOf(1.0, 3.0, 5.0),
                        arrayOf(2.0, 4.0, 6.0),
                    ),
                ),
            ),
        )
    }

    @Test
    fun min() {
        val result = Matrix.from(
            arrayOf(
                arrayOf(1.0, 0.0, 1.0),
                arrayOf(2.0, 1.0, 1.0),
                arrayOf(0.0, 1.0, 1.0),
            ),
        )
            .min(
                Matrix.from(
                    arrayOf(
                        arrayOf(1.0, 2.0, 1.0),
                        arrayOf(10.0, 3.0, 5.0),
                        arrayOf(4.0, 2.0, 2.0),
                    ),
                ),
            )
        assertTrue(
            result.equals(
                Matrix.from(
                    arrayOf(
                        arrayOf(1.0, 0.0, 1.0),
                        arrayOf(2.0, 1.0, 1.0),
                        arrayOf(0.0, 1.0, 1.0),
                    ),
                ),
            ),
        )
    }

    @Test
    fun minScalar() {
        val result = Matrix.from(
            arrayOf(
                arrayOf(1.0, 0.0, 1.0),
                arrayOf(2.0, 1.0, 1.0),
                arrayOf(0.0, 1.0, 1.0),
            ),
        )
            .min(0.5)

        assertTrue(
            result.equals(
                Matrix.from(
                    arrayOf(
                        arrayOf(0.5, 0.0, 0.5),
                        arrayOf(0.5, 0.5, 0.5),
                        arrayOf(0.0, 0.5, 0.5),
                    ),
                ),
            ),
        )
    }

    @Test
    fun max() {
        val result = Matrix.from(
            arrayOf(
                arrayOf(1.0, 0.0, 1.0),
                arrayOf(2.0, 1.0, 1.0),
                arrayOf(0.0, 1.0, 1.0),
            ),
        )
            .max(
                Matrix.from(
                    arrayOf(
                        arrayOf(1.0, 2.0, 1.0),
                        arrayOf(10.0, 3.0, 5.0),
                        arrayOf(4.0, 2.0, 2.0),
                    ),
                ),
            )
        assertTrue(
            result.equals(
                Matrix.from(
                    arrayOf(
                        arrayOf(1.0, 2.0, 1.0),
                        arrayOf(10.0, 3.0, 5.0),
                        arrayOf(4.0, 2.0, 2.0),
                    ),
                ),
            ),
        )
    }

    @Test
    fun maxScalar() {
        val result = Matrix.from(
            arrayOf(
                arrayOf(1.0, 0.0, 1.0),
                arrayOf(2.0, 1.0, 1.0),
                arrayOf(0.0, 1.0, 1.0),
            ),
        )
            .max(0.5)
        assertTrue(
            result.equals(
                Matrix.from(
                    arrayOf(
                        arrayOf(1.0, 0.5, 1.0),
                        arrayOf(2.0, 1.0, 1.0),
                        arrayOf(0.5, 1.0, 1.0),
                    ),
                ),
            ),
        )
    }

    @Test
    fun sum() {
        val result = Matrix.from(
            arrayOf(
                arrayOf(1.0, 0.0, 1.0),
                arrayOf(2.0, 1.0, 1.0),
                arrayOf(0.0, 1.0, 1.0),
            ),
        ).sum()
        assertEquals(8.0, result)
    }

    @Test
    fun mean() {
        val result = Matrix.from(
            arrayOf(
                arrayOf(1.0, 0.0, 1.0),
                arrayOf(2.0, 1.0, 1.0),
                arrayOf(0.0, 1.0, 1.0),
            ),
        ).mean()
        assertEquals(0.8888888888888888, result)
    }

    @Test
    fun exp() {
        val result = Matrix.from(
            arrayOf(
                arrayOf(1.0, 0.0, 1.0),
                arrayOf(2.0, 1.0, 1.0),
                arrayOf(0.0, 1.0, 1.0),
            ),
        ).exp()
        result.print()
        assertTrue(
            result.equals(
                Matrix.from(
                    arrayOf(
                        arrayOf(2.718281828459045, 1.0, 2.718281828459045),
                        arrayOf(7.38905609893065, 2.718281828459045, 2.718281828459045),
                        arrayOf(1.0, 2.718281828459045, 2.718281828459045),
                    ),
                ),
            ),
        )
    }

    @Test
    fun divScalar() {
        val result = Matrix.from(
            arrayOf(
                arrayOf(1.0, 0.0, 1.0),
                arrayOf(2.0, 1.0, 1.0),
                arrayOf(0.0, 1.0, 1.0),
            ),
        ).div(2.0)

        assertTrue(
            result.equals(
                Matrix.from(
                    arrayOf(
                        arrayOf(0.5, 0.0, 0.5),
                        arrayOf(1.0, 0.5, 0.5),
                        arrayOf(0.0, 0.5, 0.5),
                    ),
                ),
            ),
        )
    }

    @Test
    fun div() {
        val result = Matrix.from(
            arrayOf(
                arrayOf(4.0),
                arrayOf(2.0),
                arrayOf(0.0),
            ),
        ).div(
            Matrix.from(
                arrayOf(
                    arrayOf(2.0),
                    arrayOf(2.0),
                    arrayOf(2.0),
                ),
            ),
        )

        assertTrue(
            result.equals(
                Matrix.from(
                    arrayOf(
                        arrayOf(2.0),
                        arrayOf(1.0),
                        arrayOf(0.0),
                    ),
                ),
            ),
        )
    }

    @Test
    fun sub1() {
        val result = Matrix.from(
            arrayOf(
                arrayOf(1.0, 0.0, 1.0, 10.0, 5.0),
                arrayOf(2.0, 1.0, 1.0, 5.5, 1.1),
                arrayOf(0.0, 1.0, 1.0, 3.0, 1.0),
                arrayOf(1.4, 1.4, 1.4, 3.4, 1.4),
                arrayOf(0.5, 0.5, 1.5, 3.5, 1.5),
            ),
        ).sub(n1 = 0, n2 = 0)

        assertTrue(
            result.equals(
                Matrix.from(
                    arrayOf(
                        arrayOf(1.0),
                        arrayOf(2.0),
                        arrayOf(0.0),
                        arrayOf(1.4),
                        arrayOf(0.5),
                    ),
                ),
            ),
        )
    }

    @Test
    fun sub2() {
        val result = Matrix.from(
            arrayOf(
                arrayOf(1.0, 0.0, 1.0, 10.0, 5.0),
                arrayOf(2.0, 1.0, 1.0, 5.5, 1.1),
                arrayOf(0.0, 1.0, 1.0, 3.0, 1.0),
                arrayOf(1.4, 1.4, 1.4, 3.4, 1.4),
                arrayOf(0.5, 0.5, 1.5, 3.5, 1.5),
            ),
        ).sub(n1 = 1, n2 = 3)

        assertTrue(
            result.equals(
                Matrix.from(
                    arrayOf(
                        arrayOf(0.0, 1.0, 10.0),
                        arrayOf(1.0, 1.0, 5.5),
                        arrayOf(1.0, 1.0, 3.0),
                        arrayOf(1.4, 1.4, 3.4),
                        arrayOf(0.5, 1.5, 3.5),
                    ),
                ),
            ),
        )
    }

    @Test
    fun sub3() {
        val result = Matrix.from(
            arrayOf(
                arrayOf(1.0, 0.0, 1.0, 10.0, 5.0),
                arrayOf(2.0, 1.0, 1.0, 5.5, 1.1),
                arrayOf(0.0, 1.0, 1.0, 3.0, 1.0),
                arrayOf(1.4, 1.4, 1.4, 3.4, 1.4),
                arrayOf(0.5, 0.5, 1.5, 3.5, 1.5),
            ),
        ).sub(m1 = 1)

//        result.print()
        assertTrue(
            result.equals(
                Matrix.from(
                    arrayOf(
                        arrayOf(2.0, 1.0, 1.0, 5.5, 1.1),
                        arrayOf(0.0, 1.0, 1.0, 3.0, 1.0),
                        arrayOf(1.4, 1.4, 1.4, 3.4, 1.4),
                        arrayOf(0.5, 0.5, 1.5, 3.5, 1.5),
                    ),
                ),
            ),
        )
    }

    @Test
    fun write() {
        val matrix = Matrix.from(
            arrayOf(
                arrayOf(1.0, 0.0, 1.0, 10.0, 5.0),
                arrayOf(2.0, 1.0, 1.0, 5.5, 1.1),
                arrayOf(0.0, 1.0, 1.0, 3.0, 1.0),
                arrayOf(1.4, 1.4, 1.4, 3.4, 1.4),
                arrayOf(0.5, 0.5, 1.5, 3.5, 1.5),
            ),
        )

        val out = ByteArrayOutputStream()
        matrix.save(out)
        assertEquals(
            """
                1.0,0.0,1.0,10.0,5.0
                2.0,1.0,1.0,5.5,1.1
                0.0,1.0,1.0,3.0,1.0
                1.4,1.4,1.4,3.4,1.4
                0.5,0.5,1.5,3.5,1.5
            """.trimIndent(),
            out.toString(),
        )
    }

    @Test
    fun flatten() {
        val result = Matrix.from(
            arrayOf(
                arrayOf(1.0, 0.0, 1.0, 10.0, 5.0),
                arrayOf(2.0, 1.0, 1.0, 5.5, 1.1),
                arrayOf(0.0, 1.0, 1.0, 3.0, 1.0),
                arrayOf(1.4, 1.4, 1.4, 3.4, 1.4),
                arrayOf(0.5, 0.5, 1.5, 3.5, 1.5),
            ),
        ).flatten()

//        result.print()
        assertTrue(
            result.equals(
                Matrix.from(
                    arrayOf(
                        arrayOf(
                            1.0, 0.0, 1.0, 10.0, 5.0,
                            2.0, 1.0, 1.0, 5.5, 1.1,
                            0.0, 1.0, 1.0, 3.0, 1.0,
                            1.4, 1.4, 1.4, 3.4, 1.4,
                            0.5, 0.5, 1.5, 3.5, 1.5,
                        ),
                    ),
                ),
            ),
        )
    }

    @Test
    fun fromInputStream() {
        val result = Matrix.from(
            ByteArrayInputStream(
                """
                    1.0,0.0,1.0,10.0,5.0
                    2.0,1.0,1.0,5.5,1.1
                    0.0,1.0,1.0,3.0,1.0
                    1.4,1.4,1.4,3.4,1.4
                    0.5,0.5,1.5,3.5,1.5
                """.trimIndent().toByteArray(Charsets.UTF_8),
            ),
        )

        assertTrue(
            result.equals(
                Matrix.from(
                    arrayOf(
                        arrayOf(1.0, 0.0, 1.0, 10.0, 5.0),
                        arrayOf(2.0, 1.0, 1.0, 5.5, 1.1),
                        arrayOf(0.0, 1.0, 1.0, 3.0, 1.0),
                        arrayOf(1.4, 1.4, 1.4, 3.4, 1.4),
                        arrayOf(0.5, 0.5, 1.5, 3.5, 1.5),
                    ),
                ),
            ),
        )
    }

    @Test
    fun repeatN() {
        val result = Matrix.from(
            arrayOf(
                arrayOf(1.0),
                arrayOf(2.0),
                arrayOf(0.0),
                arrayOf(1.4),
                arrayOf(0.5),
            ),
        ).repeatN(3)

//        result.print()
        assertTrue(
            result.equals(
                Matrix.from(
                    arrayOf(
                        arrayOf(1.0, 1.0, 1.0),
                        arrayOf(2.0, 2.0, 2.0),
                        arrayOf(0.0, 0.0, 0.0),
                        arrayOf(1.4, 1.4, 1.4),
                        arrayOf(0.5, 0.5, 0.5),
                    ),
                ),
            ),
        )
    }

    @Test
    fun repeatM() {
        val result = Matrix.from(
            arrayOf(
                arrayOf(1.0, 2.0, 3.0),
            ),
        ).repeatM(3)

//        result.print()
        assertTrue(
            result.equals(
                Matrix.from(
                    arrayOf(
                        arrayOf(1.0, 2.0, 3.0),
                        arrayOf(1.0, 2.0, 3.0),
                        arrayOf(1.0, 2.0, 3.0),
                    ),
                ),
            ),
        )
    }

    @Test
    fun sumN() {
        val result = Matrix.from(
            arrayOf(
                arrayOf(1.0, 0.0, 1.0, 10.0, 5.0),
                arrayOf(2.0, 1.0, 1.0, 5.5, 1.1),
                arrayOf(0.0, 1.0, 1.0, 3.0, 1.0),
                arrayOf(1.4, 1.4, 1.4, 3.4, 1.4),
            ),
        ).sumN()

//        result.print()
        assertTrue(
            result.equals(
                Matrix.from(
                    arrayOf(
                        arrayOf(4.4, 3.4, 4.4, 21.9, 8.5),
                    ),
                ),
            ),
        )
    }

    @Test
    fun sumM() {
        val result = Matrix.from(
            arrayOf(
                arrayOf(1.0, 0.0, 1.0, 10.0, 5.0),
                arrayOf(2.0, 1.0, 1.0, 5.5, 1.1),
                arrayOf(0.0, 1.0, 1.0, 3.0, 1.0),
                arrayOf(1.4, 1.4, 1.4, 3.4, 1.4),
            ),
        ).sumM()

//        result.print()
        assertTrue(
            result.equals(
                Matrix.from(
                    arrayOf(
                        arrayOf(17.0),
                        arrayOf(10.6),
                        arrayOf(6.0),
                        arrayOf(9.0),
                    ),
                ),
            ),
        )
    }

    @Test
    fun argmaxN() {
        val result = Matrix.from(
            arrayOf(
                arrayOf(1.0, 0.0, 1.0, 10.0, 5.0),
                arrayOf(2.0, 1.0, 1.0, 5.5, 1.1),
                arrayOf(0.0, 1.0, 1.0, 3.0, 1.0),
                arrayOf(1.4, 1.4, 1.4, 3.4, 1.4),
            ),
        ).argmaxN()

//        result.print()
        assertTrue(
            result.equals(
                Matrix.from(
                    arrayOf(
                        arrayOf(1.0, 3.0, 3.0, 0.0, 0.0),
                    ),
                ),
            ),
        )
    }

    @Test
    fun argmaxM() {
        val result = Matrix.from(
            arrayOf(
                arrayOf(1.0, 0.0, 1.0, 10.0, 5.0),
                arrayOf(2.0, 1.0, 1.0, 5.5, 1.1),
                arrayOf(0.0, 1.0, 1.0, 3.0, 1.0),
                arrayOf(1.4, 1.4, 1.4, 3.4, 1.4),
            ),
        ).argmaxM()

        result.print()
        assertTrue(
            result.equals(
                Matrix.from(
                    arrayOf(
                        arrayOf(3.0),
                        arrayOf(3.0),
                        arrayOf(3.0),
                        arrayOf(3.0),
                    ),
                ),
            ),
        )
    }

    @Test
    fun equalsFalse() {
        val result = Matrix.from(
            arrayOf(
                arrayOf(1.0, 0.0, 1.0, 10.0, 5.0),
                arrayOf(2.0, 1.0, 1.0, 5.5, 1.1),
                arrayOf(0.0, 1.0, 1.0, 3.0, 1.0),
                arrayOf(1.4, 1.4, 1.4, 3.4, 1.4),
            ),
        ).equals(
            arrayOf(
                arrayOf(1.0, 0.0, 1.0, 10.0, 55555.0),
                arrayOf(2.0, 1.0, 1.0, 5.5, 1.1),
                arrayOf(0.0, 1.0, 1.0, 3.0, 1.0),
                arrayOf(1.4, 1.4, 1.4, 3.4, 1.4),
            ),
        )

        assertFalse(result)
    }

    @Test
    fun equalsTrue() {
        val result = Matrix.from(
            arrayOf(
                arrayOf(1.0, 0.0, 1.0, 10.0, 5.0),
                arrayOf(2.0, 1.0, 1.0, 5.5, 1.1),
                arrayOf(0.0, 1.0, 1.0, 3.0, 1.0),
                arrayOf(1.4, 1.4, 1.4, 3.4, 1.4),
            ),
        ).equals(
            arrayOf(
                arrayOf(1.0, 0.0, 1.0, 10.0, 5.0),
                arrayOf(2.0, 1.0, 1.0, 5.5, 1.1),
                arrayOf(0.0, 1.0, 1.0, 3.0, 1.0),
                arrayOf(1.4, 1.4, 1.4, 3.4, 1.4),
            ),
        )

        assertFalse(result)
    }

    @Test
    fun apply() {
        val result = Matrix.from(
            arrayOf(
                arrayOf(1.0, 0.0, 1.0, 10.0, 5.0),
                arrayOf(2.0, 1.0, 1.0, 5.5, 1.1),
                arrayOf(0.0, 1.0, 1.0, 3.0, 1.0),
                arrayOf(1.4, 1.4, 1.4, 3.4, 1.4),
                arrayOf(0.5, 0.5, 1.5, 3.5, 1.5),
            ),
        ).apply { _, _, v -> 2 * v }

//        result.print()
        assertTrue(
            result.equals(
                Matrix.from(
                    arrayOf(
                        arrayOf(2.0, 0.0, 2.0, 20.0, 10.0),
                        arrayOf(4.0, 2.0, 2.0, 11.0, 2.2),
                        arrayOf(0.0, 2.0, 2.0, 6.0, 2.0),
                        arrayOf(2.8, 2.8, 2.8, 6.8, 2.8),
                        arrayOf(1.0, 1.0, 3.0, 7.0, 3.0),
                    ),
                ),
            ),
        )
    }

    @Test
    fun cosineSimilarity() {
        val result = Matrix.from(
            arrayOf(
                arrayOf(1.0, 0.0, 1.0),
                arrayOf(2.0, 1.0, 1.0),
                arrayOf(0.0, 1.0, 1.0),
                arrayOf(1.4, 1.4, 1.4),
                arrayOf(0.5, 0.5, 1.5),
            ),
        ).cosineSimilarity()

//        result.print()
        assertTrue(
            result.equals(
                Matrix.from(
                    arrayOf(
                        arrayOf(1.0, 0.7641408472243002, 0.7919556171983357),
                        arrayOf(0.7641408472243002, 1.0, 0.8548939169659036),
                        arrayOf(0.7919556171983357, 0.8548939169659036, 1.0),
                    ),
                ),
            ),
        )
    }
}
