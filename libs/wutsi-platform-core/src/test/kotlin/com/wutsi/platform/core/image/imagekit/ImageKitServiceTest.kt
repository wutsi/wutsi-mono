package com.wutsi.platform.core.image.imagekit

import com.wutsi.platform.core.image.Dimension
import com.wutsi.platform.core.image.Focus
import com.wutsi.platform.core.image.Format
import com.wutsi.platform.core.image.Transformation
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class ImageKitServiceTest {
    private val service = ImageKitService("http://www.google.com", "http://www.imagekit.io/43043094")

    @Test
    fun transformWidthAndHeight() {
        val url = "http://www.google.com/img/a/b/1.png"
        val result = service.transform(
            url,
            Transformation(
                dimension = Dimension(
                    width = 200,
                    height = 150,
                ),
            ),
        )

        assertEquals("http://www.imagekit.io/43043094/img/a/b/tr:w-200,h-150/1.png", result)
    }

    @Test
    fun transformWidth() {
        val url = "http://www.google.com/img/a/b/1.png"
        val result = service.transform(
            url,
            Transformation(
                dimension = Dimension(width = 200),
            ),
        )

        assertEquals("http://www.imagekit.io/43043094/img/a/b/tr:w-200/1.png", result)
    }

    @Test
    fun transformHeight() {
        val url = "http://www.google.com/img/a/b/1.png"
        val result = service.transform(
            url,
            Transformation(
                dimension = Dimension(height = 150),
            ),
        )

        assertEquals("http://www.imagekit.io/43043094/img/a/b/tr:h-150/1.png", result)
    }

    @Test
    fun transformWithFocus() {
        val url = "http://www.google.com/img/a/b/1.png"
        val result = service.transform(
            url,
            Transformation(
                dimension = Dimension(
                    width = 400,
                    height = 150,
                ),
                focus = Focus.FACE,
            ),
        )

        assertEquals("http://www.imagekit.io/43043094/img/a/b/tr:w-400,h-150,fo-face/1.png", result)
    }

    @Test
    fun transformNone() {
        val url = "http://www.google.com/img/a/b/1.png"
        val result = service.transform(url)

        assertEquals("http://www.imagekit.io/43043094/img/a/b/1.png", result)
    }

    @Test
    fun transformInvalidOrigin() {
        val url = "http://www.yo.com/img/a/b/1.png"
        val result = service.transform(url)

        assertEquals(url, result)
    }

    @Test
    fun transformToJPG() {
        val url = "http://www.google.com/img/a/b/1.png"
        val result = service.transform(
            url,
            Transformation(
                format = Format.JPG,
            ),
        )

        assertEquals("http://www.imagekit.io/43043094/img/a/b/tr:f-jpg/1.png", result)
    }

    @Test
    fun transformToPNG() {
        val url = "http://www.google.com/img/a/b/1.png"
        val result = service.transform(
            url,
            Transformation(
                format = Format.PNG,
            ),
        )

        assertEquals("http://www.imagekit.io/43043094/img/a/b/tr:f-png/1.png", result)
    }

    @Test
    fun transformToGIF() {
        val url = "http://www.google.com/img/a/b/1.png"
        val result = service.transform(
            url,
            Transformation(
                format = Format.GIF,
            ),
        )

        assertEquals("http://www.imagekit.io/43043094/img/a/b/tr:f-gif/1.png", result)
    }
}
