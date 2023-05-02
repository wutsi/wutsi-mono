package com.wutsi.blog.story

import com.wutsi.blog.client.story.SearchStoryRequest
import com.wutsi.blog.client.story.SearchStoryResponse
import com.wutsi.blog.client.story.StoryStatus
import com.wutsi.blog.story.service.StoryService
import com.wutsi.blog.util.DateUtils
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@RequestMapping("/v1/stories/published")
class PublishedStoryController(
    private val storyService: StoryService,
) {
    @GetMapping
    fun published(
        @RequestParam(name = "site-id") siteId: Long = 1L,

        @RequestParam(name = "start-date", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        startDate: LocalDate? = null,

        @RequestParam(name = "end-date", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        endDate: LocalDate? = null,

        @RequestParam limit: Int,
        @RequestParam offset: Int,
    ): SearchStoryResponse =
        storyService.search(
            request = SearchStoryRequest(
                siteId = siteId,
                status = StoryStatus.published,
                live = true,
                limit = limit,
                offset = offset,
                publishedStartDate = startDate?.let { DateUtils.toDate(startDate) },
                publishedEndDate = endDate?.let { DateUtils.toDate(endDate) },
            ),
        )
}
