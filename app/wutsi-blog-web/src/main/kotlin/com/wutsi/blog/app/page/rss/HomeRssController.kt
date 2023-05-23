package com.wutsi.blog.app.page.rss

import com.wutsi.blog.app.page.rss.view.StoryRssView
import com.wutsi.blog.app.page.story.service.StoryService
import org.springframework.beans.factory.annotation.Value
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.Date

@RestController
class HomeRssController(
    private val storyService: StoryService,
    @Value("\${wutsi.application.server-url}") private val baseUrl: String,
) {
    @GetMapping("/rss")
    fun index(
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") startDate: Date? = null,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") endDate: Date? = null,
    ): StoryRssView =
        StoryRssView(
            baseUrl = baseUrl,
            endDate = endDate,
            startDate = startDate,
            storyService = storyService,
        )
}
