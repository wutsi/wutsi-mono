package com.wutsi.blog.app.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.OutputStream
import java.net.URL
import javax.imageio.ImageIO
import kotlin.math.min

enum class ImageType {
    PROFILE,
    DONATION,
    SHOP,
}

@Service
class OpenGraphImageGenerator {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(OpenGraphImageGenerator::class.java)
        const val IMAGE_WIDTH = 100
        const val IMAGE_HEIGHT = 100
        private const val TITLE_MAX_LEN = 50
        private const val DESCRIPTION_LINE_LENGTH = 50
    }

    /**
     * Assumption:
     *  - Image size: 1200 x 630
     *  - pictureURL size: 100x100
     */
    fun generate(
        type: ImageType,
        pictureUrl: String?,
        title: String,
        description: String?,
        language: String?,
        output: OutputStream,
    ) {
        val image = loadBackground(type, language)

        addPicture(pictureUrl, image)
        addTitle(title, image)
        addDescription(description, image)

        ImageIO.write(image, "png", output)
    }

    private fun addPicture(pictureUrl: String?, image: BufferedImage) {
        pictureUrl ?: return

        val x = 100
        val y = 265
        val width = IMAGE_WIDTH
        val height = IMAGE_HEIGHT
        val padding = 5

        val gr = image.graphics as Graphics2D
        val pastBackground = gr.background
        try {
            gr.background = Color.WHITE
            gr.fillRect(x - padding, y - padding, width + 2 * padding, height + 2 * padding)
            drawImage(pictureUrl, x, y, image)
        } finally {
            gr.background = pastBackground
        }
    }

    private fun addTitle(title: String, image: BufferedImage) {
        drawPicture(title.take(TITLE_MAX_LEN), 350, 200, 50, Font.BOLD, Color.BLACK, image)
    }

    private fun addDescription(description: String?, image: BufferedImage) {
        description ?: return

        val descr = splitDescription(description)
        var y = 300
        descr.forEach { line ->
            drawPicture(line, 350, y, 30, Font.PLAIN, Color.DARK_GRAY, image)
            y += 40
        }
    }

    fun splitDescription(description: String): List<String> {
        if (description.length < DESCRIPTION_LINE_LENGTH) {
            return listOf(description)
        }

        var line1 = description.take(DESCRIPTION_LINE_LENGTH)
        val i = toPreviousSeparator(line1)
        line1 = line1.substring(0, i)

        var line2 = description.substring(i, min(i + DESCRIPTION_LINE_LENGTH, description.length))
        if (line2.length >= DESCRIPTION_LINE_LENGTH) {
            val j = toPreviousSeparator(line2)
            line2 = line2.substring(0, j)

            if (description.length > 2 * DESCRIPTION_LINE_LENGTH) {
                line2 += "..."
            }
        }

        return listOf(line1.trim(), line2.trim())
    }

    private fun toPreviousSeparator(text: String): Int {
        var i = text.length - 1
        while (i > 0) {
            val ch = text[i]
            if (ch.isWhitespace() || ".,:;!?".contains(ch)) {
                break
            } else {
                i--
            }
        }
        return i
    }

    private fun drawPicture(
        text: String,
        x: Int,
        y: Int,
        size: Int,
        style: Int,
        color: Color,
        image: BufferedImage,
    ) {
        val gr = image.graphics as Graphics2D

        val pastFont = gr.font
        val pastColor = gr.color
        try {
            gr.color = color
            gr.font = Font("Lucinda", style, size)
            gr.drawString(text, x.toFloat(), y.toFloat())
        } finally {
            gr.font = pastFont
            gr.color = pastColor
        }
    }

    private fun drawImage(
        url: String,
        x: Int,
        y: Int,
        image: BufferedImage,
    ) {
        try {
            val img = ImageIO.read(URL(url))
            val gr = image.graphics as Graphics2D
            gr.drawImage(img, x, y, null)
        } catch (ex: Exception) {
            LOGGER.warn("Unable to draw image", ex)
        }
    }

    /**
     * Doanload background image: size: 1200 x 630
     */
    private fun loadBackground(type: ImageType, language: String?): BufferedImage {
        val suffix = when (language?.lowercase()) {
            "fr" -> "fr"
            "en" -> "en"
            else -> "en"
        }

        val prefix = when (type) {
            ImageType.DONATION -> "donate"
            ImageType.SHOP -> "shop"
            else -> "profile"
        }

        val input = OpenGraphImageGenerator::class.java.getResource("/opengraph/$prefix-$suffix.png")
        return ImageIO.read(input)
    }
}
