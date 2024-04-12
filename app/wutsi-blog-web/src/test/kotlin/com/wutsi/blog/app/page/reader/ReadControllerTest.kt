package com.wutsi.blog.app.page.reader

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.atLeast
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.reset
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.ads.dto.AdsStatus
import com.wutsi.blog.ads.dto.AdsSummary
import com.wutsi.blog.ads.dto.AdsType
import com.wutsi.blog.ads.dto.SearchAdsResponse
import com.wutsi.blog.app.model.UserModel
import com.wutsi.blog.app.page.SeleniumTestSupport
import com.wutsi.blog.app.util.CookieHelper
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.comment.dto.Comment
import com.wutsi.blog.comment.dto.CommentStoryCommand
import com.wutsi.blog.comment.dto.SearchCommentResponse
import com.wutsi.blog.like.dto.LikeStoryCommand
import com.wutsi.blog.like.dto.UnlikeStoryCommand
import com.wutsi.blog.product.dto.Discount
import com.wutsi.blog.product.dto.DiscountType
import com.wutsi.blog.product.dto.GetStoreResponse
import com.wutsi.blog.product.dto.Offer
import com.wutsi.blog.product.dto.ProductSummary
import com.wutsi.blog.product.dto.SearchOfferResponse
import com.wutsi.blog.product.dto.SearchProductResponse
import com.wutsi.blog.product.dto.Store
import com.wutsi.blog.share.dto.ShareStoryCommand
import com.wutsi.blog.story.dto.GetStoryResponse
import com.wutsi.blog.story.dto.SearchStoryResponse
import com.wutsi.blog.story.dto.SearchTopicResponse
import com.wutsi.blog.story.dto.Story
import com.wutsi.blog.story.dto.StoryAccess
import com.wutsi.blog.story.dto.StoryStatus
import com.wutsi.blog.story.dto.StorySummary
import com.wutsi.blog.story.dto.Tag
import com.wutsi.blog.story.dto.Topic
import com.wutsi.blog.subscription.dto.SubscribeCommand
import com.wutsi.blog.transaction.dto.GetWalletResponse
import com.wutsi.blog.transaction.dto.SearchTransactionResponse
import com.wutsi.blog.transaction.dto.TransactionSummary
import com.wutsi.blog.transaction.dto.Wallet
import com.wutsi.blog.user.dto.GetUserResponse
import com.wutsi.blog.user.dto.SearchUserResponse
import com.wutsi.blog.user.dto.User
import com.wutsi.blog.user.dto.UserSummary
import com.wutsi.tracking.manager.dto.PushTrackRequest
import org.apache.commons.io.IOUtils
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.openqa.selenium.By
import org.openqa.selenium.Cookie
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import java.util.Date
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ReadControllerTest : SeleniumTestSupport() {
    companion object {
        const val BLOG_ID = 1L
        const val USER_ID = 100L
        const val STORY_ID = 888L
    }

    private val topics = listOf(
        Topic(id = 100, name = "art"),
        Topic(id = 101, name = "beauty", parentId = 100),
        Topic(id = 102, name = "books", parentId = 100),
    )
    private val story = Story(
        id = STORY_ID,
        userId = BLOG_ID,
        title = "Ukraine: Finalement la paix! Poutine et Zelynski font un calin",
        tagline = "Il etait temps!!!",
        content = IOUtils.toString(ReadControllerTest::class.java.getResourceAsStream("/story.json")),
        slug = "/read/$STORY_ID/ukraine-finalement-la-paix",
        thumbnailUrl = "https://picsum.photos/1200/800",
        language = "en",
        summary = "This is the summary of the story",
        tags = listOf("Ukraine", "Russie", "Poutine", "Zelynsky", "Guerre").map { Tag(name = it) },
        creationDateTime = Date(),
        modificationDateTime = Date(),
        status = StoryStatus.PUBLISHED,
        topic = topics[1],
        likeCount = 10,
        liked = false,
        commentCount = 300,
        shareCount = 3500,
    )
    private val seeAlso = listOf(
        StorySummary(
            id = 111L,
            userId = BLOG_ID,
            title = "This is the first story recommended",
            thumbnailUrl = "https://picsum.photos/1200/800",
            status = StoryStatus.PUBLISHED,
            liked = true,
            likeCount = 30L,
            commentCount = 150L,
            shareCount = 11,
            shared = false,
        ),
        StorySummary(
            id = 112L,
            userId = BLOG_ID,
            title = "This is the second story recommended",
            thumbnailUrl = null,
            status = StoryStatus.PUBLISHED,
            liked = false,
            likeCount = 1L,
            commentCount = 5L,
            commented = true,
            shareCount = 150,
            shared = true,
        ),
        StorySummary(
            id = 444L,
            userId = BLOG_ID,
            title = "This is the second story recommended",
            thumbnailUrl = null,
            status = StoryStatus.PUBLISHED,
            liked = false,
            likeCount = 1L,
            commentCount = 5L,
            commented = true,
            shareCount = 150,
            shared = true,
        ),
        StorySummary(
            id = 555L,
            userId = BLOG_ID,
            title = "This is the second story recommended",
            thumbnailUrl = null,
            status = StoryStatus.PUBLISHED,
            liked = false,
            likeCount = 1L,
            commentCount = 5L,
            commented = true,
            shareCount = 150,
            shared = true,
        ),
    )
    private val blog = User(
        id = BLOG_ID,
        blog = true,
        name = "test",
        fullName = "Test Blog",
        country = "CM"
    )
    private val users = listOf(
        UserSummary(
            id = BLOG_ID,
            fullName = "Test Blog",
            pictureUrl = "https://picsum.photos/100/100",
        ),
        UserSummary(
            id = USER_ID,
            fullName = "Ray Sponsible",
            pictureUrl = "https://picsum.photos/100/100",
        ),
    )
    private val comments = listOf(
        Comment(
            storyId = STORY_ID,
            userId = USER_ID,
            text = "Yo man",
        ),
    )
    private val products = listOf(
        ProductSummary(
            id = 100,
            title = "Product 100",
            imageUrl = "https://picsum.photos/1200/600",
            price = 1000,
            currency = "XAF",
            slug = "/product/100/product-100",
        ),
        ProductSummary(
            id = 200,
            title = "Product 200",
            imageUrl = "https://picsum.photos/1200/600",
            price = 1000,
            currency = "XAF",
            slug = "/product/200/product-200",
        ),
        ProductSummary(
            id = 300,
            title = "Product 300",
            imageUrl = "https://picsum.photos/1200/600",
            price = 500,
            currency = "XAF",
            slug = "/product/200/product-200",
        ),
        ProductSummary(
            id = 400,
            title = "Product 400",
            imageUrl = "https://picsum.photos/1200/600",
            price = 500,
            currency = "XAF",
            slug = "/product/200/product-200",
        ),
    )

    private val offers = listOf(
        Offer(
            productId = 100,
            price = 800,
            referencePrice = 1000,
            savingAmount = 200,
            savingPercentage = 20,
            discount = Discount(
                type = DiscountType.SUBSCRIBER,
                percentage = 20
            )
        ),
        Offer(
            productId = 200,
            price = 1200,
            referencePrice = 1500,
            savingAmount = 300,
            savingPercentage = 20,
            discount = Discount(
                type = DiscountType.SUBSCRIBER,
                percentage = 20
            )
        ),
        Offer(
            productId = 200,
            price = 1200,
            referencePrice = 1500,
            savingAmount = 300,
            savingPercentage = 20,
            discount = Discount(
                type = DiscountType.SUBSCRIBER,
                percentage = 20
            )
        ),
        Offer(
            productId = 200,
            price = 1200,
            referencePrice = 1500,
            savingAmount = 300,
            savingPercentage = 20,
            discount = Discount(
                type = DiscountType.SUBSCRIBER,
                percentage = 20
            )
        )
    )

    private val ads = listOf(
        AdsSummary(
            id = "1111",
            imageUrl = "https://picsum.photos/800/600",
            type = AdsType.BANNER_WEB,
            url = "https://www.google.com",
            title = "Title 1111",
            status = AdsStatus.RUNNING,
        ),
        AdsSummary(
            id = "2222",
            imageUrl = "https://picsum.photos/300/600",
            type = AdsType.BOX_2X,
            url = "https://www.google.com",
            title = "Title 2222",
            status = AdsStatus.RUNNING,
        ),
        AdsSummary(
            id = "3333",
            imageUrl = "https://picsum.photos/300/300",
            type = AdsType.BOX_2X,
            url = "https://www.google.com",
            title = "Title 3333",
            status = AdsStatus.RUNNING,
        ),
    )

    private fun addPresubscribeCookie(blog: User) {
        driver.get(url(url))

        driver.manage().addCookie(Cookie(CookieHelper.preSubscribeKey(UserModel(id = blog.id)), "1"))
        driver.manage().addCookie(Cookie(CookieHelper.donateKey(UserModel(id = blog.id)), "1"))
    }

    private fun removePresubscribeCookie(blog: User) {
        val key = CookieHelper.preSubscribeKey(UserModel(id = blog.id))
        driver.manage().deleteCookie(Cookie(key, "1"))
    }

    private fun removeDonationCookie(blog: User) {
        val key = CookieHelper.donateKey(UserModel(id = blog.id))
        driver.manage().deleteCookie(Cookie(key, "1"))
    }

    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(GetUserResponse(blog)).whenever(userBackend).get(BLOG_ID)
        doReturn(GetUserResponse(blog)).whenever(userBackend).get(blog.name)

        doReturn(SearchTopicResponse(topics)).whenever(topicBackend).all()

        doReturn(GetStoryResponse(story)).whenever(storyBackend).get(any())
        doReturn(SearchStoryResponse(seeAlso)).whenever(storyBackend).search(any())

        doReturn(SearchUserResponse(users)).whenever(userBackend).search(any())

        doReturn(SearchCommentResponse(comments)).whenever(commentBackend).search(any())

        addPresubscribeCookie(blog)
    }

    @Test
    fun anonymous() {
        // WHEN
        navigate("$url/read/$STORY_ID")
        assertCurrentPageIs(PageName.READ)

        // THEN
        // Header
        assertElementAttribute("html", "lang", story.language)
        assertElementAttribute("head title", "text", "${story.title} | ${blog.fullName} | Wutsi")
        assertElementAttribute("head meta[name='description']", "content", story.summary)
        assertElementAttribute("head meta[name='robots']", "content", "index,follow")

        val tags = "Ukraine,Russie,Poutine,Zelynsky,Guerre,Art,Beauty"
        assertElementAttribute("head meta[name='keywords']", "content", tags)

        // OpenGraph
        assertElementAttribute("head meta[property='og:title']", "content", story.title)
        assertElementAttribute("head meta[property='og:description']", "content", story.summary)
        assertElementAttribute("head meta[property='og:type']", "content", "article")
        assertElementAttributeEndsWith("head meta[property='og:url']", "content", story.slug)
        assertElementAttribute("head meta[property='og:image']", "content", story.thumbnailUrl)
        assertElementAttribute("head meta[property='og:site_name']", "content", "Wutsi")
        assertElementAttribute("head meta[property='article:author']", "content", blog.fullName)
        assertElementCount("head meta[property='article:tag']", tags.split(",").size)

        // Wutsi
        assertElementAttribute("head meta[name='wutsi:story_id']", "content", story.id.toString())
        assertElementPresent("head meta[name='wutsi:hit_id']")

        // Facebook
        assertElementAttribute("head meta[name='facebook:app_id']", "content", "629340480740249")
        assertElementPresent("script#fb-pixel-code")

        // Google Analytics
        assertElementPresent("script#ga-code")

        // Social action
        assertElementPresent("#like-widget-$STORY_ID")
        assertElementPresent("#comment-widget-$STORY_ID")
        assertElementPresent("#share-widget-$STORY_ID")

        // Tracking
        val track = argumentCaptor<PushTrackRequest>()
        verify(trackingBackend).push(track.capture())
        assertEquals(STORY_ID.toString(), track.firstValue.productId.toString())
        assertEquals(PageName.READ, track.firstValue.page)
        assertEquals(
            driver.findElement(By.cssSelector("head meta[name='wutsi:hit_id")).getAttribute("content"),
            track.firstValue.correlationId,
        )
        assertNull(track.firstValue.accountId)
        assertEquals("readstart", track.firstValue.event)

        // Recommendations
        scrollToBottom()
        Thread.sleep(1000)
        assertElementCount("#recommendation-container .story-summary-card", seeAlso.size)
    }

    @Test
    fun loggedIn() {
        // GIVEN
        setupLoggedInUser(100, blog = false, walletId = null)

        // WHEN
        navigate("$url/read/$STORY_ID")
        assertCurrentPageIs(PageName.READ)

        // THEN
        // Header
        assertElementAttribute("html", "lang", story.language)
        assertElementAttribute("head title", "text", "${story.title} | ${blog.fullName} | Wutsi")
        assertElementAttribute("head meta[name='description']", "content", story.summary)
        assertElementAttribute("head meta[name='robots']", "content", "index,follow")

        val tags = "Ukraine,Russie,Poutine,Zelynsky,Guerre,Art,Beauty"
        assertElementAttribute("head meta[name='keywords']", "content", tags)

        // OpenGraph
        assertElementAttribute("head meta[property='og:title']", "content", story.title)
        assertElementAttribute("head meta[property='og:description']", "content", story.summary)
        assertElementAttribute("head meta[property='og:type']", "content", "article")
        assertElementAttributeEndsWith("head meta[property='og:url']", "content", story.slug)
        assertElementAttribute("head meta[property='og:image']", "content", story.thumbnailUrl)
        assertElementAttribute("head meta[property='og:site_name']", "content", "Wutsi")
        assertElementAttribute("head meta[property='article:author']", "content", blog.fullName)
        assertElementCount("head meta[property='article:tag']", tags.split(",").size)

        // Wutsi
        assertElementAttribute("head meta[name='wutsi:story_id']", "content", story.id.toString())
        assertElementPresent("head meta[name='wutsi:hit_id']")

        // Facebook
        assertElementAttribute("head meta[name='facebook:app_id']", "content", "629340480740249")
        assertElementPresent("script#fb-pixel-code")

        // Google Analytics
        assertElementPresent("script#ga-code")

        // Social action
        assertElementPresent("#like-widget-$STORY_ID")
        assertElementPresent("#comment-widget-$STORY_ID")
        assertElementPresent("#share-widget-$STORY_ID")

        // Tracking
        val track = argumentCaptor<PushTrackRequest>()
        verify(trackingBackend).push(track.capture())
        assertEquals(STORY_ID.toString(), track.firstValue.productId.toString())
        assertEquals(PageName.READ, track.firstValue.page)
        assertEquals(
            driver.findElement(By.cssSelector("head meta[name='wutsi:hit_id")).getAttribute("content"),
            track.firstValue.correlationId,
        )
        assertEquals("100", track.firstValue.accountId)
        assertEquals("readstart", track.firstValue.event)

        // Recommendations
        scrollToBottom()
        Thread.sleep(1000)
        assertElementCount("#recommendation-container .story-summary-card", seeAlso.size)
    }

    @Test
    fun notPublished() {
        // GIVEN
        doReturn(GetStoryResponse(story.copy(status = StoryStatus.DRAFT))).whenever(storyBackend).get(any())

        // WHEN
        navigate("$url/read/333")
        assertCurrentPageIs(PageName.STORY_NOT_FOUND)
    }

    @Test
    fun notFound() {
        // GIVEN
        val ex = HttpClientErrorException(HttpStatus.NOT_FOUND)
        doThrow(ex).whenever(storyBackend).get(any())

        // WHEN
        navigate("$url/read/99999")
        assertCurrentPageIs(PageName.STORY_NOT_FOUND)
    }

    @Test
    fun likeOnLoad() {
        // GIVEN
        navigate("$url/read/$STORY_ID?like=1&like-key=540540950_$STORY_ID")
        assertCurrentPageIs(PageName.READ)

        // THEN
        val like = argumentCaptor<LikeStoryCommand>()
        verify(likeBackend).like(like.capture())
        assertEquals(STORY_ID, like.firstValue.storyId)
    }

    @Test
    fun likeAStory() {
        // WHEN
        navigate("$url${story.slug}")
        scrollToBottom()
        click("#like-widget-$STORY_ID a", 1000)

        // THEN
        val command = argumentCaptor<LikeStoryCommand>()
        verify(likeBackend).like(command.capture())
        assertEquals(STORY_ID, command.firstValue.storyId)
        assertNull(command.firstValue.userId)
        assertNotNull(command.firstValue.deviceId)
    }

    @Test
    fun unlikeAsStory() {
        // GIVEN
        setupLoggedInUser(USER_ID)
        doReturn(GetStoryResponse(story.copy(liked = true))).whenever(storyBackend).get(any())

        // THEN
        navigate("$url${story.slug}")
        scrollToBottom()
        click("#like-widget-$STORY_ID a", 1000)

        // THEN
        val command = argumentCaptor<UnlikeStoryCommand>()
        verify(likeBackend).unlike(command.capture())
        assertEquals(STORY_ID, command.firstValue.storyId)
        assertEquals(USER_ID, command.firstValue.userId)
        assertNotNull(command.firstValue.deviceId)
    }

    @Test
    fun shareOnLoad() {
        // GIVEN
        navigate("$url/read/$STORY_ID?share=1")
        assertCurrentPageIs(PageName.READ)

        // THEN
        Thread.sleep(5000) // The delay is very flaky
        assertElementVisible("#share-modal")
    }

    @Test
    fun shareToFacebook() {
        // THEN
        navigate("$url${story.slug}")
        scrollToBottom()
        click("#share-widget-$STORY_ID a")

        // THEN
        assertElementVisible("#share-modal")
        Thread.sleep(1000)
        click("#share-modal a[data-target=facebook]")
        Thread.sleep(1000)

        val command = argumentCaptor<ShareStoryCommand>()
        verify(shareBackend).share(command.capture())
        assertEquals(STORY_ID, command.firstValue.storyId)
        assertNull(command.firstValue.userId)
    }

    @Test
    fun shareToTwitter() {
        // GIVEN
        setupLoggedInUser(USER_ID)
        doReturn(GetStoryResponse(story.copy(liked = true))).whenever(storyBackend).get(any())

        // THEN
        navigate("$url${story.slug}")
        scrollToBottom()
        click("#share-widget-$STORY_ID a")

        // THEN
        assertElementVisible("#share-modal")
        Thread.sleep(1000)
        click("#share-modal a[data-target=twitter]")
        Thread.sleep(1000)

        val command = argumentCaptor<ShareStoryCommand>()
        verify(shareBackend).share(command.capture())
        assertEquals(STORY_ID, command.firstValue.storyId)
        assertEquals(USER_ID, command.firstValue.userId)
        assertNotNull(command.firstValue.userId)
    }

    @Test
    fun shareToLinkedin() {
        // THEN
        navigate("$url${story.slug}")
        scrollToBottom()
        click("#share-widget-$STORY_ID a")

        // THEN
        assertElementVisible("#share-modal")
        Thread.sleep(1000)
        click("#share-modal a[data-target=linkedin]")
        Thread.sleep(1000)

        val command = argumentCaptor<ShareStoryCommand>()
        verify(shareBackend).share(command.capture())
        assertEquals(STORY_ID, command.firstValue.storyId)
        assertNull(command.firstValue.userId)
    }

    @Test
    fun shareToReddit() {
        // THEN
        navigate("$url${story.slug}")
        scrollToBottom()
        click("#share-widget-$STORY_ID a", 1000)

        // THEN
        assertElementVisible("#share-modal")
        click("#share-modal a[data-target=reddit]", 1000)

        val command = argumentCaptor<ShareStoryCommand>()
        verify(shareBackend).share(command.capture())
        assertEquals(STORY_ID, command.firstValue.storyId)
        assertNull(command.firstValue.userId)
    }

    @Test
    fun anonymousCannotComment() {
        // THEN
        navigate("$url${story.slug}")
        scrollToBottom()
        click("#comment-widget-$STORY_ID a")

        // THEN
        assertCurrentPageIs(PageName.LOGIN)
    }

    @Test
    fun userCanComment() {
        // GIVEN
        setupLoggedInUser(USER_ID)
        doReturn(GetStoryResponse(story)).whenever(storyBackend).get(any())

        // THEN
        navigate("$url${story.slug}")
        scrollToBottom()
        click("#comment-widget-$STORY_ID a")

        // THEN
        assertCurrentPageIs(PageName.COMMENT)

        input("#comment-text", "This is a comment")
        click("#btn-submit-comment")
        Thread.sleep(1000)

        val command = argumentCaptor<CommentStoryCommand>()
        verify(commentBackend).comment(command.capture())
        assertEquals(STORY_ID, command.firstValue.storyId)
        assertEquals(USER_ID, command.firstValue.userId)
        assertEquals("This is a comment", command.firstValue.text)
    }

    @Test
    fun subscribe() {
        // GIVEN
        setupLoggedInUser(100, blog = false, walletId = null)

        // WHEN
        navigate("$url/read/$STORY_ID")
        assertCurrentPageIs(PageName.READ)

        // THEN
        scroll(.33)
        click(".btn-follow", 1000)
        val command = argumentCaptor<SubscribeCommand>()
        verify(subscriptionBackend).subscribe(command.capture())
        assertEquals(blog.id, command.firstValue.userId)
        assertEquals(100, command.firstValue.subscriberId)
        assertEquals(STORY_ID, command.firstValue.storyId)
        assertEquals("story", command.firstValue.referer)
    }

    @Test
    fun `subscribe popup displayed on scroll for unsubscribed user`() {
        // GIVEN
        setupLoggedInUser(100, blog = false, walletId = null)
        removePresubscribeCookie(blog)

        // WHEN
        navigate("$url/read/$STORY_ID")
        assertCurrentPageIs(PageName.READ)

        // THEN
        scrollToMiddle()
        assertElementVisible("#follow-modal")

        click("#follow-modal-close")

        val key = CookieHelper.preSubscribeKey(UserModel(id = blog.id))
        val cookie = driver.manage().getCookieNamed(key)
        assertNotNull(cookie)
        assertEquals("1", cookie.value)
    }

    @Test
    fun `subscribe popup displayed on scroll for anonymous user`() {
        // GIVEN
        removePresubscribeCookie(blog)

        // WHEN
        navigate("$url/read/$STORY_ID")
        assertCurrentPageIs(PageName.READ)

        // THEN
        scrollToMiddle()
        assertElementVisible("#follow-modal")

        click("#follow-modal-close")

        val key = CookieHelper.preSubscribeKey(UserModel(id = blog.id))
        val cookie = driver.manage().getCookieNamed(key)
        assertNotNull(cookie)
        assertEquals("1", cookie.value)
    }

    @Test
    fun `subscribe popup not present for subscribed user`() {
        // GIVEN
        val xblog = blog.copy(subscribed = true)
        doReturn(GetUserResponse(xblog)).whenever(userBackend).get(BLOG_ID)
        doReturn(GetUserResponse(xblog)).whenever(userBackend).get(blog.name)

        removePresubscribeCookie(blog)

        // WHEN
        navigate("$url/read/$STORY_ID")
        assertCurrentPageIs(PageName.READ)

        // THEN
        assertElementNotPresent("#follow-modal")

        val key = CookieHelper.preSubscribeKey(UserModel(id = blog.id))
        val cookie = driver.manage().getCookieNamed(key)
        assertNull(cookie)
    }

    @Test
    fun `subscribe popup not present when cookie set`() {
        // WHEN
        navigate("$url/read/$STORY_ID")
        assertCurrentPageIs(PageName.READ)

        // THEN
        assertElementNotPresent("#follow-modal")
    }

    @Test
    fun `subscribe popup not present for my blog`() {
        // GIVEN
        removePresubscribeCookie(blog)
        setupLoggedInUser(story.userId)

        // WHEN
        navigate("$url/read/$STORY_ID")
        assertCurrentPageIs(PageName.READ)

        // THEN
        assertElementNotPresent("#follow-modal")

        val key = CookieHelper.preSubscribeKey(UserModel(id = blog.id))
        val cookie = driver.manage().getCookieNamed(key)
        assertNull(cookie)
    }

    @Test
    fun `restricted to subscriber - anonymous`() {
        // GIVEN
        val xstory = story.copy(access = StoryAccess.SUBSCRIBER)
        doReturn(GetStoryResponse(xstory)).whenever(storyBackend).get(STORY_ID)

        // WHEN
        navigate("$url/read/$STORY_ID")
        assertCurrentPageIs(PageName.READ)

        // THEN
        assertElementPresent("#story-paywall-subscriber")
        verify(trackingBackend, never()).push(any())
    }

    @Test
    fun `restricted to subscriber - logged in`() {
        // GIVEN
        val xstory = story.copy(access = StoryAccess.SUBSCRIBER)
        doReturn(GetStoryResponse(xstory)).whenever(storyBackend).get(STORY_ID)

        setupLoggedInUser(555L)

        // WHEN
        navigate("$url/read/$STORY_ID")
        assertCurrentPageIs(PageName.READ)

        // THEN
        assertElementPresent("#story-paywall-subscriber")
        verify(trackingBackend, never()).push(any())
    }

    @Test
    fun `restricted to subscriber - subscriber`() {
        // GIVEN
        val xstory = story.copy(access = StoryAccess.SUBSCRIBER)
        doReturn(GetStoryResponse(xstory)).whenever(storyBackend).get(STORY_ID)

        val xblog = blog.copy(subscribed = true)
        doReturn(GetUserResponse(xblog)).whenever(userBackend).get(BLOG_ID)
        doReturn(GetUserResponse(xblog)).whenever(userBackend).get(blog.name)

        setupLoggedInUser(11111)

        // WHEN
        navigate("$url/read/$STORY_ID")
        assertCurrentPageIs(PageName.READ)

        // THEN
        assertElementNotPresent("#story-paywall-subscriber")
    }

    @Test
    fun `restricted to subscriber - mine`() {
        // GIVEN
        val xstory = story.copy(access = StoryAccess.SUBSCRIBER)
        doReturn(GetStoryResponse(xstory)).whenever(storyBackend).get(STORY_ID)

        setupLoggedInUser(xstory.userId)

        // WHEN
        navigate("$url/read/$STORY_ID")
        assertCurrentPageIs(PageName.READ)

        // THEN
        assertElementNotPresent("#story-paywall-subscriber")
    }

    @Test
    fun `restricted to donor - anonymous`() {
        // GIVEN
        val xblog = blog.copy(walletId = "wallet-id")
        doReturn(GetUserResponse(xblog)).whenever(userBackend).get(blog.name)
        doReturn(GetUserResponse(xblog)).whenever(userBackend).get(blog.id)

        val xstory = story.copy(access = StoryAccess.DONOR)
        doReturn(GetStoryResponse(xstory)).whenever(storyBackend).get(STORY_ID)

        // WHEN
        navigate("$url/read/$STORY_ID")
        assertCurrentPageIs(PageName.READ)

        // THEN
        assertElementPresent("#story-paywall-donor")
        verify(trackingBackend, never()).push(any())
    }

    @Test
    fun `restricted to donor - logged in`() {
        // GIVEN
        val xblog = blog.copy(walletId = "wallet-id")
        doReturn(GetUserResponse(xblog)).whenever(userBackend).get(blog.name)
        doReturn(GetUserResponse(xblog)).whenever(userBackend).get(blog.id)

        val xstory = story.copy(access = StoryAccess.DONOR)
        doReturn(GetStoryResponse(xstory)).whenever(storyBackend).get(STORY_ID)

        setupLoggedInUser(555L)

        // WHEN
        navigate("$url/read/$STORY_ID")
        assertCurrentPageIs(PageName.READ)

        // THEN
        assertElementPresent("#story-paywall-donor")
        verify(trackingBackend, never()).push(any())
    }

    @Test
    fun `restricted to donor - donor`() {
        // GIVEN
        val xblog = blog.copy(walletId = "wallet-id")
        doReturn(GetUserResponse(xblog)).whenever(userBackend).get(blog.name)
        doReturn(GetUserResponse(xblog)).whenever(userBackend).get(blog.id)

        val xstory = story.copy(access = StoryAccess.DONOR)
        doReturn(GetStoryResponse(xstory)).whenever(storyBackend).get(STORY_ID)

        val wallet = Wallet(id = xblog.walletId!!, userId = xblog.id, country = "cm")
        doReturn(GetWalletResponse(wallet)).whenever(walletBackend).get(any())

        val user = UserSummary(id = xblog.id)
        doReturn(SearchUserResponse(listOf(user))).whenever(userBackend).search(any())

        val tx = TransactionSummary(walletId = xblog.walletId!!)
        doReturn(SearchTransactionResponse(listOf(tx))).whenever(transactionBackend).search(any())

        setupLoggedInUser(555L)

        // WHEN
        navigate("$url/read/$STORY_ID")
        assertCurrentPageIs(PageName.READ)

        // THEN
        assertElementNotPresent("#story-paywall-donor")
    }

    @Test
    fun `read with products`() {
        // GIVEN
        setupBLogWithProducts()

        // WHEN
        navigate("$url/read/$STORY_ID")
        assertCurrentPageIs(PageName.READ)

        scrollToMiddle()
        Thread.sleep(5000)
        assertElementCount("#shop-panel .product-summary-card", 3)
        assertElementCount("#product-summary-ads-100", 1)
    }

    @Test
    fun `donate popup displayed on scroll for anonymous`() {
        // GIVEN
        removeDonationCookie(blog)
        setupBLogWithProducts()

        // WHEN
        navigate("$url/read/$STORY_ID")
        assertCurrentPageIs(PageName.READ)

        // THEN
        scrollToMiddle()
        assertElementVisible("#donate-modal")

        click("#donate-modal-close")
        assertElementNotPresent("#product-summary-ads-100")

        val key = CookieHelper.donateKey(UserModel(id = blog.id))
        val cookie = driver.manage().getCookieNamed(key)
        assertEquals("1", cookie?.value)
    }

    @Test
    fun `donate popup displayed on scroll for logged in user`() {
        // GIVEN
        setupLoggedInUser(777)

        removeDonationCookie(blog)
        setupBLogWithProducts()

        // WHEN
        navigate("$url/read/$STORY_ID")
        assertCurrentPageIs(PageName.READ)

        // THEN
        scrollToMiddle()
        assertElementVisible("#donate-modal")

        click("#donate-modal-close")
        assertElementNotPresent("#product-summary-ads-100")

        val key = CookieHelper.donateKey(UserModel(id = blog.id))
        val cookie = driver.manage().getCookieNamed(key)
        assertEquals("1", cookie?.value)
    }

    @Test
    fun `show ads`() {
        // GIVEN
        val user = setupLoggedInUser(777)
        addPresubscribeCookie(blog)

        doReturn(SearchAdsResponse(listOf(ads[0])))
            .doReturn(SearchAdsResponse(listOf(ads[1])))
            .doReturn(SearchAdsResponse(listOf(ads[2])))
            .whenever(adsBackend).search(any())

        // WHEN
        navigate("$url/read/$STORY_ID")
        assertCurrentPageIs(PageName.READ)

        // THEN
        Thread.sleep(5000)
        assertElementPresent("#ads-container-sidebar .ads-container")
        assertElementPresent("#ads-container-navbar .ads-container")
        assertElementNotPresent("#ads-container-read-also-2 .ads-container")
        assertElementCount(".reader .ad", 3)

        val track = argumentCaptor<PushTrackRequest>()
        verify(trackingBackend, times(4)).push(track.capture()) // Including read event
        track.allValues
            .filter { item -> item.event == "impression" }
            .forEach { item ->
                assertEquals(STORY_ID.toString(), item.productId)
                assertEquals(PageName.READ, item.page)
                assertEquals(story.userId.toString(), item.businessId)
                assertEquals(user.id.toString(), item.accountId)
                assertEquals("impression", item.event)
                assertTrue(ads.map { it.id }.contains((item.campaign)))
            }

        // CLICK
        reset(trackingBackend)
        scrollToMiddle()
        click(".reader .ad", 5000)

        val track2 = argumentCaptor<PushTrackRequest>()
        verify(trackingBackend, atLeast(1)).push(track2.capture())

        val item = track2.allValues.find { it.event == "click" }
        assertNotNull(item)
        assertEquals(STORY_ID.toString(), item.productId)
        assertEquals(PageName.READ, item.page)
        assertEquals(story.userId.toString(), item.businessId)
        assertEquals(user.id.toString(), item.accountId)
        assertEquals("click", item.event)
        assertTrue(ads.map { it.id }.contains((item.campaign)))
    }

    private fun setupBLogWithProducts() {
        val xblog = blog.copy(walletId = "wallet-id", storeId = "store-id")
        doReturn(GetUserResponse(xblog)).whenever(userBackend).get(xblog.id)
        doReturn(GetUserResponse(xblog)).whenever(userBackend).get(xblog.name)

        doReturn(
            GetWalletResponse(
                Wallet(
                    id = xblog.walletId ?: "",
                    balance = 150000,
                    currency = "XAF",
                    country = "CM",
                    userId = xblog.id,
                    donationCount = 5,
                )
            )
        ).whenever(walletBackend).get(any())

        doReturn(
            GetStoreResponse(
                Store(
                    id = xblog.storeId ?: "",
                    userId = xblog.id,
                    currency = "XAF",
                    totalSales = 50000,
                    orderCount = 3,
                    enableDonationDiscount = true,
                    publishProductCount = 11,
                )
            )
        ).whenever(storeBackend).get(any())

        doReturn(
            SearchProductResponse(products)
        ).whenever(productBackend).search(any())

        doReturn(
            SearchOfferResponse(offers)
        ).whenever(offerBackend).search(any())
    }
}
