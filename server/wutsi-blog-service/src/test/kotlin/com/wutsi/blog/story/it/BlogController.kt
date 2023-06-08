package com.wutsi.blog.story

import com.wutsi.blog.ResourceHelper.loadResourceAsString
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class BlogController {

    @GetMapping("/blog", produces = ["text/html"])
    fun get(): String {
        return loadResourceAsString("/story.html")
    }

    @GetMapping("/blog/empty", produces = ["text/html"])
    fun empty(): String {
        return loadResourceAsString("/story-empty.html")
    }

    @GetMapping("/blog/404", produces = ["text/html"])
    fun get404(): ResponseEntity<String> {
        return ResponseEntity.notFound().build()
    }
}
