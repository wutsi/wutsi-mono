package com.wutsi.blog.app.page.reader

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.blog.app.page.SeleniumTestSupport
import com.wutsi.blog.app.page.store.ShopControllerTest
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.kpi.dto.SearchStoryKpiRequest
import com.wutsi.blog.kpi.dto.SearchStoryKpiResponse
import com.wutsi.blog.kpi.dto.SearchUserKpiRequest
import com.wutsi.blog.kpi.dto.SearchUserKpiResponse
import com.wutsi.blog.kpi.dto.StoryKpi
import com.wutsi.blog.kpi.dto.UserKpi
import com.wutsi.blog.product.dto.Discount
import com.wutsi.blog.product.dto.DiscountType
import com.wutsi.blog.product.dto.Offer
import com.wutsi.blog.product.dto.ProductStatus
import com.wutsi.blog.product.dto.ProductSummary
import com.wutsi.blog.product.dto.SearchOfferResponse
import com.wutsi.blog.product.dto.SearchProductResponse
import com.wutsi.blog.story.dto.RecommendStoryResponse
import com.wutsi.blog.story.dto.SearchStoryResponse
import com.wutsi.blog.story.dto.StorySummary
import com.wutsi.blog.story.dto.WPPConfig
import com.wutsi.blog.subscription.dto.SearchSubscriptionResponse
import com.wutsi.blog.subscription.dto.Subscription
import com.wutsi.blog.user.dto.RecommendUserResponse
import com.wutsi.blog.user.dto.SearchUserResponse
import com.wutsi.blog.user.dto.UserSummary
import org.apache.commons.lang3.time.DateUtils
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.Date

class HomeControllerTest : SeleniumTestSupport() {
    private val storyKpis = listOf(
        StoryKpi(storyId = 100, year = 2020, month = 1, value = 100),
        StoryKpi(storyId = 200, year = 2020, month = 2, value = 110),
        StoryKpi(storyId = 300, year = 2020, month = 3, value = 5),
        StoryKpi(storyId = 400, year = 2020, month = 3, value = 120),
    )

    private val userKpis = listOf(
        UserKpi(userId = 100, year = 2020, month = 1, value = 100),
        UserKpi(userId = 200, year = 2020, month = 2, value = 110),
        UserKpi(userId = 300, year = 2020, month = 3, value = 120),
        UserKpi(userId = 555, year = 2020, month = 3, value = 120),
    )

