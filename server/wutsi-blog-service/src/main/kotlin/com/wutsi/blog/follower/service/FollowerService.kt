package com.wutsi.blog.follower.service

import com.wutsi.blog.account.service.UserService
import com.wutsi.blog.client.follower.CreateFollowerRequest
import com.wutsi.blog.client.follower.SearchFollowerRequest
import com.wutsi.blog.follower.dao.FollowerRepository
import com.wutsi.blog.follower.dao.SearchFollowerQueryBuilder
import com.wutsi.blog.follower.domain.Follower
import com.wutsi.blog.follower.domain.FollowerCount
import com.wutsi.blog.util.Predicates
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.exception.ConflictException
import com.wutsi.platform.core.error.exception.NotFoundException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import javax.persistence.EntityManager
import javax.transaction.Transactional

@Service
class FollowerService(
    private val userService: UserService,
    private val dao: FollowerRepository,
    private val em: EntityManager,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(FollowerService::class.java)
    }

    @Transactional
    fun create(request: CreateFollowerRequest): Follower {
        if (isFollowingMyself(request.userId, request.followerUserId)) {
            throw ConflictException(Error("follow_himself"))
        }

        val follower = Follower(
            userId = request.userId!!,
            followerUserId = request.followerUserId!!,
        )
        return dao.save(follower)
    }

    @Transactional
    fun delete(id: Long): Follower {
        val follower = findFollower(id)
        dao.delete(follower)
        return follower
    }

    @Transactional
    fun autoFollow(followerUserId: Long): List<Follower> {
        val bloggers = userService.findBloggersToAutoFollow()
        return bloggers
            .map { autoFollow(followerUserId, it.id!!) }
            .filterNotNull()
    }

    fun updateFollowerCount(userId: Long) {
        val counts = count(SearchFollowerRequest(userId = userId))
        val followerCount = if (counts.isEmpty()) 0L else counts[0].value
        userService.updateFollowerCount(userId, followerCount)
    }

    fun search(request: SearchFollowerRequest): List<Follower> {
        val builder = SearchFollowerQueryBuilder()
        val sql = builder.query(request)
        val params = builder.parameters(request)
        val query = em.createNativeQuery(sql, Follower::class.java)
        Predicates.setParameters(query, params)
        return query.resultList as List<Follower>
    }

    fun findFollower(id: Long): Follower = dao
        .findById(id)
        .orElseThrow { NotFoundException(Error("follower_not_found")) }

    fun count(request: SearchFollowerRequest): List<FollowerCount> {
        val builder = SearchFollowerQueryBuilder()
        val sql = builder.count(request)
        val params = builder.parameters(request)
        val query = em.createNativeQuery(sql)
        Predicates.setParameters(query, params)

        val result = query.resultList as List<Array<Any>>
        return result.map {
            FollowerCount(
                userId = it[0].toString().toLong(),
                value = it[1].toString().toLong(),
            )
        }
    }

    private fun isFollowingMyself(userId: Long?, followerUserId: Long?): Boolean = followerUserId == userId

    private fun autoFollow(followerUserId: Long, userId: Long): Follower? {
        try {
            val follower = create(
                CreateFollowerRequest(
                    userId = userId,
                    followerUserId = followerUserId,
                ),
            )
            updateFollowerCount(userId)
            return follower
        } catch (ex: Exception) {
            LOGGER.error("User#$followerUserId cannot follow User#$userId", ex)
            return null
        }
    }
}
