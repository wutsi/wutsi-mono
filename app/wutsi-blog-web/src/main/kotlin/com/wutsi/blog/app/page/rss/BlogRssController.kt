package com.wutsi.blog.app.page.rss

import com.wutsi.blog.app.page.rss.view.StoryRssView
import com.wutsi.blog.app.page.settings.service.UserService
import com.wutsi.blog.app.service.StoryService
import org.springframework.beans.factory.annotation.Value
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.Date

@RestController
class BlogRssController(
    private val userService: UserService,
    private val storyService: StoryService,
    @Value("\${wutsi.application.server-url}") private val baseUrl: String,
) {
    @GetMapping("/@/{name}/rss")
    fun index(
        @PathVariable name: String,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") startDate: Date? = null,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") endDate: Date? = null,
    ): StoryRssView =
        StoryRssView(
            user = userService.get(name),
            baseUrl = baseUrl,
            endDate = endDate,
            startDate = startDate,
            storyService = storyService,
        )
}
