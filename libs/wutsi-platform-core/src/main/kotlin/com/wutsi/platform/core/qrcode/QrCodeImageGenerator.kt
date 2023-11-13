package com.wutsi.platform.core.qrcode

import qrcode.QRCode
import java.io.ByteArrayInputStream
import java.io.OutputStream
import javax.imageio.ImageIO

class QrCodeImageGenerator {
    fun generate(
        data: String,
        output: OutputStream,
        logo: ByteArray? = null,
        logoWidth: Int = 300,
        logoutHeight: Int = 300
    ) {
        val builder = QRCode.ofSquares()
            .withSize(30)

        if (logo != null) {
            builder.withLogo(logo, logoWidth, logoutHeight, true)
        }

        val qr = builder.build(data).render()

        val img = ImageIO.read(ByteArrayInputStream(qr.getBytes()))
        ImageIO.write(img, "png", output)
    }
}
