package com.wutsi.platform.core.image.imagekit

import com.wutsi.platform.core.image.Dimension
import com.wutsi.platform.core.image.Focus
import com.wutsi.platform.core.image.Format
import com.wutsi.platform.core.image.Overlay
import com.wutsi.platform.core.image.OverlayType
import com.wutsi.platform.core.image.Transformation
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class ImageKitServiceTest {
    private val service = ImageKitService("http://www.google.com", listOf("http://www.imagekit.io/43043094"))

    @Test
    fun rotateEndpoints() {
        val imageKit = ImageKitService(
            "https://img.com",
            listOf("http://www.imagekit.io/001", "http://www.imagekit.io/002", "http://www.imagekit.io/003")
        )

        val url = "https://img.com/1.png"
        assertEquals("http://www.imagekit.io/001/1.png", imageKit.transform(url))
        assertEquals("http://www.imagekit.io/002/1.png", imageKit.transform(url))
        assertEquals("http://www.imagekit.io/003/1.png", imageKit.transform(url))
        assertEquals("http://www.imagekit.io/001/1.png", imageKit.transform(url))
    }

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

    @Test
    fun textOverlay() {
        val url = "http://www.google.com/img/a/b/1.png"
        val result = service.transform(
            url,
            Transformation(
                overlay = Overlay(
                    type = OverlayType.TEXT,
                    input = "yo-man"
                )
            ),
        )

        assertEquals("http://www.imagekit.io/43043094/img/a/b/tr:l-text,i-yo-man,l-end/1.png", result)
    }

    @Test
    fun imageOverlay() {
        val url = "http://www.google.com/img/a/b/1.png"
        val result = service.transform(
            url,
            Transformation(
                overlay = Overlay(
                    type = OverlayType.IMAGE,
                    input = "yo-man.png",
                    dimension = Dimension(width = 10, height = 30)
                )
            ),
        )

        assertEquals("http://www.imagekit.io/43043094/img/a/b/tr:l-image,i-yo-man.png,w-10,h-30,l-end/1.png", result)
    }
}
