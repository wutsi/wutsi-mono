package com.wutsi.blog.mail.service.filter

import com.wutsi.blog.Fixtures
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.context.support.ResourceBundleMessageSource

internal class DecoratorFilterTest {
    private val context = Fixtures.createMailContext()
    private val filter = DecoratorFilter(ResourceBundleMessageSource())

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
    <meta charset="utf-8">
    <meta content="text/html; charset=UTF-8" http-equiv="Content-Type"/>
    <link href="https://fonts.googleapis.com/css2?family=PT+Sans&display=swap" rel="stylesheet"/>
    <meta content="width=device-width,initial-scale=1" name="viewport">
    <meta name="x-apple-disable-message-reformatting">
</head>
<body>
<table class="body" role="presentation">
    <tr>
        <td class="text-center padding-2x">
            <a href="https://www.wutsi.com/@/maison-h" style="text-decoration: none;">
                <h1>
                    <img class="border" height="64" src="https://ik.imagekit.io/cx8qxsgz4d/user/12/picture/tr:w-64,h-64,fo-face/023bb5c8-7b09-4f2f-be51-29f5c851c2c0-scaled_image_picker1721723356188894418.png"
                         style="border-radius: 32px 32px 32px 32px; vertical-align: middle; padding: 2px"
                         width="64"/>
                    <span class="margin-left">Maison K</span>
                </h1>
            </a>
        </td>
    </tr>
    <tr>
        <td>
            <div class="content">
                <p>Hello world</p>
            </div>
        </td>
    </tr>
    <tr>
        <td class="padding text-center" style="background: black; color: white">
            <div>
                Maison K
                <a class="margin-left text-smaller" href="https://www.wutsi.com/@/mason-k/unsubscribe?email&#61;yo@gmail.com" style="color: white">
                    button.unsubscribe
                </a>
            </div>
            <div class="margin-top">
                <a class="no-text-decoration" href="http://www.facebook.com/1212">
                    <img class="padding" height="48" src="https://s3.amazonaws.com/int-wutsi/assets/wutsi/img/social/facebook.png"
                         width="48"/>
                </a>
                <a class="no-text-decoration" href="http://www.twitter.com/1212">
                    <img class="padding" height="48" src="https://s3.amazonaws.com/int-wutsi/assets/wutsi/img/social/twitter.png" width="48"/>
                </a>
                <a class="no-text-decoration" href="http://www.linkedin.com/in/1212">
                    <img class="padding" height="48" src="https://s3.amazonaws.com/int-wutsi/assets/wutsi/img/social/linkedin.png"
                         width="48"/>
                </a>
            </div>
        </td>
    </tr>
    <tr>
        <td class="text-center padding">
            Powered by <a href="https://www.wutsi.com">Wutsi</a>.
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
