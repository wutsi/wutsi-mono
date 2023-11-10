package com.wutsi.blog.app.page.admin

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.SeleniumTestSupport
import com.wutsi.blog.app.page.reader.ReadControllerTest
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.kpi.dto.KpiType
import com.wutsi.blog.kpi.dto.SearchStoryKpiRequest
import com.wutsi.blog.kpi.dto.SearchStoryKpiResponse
import com.wutsi.blog.kpi.dto.StoryKpi
import com.wutsi.blog.kpi.dto.TrafficSource
import com.wutsi.blog.story.dto.GetStoryResponse
import com.wutsi.blog.story.dto.Reader
import com.wutsi.blog.story.dto.SearchReaderResponse
import com.wutsi.blog.story.dto.Story
import com.wutsi.blog.story.dto.StoryStatus
import com.wutsi.blog.story.dto.Tag
import com.wutsi.blog.story.dto.Topic
import com.wutsi.blog.user.dto.SearchUserResponse
import com.wutsi.blog.user.dto.UserSummary
import org.apache.commons.io.IOUtils
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doReturn
import java.util.Date

internal class StatsStoryControllerTest : SeleniumTestSupport() {
    companion object {
        const val BLOG_ID = 1L
        const val STORY_ID = 888L
    }

    private val story = Story(
        id = STORY_ID,
        userId = BLOG_ID,
        title = "Ukraine: Finalement la paix! Poutine et Zelynski font un calin",
        tagline = "Il etait temps!!!",
        content = IOUtils.toString(ReadControllerTest::class.java.getResourceAsStream("/story.json")),
        slug = "/read/${ReadControllerTest.STORY_ID}/ukraine-finalement-la-paix",
        thumbnailUrl = "https://picsum.photos/1200/800",
        language = "en",
        summary = "This is the summary of the story",
        tags = listOf("Ukraine", "Russie", "Poutine", "Zelynsky", "Guerre").map { Tag(name = it) },
        creationDateTime = Date(),
        modificationDateTime = Date(),
        status = StoryStatus.PUBLISHED,
        topic = Topic(
            id = 100,
            name = "Topic 100",
        ),
        likeCount = 10,
        liked = false,
        commentCount = 300,
        shareCount = 3500,
        subscriberReaderCount = 20,
        recipientCount = 10,
    )

    private val kpis = listOf(
        StoryKpi(storyId = 10, type = KpiType.READ, source = TrafficSource.DIRECT, year = 2020, month = 1, value = 100),
        StoryKpi(
            storyId = 10,
            type = KpiType.READ,
            source = TrafficSource.FACEBOOK,
            year = 2020,
            month = 2,
            value = 110,
        ),
        StoryKpi(
            storyId = 10,
            type = KpiType.READ,
            source = TrafficSource.LINKEDIN,
            year = 2020,
            month = 3,
            value = 120,
        ),
    )

    private val users = listOf(
        UserSummary(id = 101, name = "yo.man", fullName = "Yo", pictureUrl = "https://picsum.photos/50/50"),
        UserSummary(id = 102, name = "user.102", fullName = "User 102", pictureUrl = "https://picsum.photos/50/50"),
        UserSummary(id = 103, name = "user.103", fullName = "User 103", pictureUrl = "https://picsum.photos/50/50"),
        UserSummary(id = 104, name = "user.103", fullName = "User 104", pictureUrl = "https://picsum.photos/50/50"),
        UserSummary(id = 105, name = "user.103", fullName = "User 104", pictureUrl = "https://picsum.photos/50/50"),
    )

    private val readers = listOf(
        Reader(storyId = 10, userId = 101, liked = true),
        Reader(storyId = 10, userId = 102, commented = true),
        Reader(storyId = 10, userId = 103, subscribed = true),
        Reader(storyId = 10, userId = 104, subscribed = true),
        Reader(storyId = 10, userId = 105, subscribed = true),
    )

    private val storyKpis = listOf(
        StoryKpi(storyId = 10, type = KpiType.READ, source = TrafficSource.EMAIL, year = 2020, month = 1, value = 100),
        StoryKpi(storyId = 10, year = 2020, source = TrafficSource.EMAIL, month = 2, value = 110),
        StoryKpi(storyId = 10, year = 2020, source = TrafficSource.EMAIL, month = 3, value = 5),
        StoryKpi(storyId = 10, type = KpiType.READ, year = 2020, month = 3, value = 120),
    )

    @Test
    fun index() {
        // GIVEN
        setupLoggedInUser(BLOG_ID)
        doReturn(GetStoryResponse(story)).whenever(storyBackend).get(any())
        doReturn(SearchStoryKpiResponse(kpis)).whenever(kpiBackend).search(any<SearchStoryKpiRequest>())
        doReturn(SearchUserResponse(users)).whenever(userBackend).search(any())
        doReturn(SearchReaderResponse(readers)).whenever(readerBackend).search(any())
        com.nhaarman.mockitokotlin2.doReturn(SearchStoryKpiResponse(storyKpis))
            .whenever(kpiBackend).search(any<SearchStoryKpiRequest>())

        // WHEN
        navigate(url("/me/stats/story?story-id=$STORY_ID"))

        // THEN
        assertCurrentPageIs(PageName.STATS_STORY)
    }
}
