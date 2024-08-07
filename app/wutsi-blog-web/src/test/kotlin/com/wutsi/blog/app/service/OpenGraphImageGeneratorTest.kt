package com.wutsi.blog.app.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

class OpenGraphImageGeneratorTest {
    private val generator = OpenGraphImageGenerator()

    @Test
    fun `no picture`() {
        val out = ByteArrayOutputStream()
        generator.generate(ImageType.PROFILE, null, "Ray Sponsible", "This is an example of blog", "en", out)

        val img = ImageIO.read(ByteArrayInputStream(out.toByteArray()))
        assertEquals(1200, img.width)
        assertEquals(630, img.height)
    }

    @Test
    fun `with picture`() {
        val out = ByteArrayOutputStream()
        generator.generate(ImageType.PROFILE, "https://picsum.photos/200/200", "Ray Sponsible", null, "fr", out)

        val img = ImageIO.read(ByteArrayInputStream(out.toByteArray()))
        assertEquals(1200, img.width)
        assertEquals(630, img.height)
    }

    @Test
    fun `with no language`() {
        val out = ByteArrayOutputStream()
        generator.generate(ImageType.DONATION, "https://picsum.photos/200/200", "Ray Sponsible", null, "", out)

        val img = ImageIO.read(ByteArrayInputStream(out.toByteArray()))
        assertEquals(1200, img.width)
        assertEquals(630, img.height)
    }

    @Test
    fun `split description - 1 line`() {
        assertEquals(
            listOf("This is an example of blog"),
            generator.splitDescription("This is an example of blog"),
        )
    }

    @Test
    fun `split description - 2 lines`() {
        assertEquals(
            listOf("This is an example of blog that span accorss 2", "lines"),
            generator.splitDescription("This is an example of blog that span accorss 2 lines"),
        )
    }

    @Test
    fun `split description - more than 2 lines`() {
        assertEquals(
            listOf("This is an example of blog that span accorss 2", "lines. This is an example of blog that span..."),
            generator.splitDescription("This is an example of blog that span accorss 2 lines. This is an example of blog that span accoss 2 lines. This is an example of blog that span accoss 2 lines"),
        )
    }

    @Test
    fun `split text - 2 lines`() {
        assertEquals(
            listOf("This is an example of blog that span accross 2", " lines"),
            generator.split("This is an example of blog that span accross 2 lines", 2, 50),
        )
    }

    @Test
    fun `split text - more than 3 lines`() {
        assertEquals(
            listOf(
                "This is an example of blog that span accross 2",
                " lines. This is an example of blog that span",
                " accoss 2 lines. This is an example of blog that"
            ),
            generator.split(
                "This is an example of blog that span accross 2 lines. This is an example of blog that span accoss 2 lines. This is an example of blog that span accoss 2 lines",
                3,
                50
            ),
        )
    }
}
