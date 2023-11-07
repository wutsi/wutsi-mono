package com.wutsi.platform.core.qrcode

import org.apache.commons.io.IOUtils
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URL

internal class QrCodeImageGeneratorTest {
    private val data = "YWNjb3VudCwxMjMsMjE0NzQ4MzY0Nw==.MQ==.MmE5N2UxZWRhZjVhZTE5YmNmZDZkNjE4OWY1NWFiNzQ="

    @Test
    fun generateNoLogo() {
        val generator = QrCodeImageGenerator()
        val out = ByteArrayOutputStream()
        generator.generate(data, out)

        toFile("qr.png", out.toByteArray())
    }

    @Test
    fun generateWithLogo() {
        val generator = QrCodeImageGenerator()
        val logoStream =
            URL("https://prod-wutsi.s3.amazonaws.com/static/wutsi-blog-web/assets/wutsi/img/logo/name-104x50.png").openStream()
        logoStream.use {
            val logo = ByteArrayOutputStream()
            IOUtils.copy(logoStream, logo)
            val out = ByteArrayOutputStream()
            generator.generate(data, out, logo.toByteArray(), 300, 300)

            toFile("qr-logo.png", out.toByteArray())
        }
    }

    private fun toFile(name: String, out: ByteArray) {
        val file = File(System.getProperty("user.home") + "/wutsi", name)
        file.parentFile.mkdirs()

        val fout = FileOutputStream(file)
        fout.use {
            IOUtils.copy(ByteArrayInputStream(out), fout)
        }
    }
}
