package com.wutsi.blog.app.page.store

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class EPubController {
    @GetMapping("/store/reader")
    fun index(model: Model): String {
        return "store/reader"
    }
}
