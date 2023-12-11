package com.wutsi.blog.app.page.reader.view

import com.wutsi.blog.SortOrder
import com.wutsi.blog.app.backend.StoryBackend
import com.wutsi.blog.app.backend.UserBackend
import com.wutsi.blog.app.mapper.SitemapMapper
import com.wutsi.blog.app.model.SitemapModel
import com.wutsi.blog.app.model.UrlModel
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
        val users = getUsers()
        val stories = getStories(users.map { it.id })
        logger.add("user_count", users.size)
        logger.add("story_count", stories.size)

        val urls = mutableListOf<UrlModel>()
        urls.addAll(pageUrls())
        urls.addAll(users.map { mapper.toUrlModel(it) })
        urls.addAll(stories.map { mapper.toUrlModel(it) })

        return SitemapModel(
            url = urls,
        )
    }

    private fun pageUrls(): List<UrlModel> {
        val urls = mutableListOf(
            mapper.toUrlModel("/"),
            mapper.toUrlModel("/create"),
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
}
