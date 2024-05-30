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
    EBOOK,
}

@Service
class OpenGraphImageGenerator {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(OpenGraphImageGenerator::class.java)
        const val LOGO_IMAGE_WIDTH = 100
        const val LOGO_IMAGE_HEIGHT = 100
        const val EBOOK_IMAGE_WIDTH = 400
        const val EBOOK_IMAGE_HEIGHT = 620
        private const val TITLE_MAX_LEN = 50
        private const val DESCRIPTION_LINE_LENGTH = 50
    }

    /**
     * Assumption:
     *  - Image size: 1200 x 630
     *  - pictureURL size:
     *      for ebook: 400x600
     *      else: 100x100
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

        if (type == ImageType.EBOOK) {
            addPicture(pictureUrl, image, 0, 0, EBOOK_IMAGE_WIDTH, EBOOK_IMAGE_HEIGHT, 5)
            addDescription(description, image, 430, 250, 6)
        } else {
            addPicture(pictureUrl, image, 100, 265, LOGO_IMAGE_WIDTH, LOGO_IMAGE_HEIGHT, 5)
            addTitle(title, image, 50, 200)
            addDescription(description, image, 350, 300, 4)
        }
        ImageIO.write(image, "png", output)
    }

    private fun addPicture(
        pictureUrl: String?,
        image: BufferedImage,
        x: Int,
        y: Int,
        width: Int,
        height: Int,
        padding: Int,
    ) {
        pictureUrl ?: return

        val gr = image.graphics as Graphics2D
        val pastBackground = gr.background
        try {
            gr.background = Color.WHITE
            gr.fillRect(x, y, width + 2 * padding, height + 2 * padding)
            drawImage(pictureUrl, x + padding, y + padding, image)
        } finally {
            gr.background = pastBackground
        }
    }

    private fun addTitle(title: String, image: BufferedImage, x: Int, y: Int) {
        drawPicture(title.take(TITLE_MAX_LEN), x, y, 50, Font.BOLD, Color.BLACK, image)
    }

    private fun addDescription(description: String?, image: BufferedImage, x: Int, y: Int, maxLines: Int) {
        description ?: return

        val descr = split(description, maxLines, DESCRIPTION_LINE_LENGTH)
        var cy = y
        descr.forEach { line ->
            drawPicture(line, x, cy, 30, Font.PLAIN, Color.DARK_GRAY, image)
            cy += 40
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

    fun split(text: String, maxLines: Int, maxLineLength: Int): List<String> {
        val lines = mutableListOf<String>()
        var cur = 0
        val line = StringBuilder()
        while (true) {
            if (cur >= text.length) {
                lines.add(line.toString())
                break
            }

            val ch = text[cur++]
            if (ch == '\n') {
                lines.add(line.toString())
                line.clear()
            } else {
                if (line.length == maxLineLength) {
                    val xcur = toPreviousSeparator(text, --cur)
                    if (xcur > 0) {
                        lines.add(line.substring(0, maxLineLength - cur + xcur))
                        cur = xcur
                    } else {
                        lines.add(line.toString())
                    }
                    line.clear()
                } else {
                    line.append(ch)
                }
            }

            if (lines.size >= maxLines) {
                break
            }
        }
        return lines
    }

    private fun toPreviousSeparator(text: String, cur: Int): Int {
        var i = cur - 1
        while (i > 0) {
            val ch = text[i]
            if (ch.isWhitespace() || ".,:;!?-".contains(ch)) {
                break
            } else {
                i--
            }
        }
        return i
    }

    private fun toPreviousSeparator(text: String): Int {
        var i = text.length - 1
        while (i > 0) {
            val ch = text[i]
            if (ch.isWhitespace() || ".,:;!?-".contains(ch)) {
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
            ImageType.EBOOK -> "ebook"
            else -> "profile"
        }

        val input = OpenGraphImageGenerator::class.java.getResource("/opengraph/$prefix-$suffix.png")
        return ImageIO.read(input)
    }
}
