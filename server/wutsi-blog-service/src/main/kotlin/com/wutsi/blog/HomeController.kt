package com.wutsi.blog

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
public class HomeController {
    @GetMapping("/")
    fun invoke(): String =
        """
            <html>
                <body>
                    <h1>Wutsi Blog Server</h1>
                </body>
            </html>
        """.trimIndent()
}
