package com.wutsi.blog.app.page.admin

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.SeleniumTestSupport
import com.wutsi.blog.app.page.reader.ReadControllerTest
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.product.dto.Category
import com.wutsi.blog.product.dto.ProductStatus
import com.wutsi.blog.product.dto.ProductSummary
import com.wutsi.blog.product.dto.SearchCategoryResponse
import com.wutsi.blog.product.dto.SearchProductResponse
import com.wutsi.blog.story.dto.CreateStoryResponse
import com.wutsi.blog.story.dto.GetStoryReadabilityResponse
import com.wutsi.blog.story.dto.GetStoryResponse
import com.wutsi.blog.story.dto.PublishStoryCommand
import com.wutsi.blog.story.dto.SearchTagResponse
import com.wutsi.blog.story.dto.SearchTopicResponse
import com.wutsi.blog.story.dto.Story
import com.wutsi.blog.story.dto.StoryStatus
import com.wutsi.blog.story.dto.Tag
import com.wutsi.blog.story.dto.Topic
import com.wutsi.blog.story.dto.ValidateStoryWPPEligibilityResponse
import com.wutsi.blog.story.dto.WPPValidation
import com.wutsi.blog.user.dto.Readability
import org.apache.commons.io.IOUtils
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.Date
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class EditorControllerTest : SeleniumTestSupport() {
    companion object {
        const val STORY_ID = 100L
        const val BLOG_ID = 1000L
        const val STORE_ID = "111"
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
        status = StoryStatus.DRAFT,
        topic = Topic(id = 100, name = "Topic 100"),
        category = Category(id = 101, parentId = 1, title = "moo", longTitle = "foor > moo"),
        likeCount = 10,
        liked = false,
        commentCount = 300,
        shareCount = 3500,
    )

    private val tags = listOf(
        Tag(id = 1, name = "topic1", displayName = "Topic 1"),
        Tag(id = 2, name = "topic2", displayName = "Topic 2"),
        Tag(id = 3, name = "topic3", displayName = "Topic 3"),
        Tag(id = 4, name = "topic4", displayName = "Topic 4"),
    )

    private val topics = listOf(
        Topic(id = 1, name = "foo"),
        Topic(id = 100, parentId = 1, name = "bar"),
        Topic(id = 101, parentId = 1, name = "moo"),
    )

    private val categories = listOf(
        Category(id = 1, title = "foo", longTitle = "foo"),
        Category(id = 100, parentId = 1, title = "bar", longTitle = "foo > bar"),
        Category(id = 101, parentId = 1, title = "moo", longTitle = "foor > moo"),
    )

    private val readability = Readability(
        score = 90,
        scoreThreshold = 50,
    )

    private val wppValidation = WPPValidation(
        blogAgeRule = true,
        subscriptionRule = false,
    )

    private val products = listOf(
        ProductSummary(
            id = 100,
            title = "Product 100",
            imageUrl = "https://picsum.photos/1200/600",
            fileUrl = "https://www.google.ca/123.pdf",
            storeId = STORE_ID,
            price = 1000,
            currency = "XAF",
            status = ProductStatus.PUBLISHED,
            available = true,
            slug = "/product/100/product-100",
            categoryId = 110,
        ),
        ProductSummary(
            id = 200,
            title = "Product 200",
            imageUrl = "https://picsum.photos/1200/600",
            fileUrl = "https://www.google.ca/123.pdf",
            storeId = STORE_ID,
            price = 1000,
            currency = "XAF",
            status = ProductStatus.PUBLISHED,
            available = true,
            slug = "/product/200/product-200",
            categoryId = 120,
        )
    )

    @BeforeEach
    override fun setUp() {
        super.setUp()

        setupLoggedInUser(BLOG_ID)

        doReturn(CreateStoryResponse(STORY_ID)).whenever(storyBackend).create(any())
        doReturn(GetStoryReadabilityResponse(readability)).whenever(storyBackend).readability(any())
        doReturn(ValidateStoryWPPEligibilityResponse(wppValidation)).whenever(storyBackend)
            .validateWPPEligibility(any())
        doReturn(GetStoryResponse(story)).whenever(storyBackend).get(STORY_ID)

        doReturn(SearchTagResponse(tags)).whenever(tagBackend).search(any())

        doReturn(SearchTopicResponse(topics)).whenever(topicBackend).all()

        doReturn(SearchCategoryResponse(categories)).whenever(categoryBackend).search(any())

        doReturn(SearchProductResponse(products)).whenever(productBackend).search(any())
    }

    @Test
    fun `user can create and publish new story`() {
        navigate(url("/editor"))

        assertCurrentPageIs(PageName.EDITOR)
        input("#title", "Hello world")
        click(".ce-paragraph")
        input(".ce-paragraph", "This is an example of paragraph containing multiple data...")
        click("#btn-publish", 1000)

        assertCurrentPageIs(PageName.EDITOR_READABILITY)
        click("#btn-next", 1000)

        assertCurrentPageIs(PageName.EDITOR_TAG)
        assertElementPresent("#sidebar-wpp")
        input("#title", "This is title")
        input("#tagline", "This is tagline")
        select("#category-id", 1)
        assertElementNotPresent("#product-id")
        click("#btn-publish", 1000)

        val cmd = argumentCaptor<PublishStoryCommand>()
        verify(storyBackend).publish(cmd.capture())
        assertEquals(STORY_ID, cmd.firstValue.storyId)
        assertEquals(categories[0].id, cmd.firstValue.categoryId)
        assertNull(cmd.firstValue.productId)
        assertEquals("This is title", cmd.firstValue.title)
        assertEquals("This is tagline", cmd.firstValue.tagline)

        assertCurrentPageIs(PageName.EDITOR_SHARE)
    }

    @Test
    fun `user can link a product with story`() {
        setupLoggedInUser(BLOG_ID, storeId = STORE_ID, walletId = "111")

        navigate(url("/editor"))

        assertCurrentPageIs(PageName.EDITOR)
        input("#title", "Hello world")
        click(".ce-paragraph")
        input(".ce-paragraph", "This is an example of paragraph containing multiple data...")
        click("#btn-publish", 1000)

        assertCurrentPageIs(PageName.EDITOR_READABILITY)
        click("#btn-next", 1000)

        assertCurrentPageIs(PageName.EDITOR_TAG)
        assertElementPresent("#sidebar-wpp")
        input("#title", "This is title")
        input("#tagline", "This is tagline")
        select("#category-id", 1)
        select("#product-id", 1)
        click("#btn-publish", 1000)

        val cmd = argumentCaptor<PublishStoryCommand>()
        verify(storyBackend).publish(cmd.capture())
        assertEquals(STORY_ID, cmd.firstValue.storyId)
        assertEquals(categories[0].id, cmd.firstValue.categoryId)
        assertEquals(products[0].id, cmd.firstValue.productId)
        assertEquals("This is title", cmd.firstValue.title)
        assertEquals("This is tagline", cmd.firstValue.tagline)

        assertCurrentPageIs(PageName.EDITOR_SHARE)
    }

    @Test
    fun `user can edit and close story`() {
        navigate(url("/editor/$STORY_ID"))

        assertCurrentPageIs(PageName.EDITOR)
        input("#title", "Hello world")
        click(".ce-paragraph")
        input(".ce-paragraph", "This is an example of paragraph containing multiple data...")
        click("#btn-close", 1000)

        assertCurrentPageIs(PageName.STORY_DRAFT)
    }
}