    private val stories = listOf(
        StorySummary(
            id = 100,
            userId = 100L,
            title = "Story 1",
            thumbnailUrl = "https://picsum.photos/400/400",
            commentCount = 11,
            likeCount = 12,
            shareCount = 13,
            summary = "this is summary 100",
        ),
        StorySummary(
            id = 200,
            userId = 100L,
            title = "Story 2",
            thumbnailUrl = "https://picsum.photos/450/400",
            commentCount = 20,
            likeCount = 21,
            shareCount = 22,
            summary = "this is summary 200",
        ),
        StorySummary(
            id = 300,
            userId = 100L,
            title = "Story 3",
            thumbnailUrl = "https://picsum.photos/450/400",
            commentCount = 20,
            likeCount = 21,
            shareCount = 22,
            summary = "this is summary 300",
        ),
        StorySummary(
            id = 400,
            userId = 100L,
            title = "Story 4",
            thumbnailUrl = "https://picsum.photos/450/400",
            commentCount = 20,
            likeCount = 21,
            shareCount = 22,
            summary = "this is summary 400",
        ),
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

    private val products = listOf(
        ProductSummary(
            id = 100,
            title = "Product 100",
            imageUrl = "https://picsum.photos/1200/600",
            fileUrl = "https://www.google.ca/123.pdf",
            storeId = ShopControllerTest.STORE_ID,
            price = 1000,
            currency = "XAF",
            status = ProductStatus.PUBLISHED,
            available = true,
            slug = "/product/100/product-100",
        ),
        ProductSummary(
            id = 200,
            title = "Product 200",
            imageUrl = "https://picsum.photos/1200/600",
            fileUrl = "https://www.google.ca/123.pdf",
            storeId = ShopControllerTest.STORE_ID,
            price = 1500,
            currency = "XAF",
            status = ProductStatus.PUBLISHED,
            available = true,
            slug = "/product/200/product-200",
        )
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
        )
    )

    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(SearchStoryResponse(stories)).whenever(storyBackend).search(any())
        doReturn(RecommendStoryResponse(stories.map { it.id })).whenever(storyBackend).recommend(any())
        doReturn(SearchUserResponse(users)).whenever(userBackend).search(any())
        doReturn(RecommendUserResponse(users.map { it.id })).whenever(userBackend).recommend(any())
        doReturn(SearchStoryKpiResponse(storyKpis)).whenever(kpiBackend).search(any<SearchStoryKpiRequest>())
        doReturn(SearchUserKpiResponse(userKpis)).whenever(kpiBackend).search(any<SearchUserKpiRequest>())
        doReturn(SearchProductResponse(products)).whenever(productBackend).search(any())
        doReturn(SearchOfferResponse(offers)).whenever(offerBackend).search(any())
    }

    @Test
    fun anonymous() {
        // WHEN
        driver.get(url)
        assertCurrentPageIs(PageName.HOME)

        // THEN
        assertElementCount(".author-summary-card", 4)

        assertElementPresent("#author-summary-card-100")
        assertElementAttributeEndsWith("#author-summary-card-100 a", "href", "/@/ray.sponsible")
        assertElementAttribute("#author-summary-card-100 img", "src", "https://picsum.photos/200/200")

        assertElementPresent("#author-summary-card-200")
        assertElementAttributeEndsWith("#author-summary-card-200 a", "href", "/@/roger.milla")
        assertElementAttribute("#author-summary-card-200 img", "src", "https://picsum.photos/100/100")

        assertElementPresent("#author-summary-card-300")
        assertElementAttributeEndsWith("#author-summary-card-300 a", "href", "/@/samuel.etoo")
        assertElementAttribute("#author-summary-card-300 img", "src", "https://picsum.photos/128/128")

        assertElementPresent("#author-summary-card-555")
        assertElementAttributeEndsWith("#author-summary-card-555 a", "href", "/@/kylian.mbappe")
        assertElementAttribute("#author-summary-card-555 img", "src", "https://picsum.photos/128/128")

        assertElementAttributeEndsWith("#btn-create-hero", "href", "/create")
    }

    @Test
    fun authenticated() {
        // GIVEN
        setupLoggedInUser(111, blog = true)

        doReturn(
            SearchSubscriptionResponse(
                subscriptions = listOf(
                    Subscription(400, 111),
                ),
            ),
        ).whenever(subscriptionBackend).search(any())

        // WHEN
        driver.get(url)
        assertCurrentPageIs(PageName.HOME)

        // THEN
        assertElementCount(".story-summary-card", stories.size)
    }

    @Test
    fun `authenticated with no subscriptions fallback to trending`() {
        // GIVEN
        setupLoggedInUser(100, blog = true)

        doReturn(SearchSubscriptionResponse()).whenever(subscriptionBackend).search(any())

        // WHEN
        driver.get(url)

        // THEN
        assertElementCount(".story-summary-card", 4)
    }

    @Test
    fun `authenticated with subscription error`() {
        // GIVEN
        setupLoggedInUser(100, blog = true)

        doThrow(RuntimeException::class).whenever(subscriptionBackend).search(any())

        // WHEN
        driver.get(url)

        // THEN
        assertElementCount(".story-summary-card", 4)
    }

    @Test
    fun `authenticated with story error`() {
        // GIVEN
        setupLoggedInUser(100, blog = true)

        doThrow(RuntimeException::class).whenever(storyBackend).search(any())

        // WHEN
        driver.get(url)

        // THEN
        assertElementCount(".story-summary-card", 0)
    }
}
