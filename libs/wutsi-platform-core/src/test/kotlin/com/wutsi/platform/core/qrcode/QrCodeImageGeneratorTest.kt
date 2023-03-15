package com.wutsi.platform.core.qrcode

import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.net.URL

internal class QrCodeImageGeneratorTest {
    private val data = "YWNjb3VudCwxMjMsMjE0NzQ4MzY0Nw==.MQ==.MmE5N2UxZWRhZjVhZTE5YmNmZDZkNjE4OWY1NWFiNzQ="

    @Test
    fun generateNoLogo() {
        val generator = QrCodeImageGenerator()
        val out = ByteArrayOutputStream()
        generator.generate(data, out)
    }

    @Test
    fun generateWithLogo() {
        val generator =
            QrCodeImageGenerator(URL("https://prod-wutsi.s3.amazonaws.com/static/wutsi-blog-web/assets/wutsi/img/logo/name-104x50.png"))
        val out = ByteArrayOutputStream()
        generator.generate(data, out)
    }
}
