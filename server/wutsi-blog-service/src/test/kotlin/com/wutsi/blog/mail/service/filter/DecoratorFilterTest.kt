package com.wutsi.blog.mail.service.filter

import com.wutsi.blog.Fixtures
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class DecoratorFilterTest {
    private val context = Fixtures.createMailContext()
    private val filter = DecoratorFilter()

    @Test
    fun decorate() {
        val html = """
            <p>Hello world</p>
        """.trimIndent()

        val result = filter.filter(html, context)
        assertEquals(
            """
<html>
<head>
    <meta content="text/html; charset=UTF-8" http-equiv="Content-Type"/>
    <link href="https://fonts.googleapis.com/css2?family=PT+Sans&display=swap" rel="stylesheet"/>
</head>
<body>
<table cellpadding="0" cellspacing="10" class="body" width="100%">
    <tr>
        <td align="center">
            <table cellpadding="0" cellspacing="0" class="content" width="100%">
                <tr>
                    <td align="center" class="padding">
                        <a href="https://www.wutsi.com/@/maison-h">
                            <img class="border" height="64" src="https://ik.imagekit.io/cx8qxsgz4d/user/12/picture/tr:w-64,h-64,fo-face/023bb5c8-7b09-4f2f-be51-29f5c851c2c0-scaled_image_picker1721723356188894418.png"
                                 style="border-radius: 32px 32px 32px 32px; vertical-align: middle; padding: 2px"
                                 width="64"/>
                        </a>
                    </td>
                </tr>
                <tr>
                    <td class="border-top">
                        <p>Hello world</p>
                    </td>
                </tr>
                <tr>
                    <td class="border-top padding">
                        <table border="0" cellpadding="0" cellspacing="5" width="100%">
                            <tr>
                                <td align="center" class="text-small">
                                    <b>Maison K</b>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>
                <tr>
                    <td align="center" class="text-small padding border-top">
                        Powered by <a href="https://www.wutsi.com">Wutsi</a>.
                    </td>
                </tr>
            </table>
        </td>
    </tr>
</table>
</body>
</html>
            """.trimIndent(),
            result.trimIndent(),
        )
    }
}
