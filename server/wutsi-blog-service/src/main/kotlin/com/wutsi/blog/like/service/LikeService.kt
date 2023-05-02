package com.wutsi.blog.like.service

import com.wutsi.blog.account.service.UserService
import com.wutsi.blog.client.like.CreateLikeRequest
import com.wutsi.blog.client.like.SearchLikeRequest
import com.wutsi.blog.like.dao.LikeRepository
import com.wutsi.blog.like.dao.SearchLikeQueryBuilder
import com.wutsi.blog.like.domain.Like
import com.wutsi.blog.like.domain.LikeCount
import com.wutsi.blog.story.service.StoryService
import com.wutsi.blog.util.Predicates
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.exception.NotFoundException
import org.springframework.stereotype.Service
import javax.persistence.EntityManager
import javax.transaction.Transactional

@Service
class LikeService(
    private val users: UserService,
    private val stories: StoryService,
    private val dao: LikeRepository,
    private val em: EntityManager,
) {
    @Transactional
    fun create(request: CreateLikeRequest, deviceId: String?): Like {
        val like = dao.save(
            Like(
                deviceId = deviceId,
                user = request.userId?.let { users.findById(it) },
                story = stories.findById(request.storyId!!),
            ),
        )
        return like
    }

    @Transactional
    fun delete(id: Long) {
        val like = findLike(id)
        dao.delete(like)
    }

    fun count(request: SearchLikeRequest): List<LikeCount> {
        val builder = SearchLikeQueryBuilder()
        val sql = builder.count(request)
        val params = builder.parameters(request)
        val query = em.createNativeQuery(sql)
        Predicates.setParameters(query, params)

        val result = query.resultList as List<Array<Any>>
        return result.map {
            LikeCount(
                storyId = toLong(it.get(0)),
                value = toLong(it[1]),
            )
        }
    }

    fun search(request: SearchLikeRequest): List<Like> {
        val builder = SearchLikeQueryBuilder()
        val sql = builder.query(request)
        val params = builder.parameters(request)
        val query = em.createNativeQuery(sql, Like::class.java)
        Predicates.setParameters(query, params)
        return query.resultList as List<Like>
    }

    fun findLike(id: Long): Like = dao.findById(id).orElseThrow { NotFoundException(Error("like_not_found")) }

    private fun toLong(value: Any) = value.toString().toLong()
}
