package com.wutsi.blog.app.page.admin

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.story.dto.GetStoryResponse
import com.wutsi.blog.story.dto.ImportStoryResponse
import com.wutsi.blog.story.dto.Story
import com.wutsi.blog.story.dto.StoryStatus
import com.wutsi.blog.story.dto.Tag
import com.wutsi.blog.story.dto.Topic
import org.apache.commons.io.IOUtils
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.Date

class StorySyndicateControllerTest : SeleniumTestSupport() {
    companion object {
        const val BLOG_ID = 1L
    }

    private val story = Story(
        id = 111L,
        userId = BLOG_ID,
        title = "Ukraine: Finalement la paix! Poutine et Zelynski font un calin",
        tagline = "Il etait temps!!!",
        content = IOUtils.toString(PreviewControllerTest::class.java.getResourceAsStream("/story.json")),
        slug = "/read/${PreviewControllerTest.STORY_ID}/ukraine-finalement-la-paix",
        thumbnailUrl = "https://picsum.photos/1200/800",
        language = "en",
        summary = "This is the summary of the story",
        tags = listOf("Ukraine", "Russie", "Poutine", "Zelynsky", "Guerre").map { Tag(name = it) },
        creationDateTime = Date(),
        modificationDateTime = Date(),
        status = StoryStatus.DRAFT,
        topic = Topic(
            id = 100,
            name = "Topic 100",
        ),
        likeCount = 10,
        liked = false,
        commentCount = 300,
        shareCount = 3500,
    )

    @BeforeEach
    override fun setUp() {
        super.setUp()

        setupLoggedInUser(BLOG_ID, blog = true)
        doReturn(GetStoryResponse(story)).whenever(storyBackend).get(any())
        doReturn(ImportStoryResponse(story.id)).whenever(storyBackend).import(any())
    }

    @Test
    fun syndicate() {
        navigate("$url/me/syndicate")
        assertCurrentPageIs(PageName.STORY_SYNDICATE)

        input(
            "#url",
            "https://kamerkongossa.cm/2020/01/07/a-yaounde-on-rencontre-le-sous-developpement-par-les-chemins-quon-emprunte-pour-leviter/",
        )
        click("#btn-submit")

        assertCurrentPageIs(PageName.EDITOR)
        Thread.sleep(1000) // Wait for data to be fetched

        assertElementAttribute("#title", "value", story.title)
    }
}
