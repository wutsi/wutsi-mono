package com.wutsi.blog.app.service

import com.wutsi.blog.SortOrder
import com.wutsi.blog.app.backend.AuthenticationBackend
import com.wutsi.blog.app.backend.UserBackend
import com.wutsi.blog.app.form.UserAttributeForm
import com.wutsi.blog.app.mapper.UserMapper
import com.wutsi.blog.app.model.UserModel
import com.wutsi.blog.app.page.admin.AbstractStoryListController.Companion.LIMIT
import com.wutsi.blog.kpi.dto.Dimension
import com.wutsi.blog.kpi.dto.KpiType
import com.wutsi.blog.kpi.dto.SearchUserKpiRequest
import com.wutsi.blog.story.dto.WPPConfig
import com.wutsi.blog.user.dto.CreateBlogCommand
import com.wutsi.blog.user.dto.JoinWPPCommand
import com.wutsi.blog.user.dto.RecommendUserRequest
import com.wutsi.blog.user.dto.SearchUserRequest
import com.wutsi.blog.user.dto.UpdateUserAttributeCommand
import com.wutsi.blog.user.dto.UserSortStrategy
import org.apache.commons.lang3.time.DateUtils
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.Date

@Service
class UserService(
    private val backend: UserBackend,
    private val authBackend: AuthenticationBackend,
    private val mapper: UserMapper,
    private val currentSessionHolder: CurrentSessionHolder,
    private val requestContext: RequestContext,
    private val kpiService: KpiService,
) {
    fun get(id: Long): UserModel {
        val user = backend.get(id).user
        return mapper.toUserModel(user)
    }

    fun get(name: String): UserModel {
        val user = backend.get(name).user
        return mapper.toUserModel(user)
    }

    fun joinWPP() {
        backend.joinWpp(JoinWPPCommand(userId = requestContext.currentUser()!!.id))
    }

    fun getByAccessToken(accessToken: String): UserModel {
        val session = authBackend.session(accessToken).session
        return get(session.userId)
    }

    fun search(request: SearchUserRequest): List<UserModel> {
        val users = backend.search(request).users
        return users.map { mapper.toUserModel(it) }
    }

    fun createBlog(writerId: List<Long>) {
        val user = requestContext.currentUser() ?: return

        backend.createBlog(
            CreateBlogCommand(
                userId = user.id,
                subscribeToUserIds = writerId
            ),
        )
    }

    fun updateAttribute(request: UserAttributeForm) {
        val userId = currentUserId() ?: return

        backend.updateAttribute(
            UpdateUserAttributeCommand(
                userId = userId,
                name = request.name,
                value = sanitizeAttribute(request.name, request.value),
            ),
        )
    }

    fun recommend(limit: Int): List<UserModel> {
        val userIds = backend.recommend(
            RecommendUserRequest(
                readerId = requestContext.currentUser()?.id,
                deviceId = requestContext.deviceId(),
                limit = limit,
            ),
        ).userIds
        if (userIds.isEmpty()) {
            return emptyList()
        }

        return search(
            SearchUserRequest(
                userIds = userIds,
                blog = true,
                active = true,
                limit = userIds.size,
                sortBy = UserSortStrategy.POPULARITY,
                sortOrder = SortOrder.DESCENDING,
                minPublishStoryCount = WPPConfig.MIN_STORY_COUNT,
                minCreationDateTime = DateUtils.addMonths(Date(), -WPPConfig.MIN_AGE_MONTHS)
            ),
        )
    }

    fun trending(limit: Int): List<UserModel> {
        val userIds = kpiService.search(
            SearchUserKpiRequest(
                types = listOf(KpiType.DURATION),
                dimension = Dimension.ALL,
                fromDate = LocalDate.now().minusDays(7)
            )
        ).sortedByDescending { it.value }
            .map { it.targetId }
            .toSet()
            .take(limit)

        return if (userIds.isEmpty()) {
            search(
                SearchUserRequest(
                    blog = true,
                    active = true,
                    limit = LIMIT,
                    sortBy = UserSortStrategy.POPULARITY,
                    sortOrder = SortOrder.DESCENDING,
                    minSubscriberCount = 10,
                ),
            )
        } else {
            val userMap = search(
                SearchUserRequest(
                    userIds = userIds.toList(),
                    blog = true,
                    active = true,
                    limit = userIds.size,
                    sortBy = UserSortStrategy.NONE,
                    minSubscriberCount = 10,
                ),
            ).associateBy { it.id }
            userIds.mapNotNull { userMap[it] }
        }
    }

    private fun sanitizeAttribute(name: String, value: String?): String? {
        if (value.isNullOrEmpty()) {
            return null
        }

        val xvalue = value.trim()
        return if (name == "youtube_id" || name == "facebook_id" || name == "twitter_id") {
            addPrefix(xvalue.lowercase(), "@")
        } else if (name == "github_id" || name == "linkedin_id" || name == "telegram_id") {
            xvalue.lowercase()
        } else {
            xvalue
        }
    }

    private fun addPrefix(value: String, prefix: String): String =
        if (!value.startsWith(prefix)) {
            "$prefix$value"
        } else {
            value
        }

    private fun currentUserId(): Long? =
        currentSessionHolder.session()?.userId
}
