package com.wutsi.blog.app.page.reader

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.comment.dto.Comment
import com.wutsi.blog.comment.dto.CommentStoryCommand
import com.wutsi.blog.comment.dto.SearchCommentResponse
import com.wutsi.blog.story.dto.GetStoryResponse
import com.wutsi.blog.story.dto.Story
import com.wutsi.blog.story.dto.StoryStatus
import com.wutsi.blog.story.dto.Tag
import com.wutsi.blog.story.dto.Topic
import com.wutsi.blog.story.dto.WPPConfig
import com.wutsi.blog.user.dto.SearchUserResponse
import com.wutsi.blog.user.dto.UserSummary
import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.time.DateUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.Date

class CommentsControllerTest : SeleniumTestSupport() {
    companion object {
        const val USER_ID = 100L
        const val STORY_ID = 888L
    }

    private val story = Story(
        id = STORY_ID,
        userId = USER_ID,
        title = "Ukraine: Finalement la paix! Poutine et Zelynski font un calin",
        tagline = "Il etait temps!!!",
        content = IOUtils.toString(CommentsControllerTest::class.java.getResourceAsStream("/story.json")),
        slug = "/read/$STORY_ID/ukraine-finalement-la-paix",
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
    )

    private val users = listOf(
        UserSummary(
            id = 100,
            name = "ray.sponsible",
            blog = true,
            subscriberCount = 100,
            pictureUrl = "https://picsum.photos/200/200",
            publishStoryCount = 1L + WPPConfig.MIN_STORY_COUNT,
            creationDateTime = DateUtils.addMonths(Date(), -WPPConfig.MIN_AGE_MONTHS - 1),
        ),
        UserSummary(
            id = 200,
            name = "roger.milla",
            blog = true,
            subscriberCount = 10,
            pictureUrl = "https://picsum.photos/100/100",
            publishStoryCount = 2L + WPPConfig.MIN_STORY_COUNT,
            creationDateTime = DateUtils.addMonths(Date(), -WPPConfig.MIN_AGE_MONTHS - 2),
        ),
        UserSummary(
            id = 300,
            name = "samuel.etoo",
            blog = true,
            subscriberCount = 30,
            pictureUrl = "https://picsum.photos/128/128",
            publishStoryCount = 3L + WPPConfig.MIN_STORY_COUNT,
            creationDateTime = DateUtils.addMonths(Date(), -WPPConfig.MIN_AGE_MONTHS - 3),
        ),
        UserSummary(
            id = 555,
            name = "kylian.mbappe",
            blog = true,
            subscriberCount = 30,
            pictureUrl = "https://picsum.photos/128/128",
        ),
    )

    private val comments = listOf(
        Comment(id = 11, userId = 100, storyId = STORY_ID, text = "Comment 1"),
        Comment(id = 12, userId = 200, storyId = STORY_ID, text = "Comment 2"),
        Comment(id = 13, userId = 200, storyId = STORY_ID, text = "Comment 3"),
        Comment(id = 14, userId = 300, storyId = STORY_ID, text = "Comment 4"),
    )

    @BeforeEach
    override fun setUp() {
        super.setUp()

        setupLoggedInUser(userId = USER_ID)

        doReturn(GetStoryResponse(story)).whenever(storyBackend).get(any())
        doReturn(SearchUserResponse(users)).whenever(userBackend).search(any())
        doReturn(SearchCommentResponse(comments)).whenever(commentBackend).search(any())
    }

    @Test
    fun comment() {
        navigate("$url/comments?story-id=$STORY_ID")
        assertCurrentPageIs(PageName.COMMENT)

        input("#comment-text", "This is a comment")
        click("#btn-submit-comment", 1000)

        val cmd = argumentCaptor<CommentStoryCommand>()
        verify(commentBackend).comment(cmd.capture())
        assertEquals(STORY_ID, cmd.firstValue.storyId)
        assertEquals(USER_ID, cmd.firstValue.userId)
        assertEquals("This is a comment", cmd.firstValue.text)
    }

    @Test
    fun `load more`() {
        doReturn(
            SearchCommentResponse(
                comments = (0..CommentsController.LIMIT).map {
                    Comment(id = 11, userId = 100, storyId = STORY_ID, text = "Comment 1")
                }
            )
        ).whenever(commentBackend).search(any())

        // WHEN
        navigate("$url/comments?story-id=$STORY_ID")

        scrollToBottom()
        assertElementPresent("#btn-load-more-comments")

        doReturn(SearchCommentResponse(comments)).whenever(commentBackend).search(any())
        click("#btn-load-more-comments", 1000)
        assertElementNotPresent("#btn-load-more-comments")
    }
}
