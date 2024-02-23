package com.wutsi.blog.app.page.reader.view

import com.wutsi.blog.SortOrder
import com.wutsi.blog.app.backend.ProductBackend
import com.wutsi.blog.app.backend.StoryBackend
import com.wutsi.blog.app.backend.UserBackend
import com.wutsi.blog.app.mapper.SitemapMapper
import com.wutsi.blog.app.model.SitemapModel
import com.wutsi.blog.app.model.UrlModel
import com.wutsi.blog.product.dto.ProductSortStrategy
import com.wutsi.blog.product.dto.ProductSummary
import com.wutsi.blog.product.dto.SearchProductRequest
import com.wutsi.blog.story.dto.SearchStoryRequest
import com.wutsi.blog.story.dto.StorySortStrategy
import com.wutsi.blog.story.dto.StoryStatus
import com.wutsi.blog.story.dto.StorySummary
import com.wutsi.blog.user.dto.SearchUserRequest
import com.wutsi.blog.user.dto.UserSortStrategy
import com.wutsi.blog.user.dto.UserSummary
import com.wutsi.platform.core.logging.KVLogger
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.xml.bind.JAXBContext
import jakarta.xml.bind.Marshaller
import org.springframework.stereotype.Service
import org.springframework.web.servlet.View

@Service
class SitemapView(
    private val storyBackend: StoryBackend,
    private val userBackend: UserBackend,
    private val productBackend: ProductBackend,
    private val mapper: SitemapMapper,
    private val logger: KVLogger,
) : View {
    companion object {
        const val LIMIT = 100
    }

    override fun render(model: MutableMap<String, *>, request: HttpServletRequest, response: HttpServletResponse) {
        response.contentType = "application/xml"
        response.characterEncoding = "utf-8"

        val sitemap = get()

        val jaxbContext = JAXBContext.newInstance(SitemapModel::class.java, UrlModel::class.java)
        val marshaller = jaxbContext.createMarshaller()
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)
        marshaller.marshal(sitemap, response.outputStream)
    }

    private fun get(): SitemapModel {
        val urls = mutableListOf<UrlModel>()
        urls.addAll(pageUrls())

        val users = getUsers()
        logger.add("user_count", users.size)
        urls.addAll(users.map { user -> mapper.toUrlModel(user) })

        val stories = getStories(users.map { user -> user.id })
        logger.add("story_count", stories.size)
        urls.addAll(stories.map { story -> mapper.toUrlModel(story) })

        val products = getProducts(users.mapNotNull { user -> user.storeId })
        logger.add("product_count", stories.size)
        urls.addAll(products.map { product -> mapper.toUrlModel(product) })

        return SitemapModel(
            url = urls,
        )
    }

    private fun pageUrls(): List<UrlModel> {
        val urls = mutableListOf(
            mapper.toUrlModel("/"),
            mapper.toUrlModel("/about"),
            mapper.toUrlModel("/writers"),
            mapper.toUrlModel("/partner"),
        )
        return urls
    }

    private fun getUsers(): List<UserSummary> {
        val users = mutableListOf<UserSummary>()
        var offset = 0
        while (true) {
            val tmp = userBackend.search(
                SearchUserRequest(
                    blog = true,
                    active = true,
                    limit = LIMIT,
                    offset = offset,
                    minSubscriberCount = 10,
                    sortBy = UserSortStrategy.POPULARITY,
                    sortOrder = SortOrder.DESCENDING,
                ),
            ).users
            users.addAll(tmp)

            offset += LIMIT
            if (tmp.size < LIMIT) {
                break
            }
        }
        return users
    }

    private fun getStories(userIds: List<Long>): List<StorySummary> {
        var offset = 0
        val stories = mutableListOf<StorySummary>()
        while (true) {
            val tmp = storyBackend.search(
                SearchStoryRequest(
                    userIds = userIds,
                    status = StoryStatus.PUBLISHED,
                    limit = LIMIT,
                    offset = offset,
                    sortBy = StorySortStrategy.POPULARITY,
                    sortOrder = SortOrder.DESCENDING,
                ),
            ).stories
            stories.addAll(tmp)

            offset += LIMIT
            if (tmp.size < LIMIT) {
                break
            }
        }
        return stories
    }

    private fun getProducts(storeIds: List<String>): List<ProductSummary> {
        var offset = 0
        val products = mutableListOf<ProductSummary>()
        while (true) {
            val tmp = productBackend.search(
                SearchProductRequest(
                    storeIds = storeIds,
                    limit = LIMIT,
                    offset = offset,
                    sortBy = ProductSortStrategy.ORDER_COUNT,
                    sortOrder = SortOrder.DESCENDING,
                ),
            ).products
            products.addAll(tmp)

            offset += LIMIT
            if (tmp.size < LIMIT) {
                break
            }
        }
        return products
    }
}
