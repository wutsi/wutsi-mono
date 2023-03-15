package com.wutsi.platform.core.image.none

import com.wutsi.platform.core.image.AspectRatio
import com.wutsi.platform.core.image.Dimension
import com.wutsi.platform.core.image.Focus
import com.wutsi.platform.core.image.ImageService
import com.wutsi.platform.core.image.Transformation
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class ImageServiceNoneTest {
    private val imageService: ImageService = ImageServiceNone()

    @Test
    fun transform() {
        val url = "https://www.google.ca"
        assertEquals(
            url,
            imageService.transform(
                url,
                Transformation(
                    dimension = Dimension(width = 100, height = 100),
                    aspectRatio = AspectRatio(16, 9),
                    focus = Focus.FACE,
                ),
            ),
        )
    }
}
