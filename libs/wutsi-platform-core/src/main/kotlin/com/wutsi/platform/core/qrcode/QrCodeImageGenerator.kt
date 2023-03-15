package com.wutsi.platform.core.qrcode

import io.github.g0dkar.qrcode.QRCode
import org.slf4j.LoggerFactory
import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.net.URL
import javax.imageio.ImageIO

class QrCodeImageGenerator(
    private val logoUrl: URL? = null,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(QrCodeImageGenerator::class.java)
    }

    fun generate(data: String, output: OutputStream) {
        val qr = ByteArrayOutputStream()
        QRCode(data)
            .render(margin = 30, cellSize = 30)
            .writeImage(qr)

        val logo = downloadLogo()
        val combined = combine(ImageIO.read(ByteArrayInputStream(qr.toByteArray())), logo)
        ImageIO.write(combined, "png", output)
    }

    private fun downloadLogo(): BufferedImage? =
        try {
            logoUrl?.let { ImageIO.read(it) }
        } catch (ex: Exception) {
            LOGGER.warn("Unable to download image $logoUrl", ex)
            null
        }

    private fun combine(qrcode: BufferedImage, logo: BufferedImage?): BufferedImage {
        logo ?: return qrcode

        val deltaHeight: Int = qrcode.height - logo.height
        val deltaWidth: Int = qrcode.width - logo.width

        val combined = BufferedImage(qrcode.width, qrcode.height, BufferedImage.TYPE_INT_ARGB)
        val g2 = combined.graphics as Graphics2D
        g2.drawImage(qrcode, 0, 0, null)
        g2.background = Color.WHITE
        g2.fillOval(
            Math.round((deltaWidth / 2).toFloat()),
            Math.round((deltaHeight / 2).toFloat()),
            logo.width,
            logo.height,
        )
        g2.drawImage(
            logo,
            Math.round((deltaWidth / 2).toFloat()),
            Math.round((deltaHeight / 2).toFloat()),
            null,
        )
        return combined
    }
}
