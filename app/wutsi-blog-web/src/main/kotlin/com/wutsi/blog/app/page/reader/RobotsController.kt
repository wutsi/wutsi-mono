package com.wutsi.blog.app.page.reader

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class RobotsController {
    @GetMapping("/robots.txt", produces = ["text/plain"])
    fun robot(): String = "robots.txt"
}
