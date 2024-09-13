package com.wutsi.tracking.manager.endpoint

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
public class HomeController {
    @GetMapping("/")
    fun invoke(): String =
        """
            <html>
                <body>
                    <h1>Wutsi Tracking Manager</h1>
                </body>
            </html>
        """.trimIndent()
}
