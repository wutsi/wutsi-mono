package com.wutsi.ml.matrix

import org.apache.commons.io.IOUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.nio.file.Files
import kotlin.test.assertFalse

internal class MatrixTest {
    @Test
    fun random() {
        val result = Matrix.random(10, 11)
        for (i in 0 until 10) {
            for (j in 0 until 11) {
                assertTrue(result.get(i, j) >= 0)
                assertTrue(result.get(i, j) <= 1)
            }
        }
    }

    @Test
    fun identity() {
        val result = Matrix.identify(10)
        for (i in 0 until 10) {
            for (j in 0 until 10) {
                if (i == j) {
                    assertEquals(1.0, result.get(i, j))
                } else {
                    assertEquals(0.0, result.get(i, j))
                }
            }
        }
    }

    @Test
    fun plusScalar() {
        val result = Matrix(2, 2).plus(10.0)
        for (i in 0 until 2) {
            for (j in 0 until 2) {
                assertEquals(10.0, result.get(i, j))
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
                assertEquals(3.0, result.get(i, j))
            }
        }
    }

    @Test
    fun `plus with matrix size mismatch`() {
        assertThrows<RuntimeException> {
            Matrix.of(2, 2, 1.0)
                .plus(
                    Matrix.of(2, 3, 2.0),
                )
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
                assertEquals(-10.0, result.get(i, j))
            }
        }
    }

    @Test
    fun minusReajustN() {
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
                assertEquals(-1.0, result.get(i, j))
            }
        }
    }

    @Test
    fun `minus with matrix size mismatch`() {
        assertThrows<RuntimeException> {
            Matrix.of(2, 2, 1.0)
                .minus(
                    Matrix.of(2, 3, 2.0),
                )
        }
    }

    @Test
    fun timesScalar() {
        val result = Matrix.of(2, 2, 2.0).times(10.0)
        for (i in 0 until 2) {
            for (j in 0 until 2) {
                assertEquals(20.0, result.get(i, j))
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
    fun `times with matrix size mismatch`() {
        assertThrows<RuntimeException> {
            Matrix.of(2, 2, 1.0)
                .times(
                    Matrix.of(2, 3, 2.0),
                )
        }
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
    fun `dot with matrix size mismatch`() {
        assertThrows<RuntimeException> {
            Matrix.from(
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
                        arrayOf(4.0, 2.0, 2.0),
                        arrayOf(4.0, 2.0, 2.0),
                    ),
                ),
            )
        }
    }

    @Test
    fun dotIJ() {
        val m1 = Matrix.from(
            arrayOf(
                arrayOf(1.0, 0.0, 1.0),
                arrayOf(2.0, 1.0, 1.0),
                arrayOf(0.0, 1.0, 1.0),
                arrayOf(1.0, 1.0, 2.0),
            ),
        )
        val m2 = Matrix.from(
            arrayOf(
                arrayOf(1.0, 2.0, 1.0),
                arrayOf(2.0, 3.0, 1.0),
                arrayOf(4.0, 2.0, 2.0),
            ),
        )
        assertEquals(5.0, m1.dot(m2, 1, 2))
        assertEquals(9.0, m1.dot(m2, 1, 1))
        assertEquals(11.0, m1.dot(m2, 3, 0))
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
    fun `min with size mismatch`() {
        assertThrows<RuntimeException> {
            Matrix.from(
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
                        ),
                    ),
                )
        }
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
    fun `max with size mismatch`() {
        assertThrows<RuntimeException> {
            Matrix.from(
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
                        ),
                    ),
                )
        }
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
//        result.print()
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
    fun `div with matrix size mismatch`() {
        assertThrows<RuntimeException> {
            Matrix.of(2, 2, 1.0)
                .div(
                    Matrix.of(2, 3, 2.0),
                )
        }
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
        ).repeat(3, Axis.N)

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
        ).repeat(3, Axis.M)

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
    fun sumM() {
        val result = Matrix.from(
            arrayOf(
                arrayOf(1.0, 0.0, 1.0, 10.0, 5.0),
                arrayOf(2.0, 1.0, 1.0, 5.5, 1.1),
                arrayOf(0.0, 1.0, 1.0, 3.0, 1.0),
                arrayOf(1.4, 1.4, 1.4, 3.4, 1.4),
            ),
        ).sum(Axis.M)

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
    fun sumN() {
        val result = Matrix.from(
            arrayOf(
                arrayOf(1.0, 0.0, 1.0, 10.0, 5.0),
                arrayOf(2.0, 1.0, 1.0, 5.5, 1.1),
                arrayOf(0.0, 1.0, 1.0, 3.0, 1.0),
                arrayOf(1.4, 1.4, 1.4, 3.4, 1.4),
            ),
        ).sum(Axis.N)

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
    fun argmaxM() {
        val result = Matrix.from(
            arrayOf(
                arrayOf(1.0, 0.0, 1.0, 10.0, 5.0),
                arrayOf(2.0, 1.0, 1.0, 5.5, 1.1),
                arrayOf(0.0, 1.0, 1.0, 3.0, 1.0),
                arrayOf(1.4, 1.4, 1.4, 3.4, 1.4),
            ),
        ).argmax(Axis.M)

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
    fun argmaxN() {
        val result = Matrix.from(
            arrayOf(
                arrayOf(1.0, 0.0, 1.0, 10.0, 5.0),
                arrayOf(2.0, 1.0, 1.0, 5.5, 1.1),
                arrayOf(0.0, 1.0, 1.0, 3.0, 1.0),
                arrayOf(1.4, 1.4, 1.4, 3.4, 1.4),
            ),
        ).argmax(Axis.N)

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
    fun equalsWithSizeMismatch() {
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
                arrayOf(1.4, 1.4, 1.4, 3.4, 1.4),
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
        ).cosineSimilarity(false)

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

    @Test
    fun cosineSimilarityWithHeader() {
        val result = Matrix.from(
            arrayOf(
                arrayOf(100.0, 200.0, 300.0),
                arrayOf(1.0, 0.0, 1.0),
                arrayOf(2.0, 1.0, 1.0),
                arrayOf(0.0, 1.0, 1.0),
                arrayOf(1.4, 1.4, 1.4),
                arrayOf(0.5, 0.5, 1.5),
            ),
        ).cosineSimilarity(true)

//        result.print()
        assertTrue(
            result.equals(
                Matrix.from(
                    arrayOf(
                        arrayOf(100.0, 200.0, 300.0),
                        arrayOf(1.0, 0.7641408472243002, 0.7919556171983357),
                        arrayOf(0.7641408472243002, 1.0, 0.8548939169659036),
                        arrayOf(0.7919556171983357, 0.8548939169659036, 1.0),
                    ),
                ),
            ),
        )
    }

    @Test
    fun concatenateM() {
        val result = Matrix.from(
            arrayOf(
                arrayOf(1.0, 0.0, 1.0),
                arrayOf(2.0, 1.0, 1.0),
            ),
        ).concatenate(
            Matrix.from(
                arrayOf(
                    arrayOf(0.0, 1.0, 1.0),
                    arrayOf(1.4, 1.4, 1.4),
                    arrayOf(0.5, 0.5, 1.5),
                ),
            ),
            Axis.M,
        )

        assertTrue(
            result.equals(
                Matrix.from(
                    arrayOf(
                        arrayOf(1.0, 0.0, 1.0),
                        arrayOf(2.0, 1.0, 1.0),
                        arrayOf(0.0, 1.0, 1.0),
                        arrayOf(1.4, 1.4, 1.4),
                        arrayOf(0.5, 0.5, 1.5),
                    ),
                ),
            ),
        )
    }

    @Test
    fun concatenateN() {
        val result = Matrix.from(
            arrayOf(
                arrayOf(1.0),
                arrayOf(2.0),
                arrayOf(0.0),
                arrayOf(1.4),
                arrayOf(0.5),
            ),
        ).concatenate(
            Matrix.from(
                arrayOf(
                    arrayOf(0.0, 1.0),
                    arrayOf(1.0, 1.0),
                    arrayOf(1.0, 1.0),
                    arrayOf(1.4, 1.4),
                    arrayOf(0.5, 1.5),
                ),
            ),
            Axis.N,
        )

        assertTrue(
            result.equals(
                Matrix.from(
                    arrayOf(
                        arrayOf(1.0, 0.0, 1.0),
                        arrayOf(2.0, 1.0, 1.0),
                        arrayOf(0.0, 1.0, 1.0),
                        arrayOf(1.4, 1.4, 1.4),
                        arrayOf(0.5, 0.5, 1.5),
                    ),
                ),
            ),
        )
    }

    @Test
    fun toList() {
        val result = Matrix.from(
            arrayOf(
                arrayOf(0.0, 1.0),
                arrayOf(1.0, 1.0),
                arrayOf(1.0, 1.0),
                arrayOf(1.4, 1.4),
                arrayOf(0.5, 1.5),
            ),
        ).toList()

        assertEquals(
            listOf(
                0.0, 1.0,
                1.0, 1.0,
                1.0, 1.0,
                1.4, 1.4,
                0.5, 1.5,
            ),
            result,
        )
    }

    @Test
    fun `import file`() {
        // GIVEN
        val input = MatrixTest::class.java.getResourceAsStream("/matrix.csv")
        val file = Files.createTempFile("from-file", ".csv").toFile()
        val fout = FileOutputStream(file)
        fout.use {
            IOUtils.copy(input, fout)
        }

        // WHEN
        val result = Matrix.from(file, false)

//        result.print()
        assertTrue(
            result.equals(
                Matrix.from(
                    arrayOf(
                        arrayOf(1.0, 2.0),
                        arrayOf(3.0, 4.0),
                    ),
                ),
            ),
        )
    }

    @Test
    fun `import file - ignore header`() {
        // GIVEN
        val input = MatrixTest::class.java.getResourceAsStream("/matrix_with_header.csv")
        val file = Files.createTempFile("from-file", ".csv").toFile()
        val fout = FileOutputStream(file)
        fout.use {
            IOUtils.copy(input, fout)
        }

        // WHEN
        val result = Matrix.from(file, true)

//        result.print()
        assertTrue(
            result.equals(
                Matrix.from(
                    arrayOf(
                        arrayOf(1.0, 2.0),
                        arrayOf(3.0, 4.0),
                    ),
                ),
            ),
        )
    }

    @Test
    fun sqrt() {
        val result = Matrix.from(
            arrayOf(
                arrayOf(1.0),
                arrayOf(4.0),
                arrayOf(9.0),
                arrayOf(16.0),
                arrayOf(25.0),
            ),
        ).sqrt()

        assertTrue(
            result.equals(
                Matrix.from(
                    arrayOf(
                        arrayOf(1.0),
                        arrayOf(2.0),
                        arrayOf(3.0),
                        arrayOf(4.0),
                        arrayOf(5.0),
                    ),
                ),
            ),
        )
    }

    @Test
    fun tanh() {
        val result = Matrix.from(
            arrayOf(
                arrayOf(1.0),
                arrayOf(1.1),
                arrayOf(2.2),
            ),
        ).tanh()

        assertTrue(
            result.equals(
                Matrix.from(
                    arrayOf(
                        arrayOf(0.7615941559557649),
                        arrayOf(0.8004990217606297),
                        arrayOf(0.9757431300314515),
                    ),
                ),
            ),
        )
    }

    @Test
    fun log() {
        val result = Matrix.from(
            arrayOf(
                arrayOf(1.0),
                arrayOf(1.1),
                arrayOf(2.2),
            ),
        ).log()

        assertTrue(
            result.equals(
                Matrix.from(
                    arrayOf(
                        arrayOf(0.0),
                        arrayOf(0.04139268515822507),
                        arrayOf(0.3424226808222062),
                    ),
                ),
            ),
        )
    }

    @Test
    fun gt() {
        val result = Matrix.from(
            arrayOf(
                arrayOf(100.0, 200.0, 300.0),
                arrayOf(1.0, 0.0, 1.0),
                arrayOf(2.0, 1.0, 1.0),
                arrayOf(0.0, 1.0, 1.0),
                arrayOf(1.4, 1.4, 1.4),
                arrayOf(0.5, 0.5, 1.5),
            ),
        ).gt(2.0)

        result.print()
        assertTrue(
            result.equals(
                Matrix.from(
                    arrayOf(
                        arrayOf(1.0, 1.0, 1.0),
                        arrayOf(0.0, 0.0, 0.0),
                        arrayOf(0.0, 0.0, 0.0),
                        arrayOf(0.0, 0.0, 0.0),
                        arrayOf(0.0, 0.0, 0.0),
                        arrayOf(0.0, 0.0, 0.0),
                    ),
                ),
            ),
        )
    }
}
