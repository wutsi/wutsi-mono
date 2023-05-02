package com.wutsi.blog.story

import com.wutsi.blog.ResourceHelper.loadResourceAsString
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/blog")
class BlogController {

    @GetMapping(produces = ["text/html"])
    fun get(): String {
        return loadResourceAsString("/story.html")
    }

    @GetMapping("/404", produces = ["text/html"])
    fun get404(): ResponseEntity<String> {
        return ResponseEntity.notFound().build()
    }
}
