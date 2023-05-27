package com.wutsi.blog.comment.service

import com.wutsi.blog.client.comment.CreateCommentRequest
import com.wutsi.blog.client.comment.SearchCommentRequest
import com.wutsi.blog.client.comment.UpdateCommentRequest
import com.wutsi.blog.comment.dao.CommentRepository
import com.wutsi.blog.comment.dao.SearchCommentQueryBuilder
import com.wutsi.blog.comment.domain.Comment
import com.wutsi.blog.comment.domain.CommentCount
import com.wutsi.blog.util.Predicates
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.exception.NotFoundException
import org.springframework.stereotype.Service
import java.util.Date
import javax.persistence.EntityManager
import javax.transaction.Transactional

@Deprecated("")
@Service
class CommentServiceV0(
    private val dao: CommentRepository,
    private val em: EntityManager,
) {
    fun search(request: SearchCommentRequest): List<Comment> {
        val builder = SearchCommentQueryBuilder()
        val sql = builder.query(request)
        val params = builder.parameters(request)
        val query = em.createNativeQuery(sql, Comment::class.java)
        Predicates.setParameters(query, params)
        return query.resultList as List<Comment>
    }

    fun count(request: SearchCommentRequest): List<CommentCount> {
        val builder = SearchCommentQueryBuilder()
        val sql = builder.count(request)
        val params = builder.parameters(request)
        val query = em.createNativeQuery(sql)
        Predicates.setParameters(query, params)

        val result = query.resultList as List<Array<Any>>
        return result.map {
            CommentCount(
                storyId = it[0].toString().toLong(),
                value = it[1].toString().toLong(),
            )
        }
    }

    @Transactional
    fun create(request: CreateCommentRequest): Comment {
        val now = Date()
        val comment = Comment(
            userId = request.userId!!,
            storyId = request.storyId!!,
            text = request.text,
            modificationDateTime = now,
            creationDateTime = now,
        )
        return dao.save(comment)
    }

    @Transactional
    fun update(id: Long, request: UpdateCommentRequest): Comment {
        val comment = findById(id)
        comment.text = request.text
        comment.modificationDateTime = Date()
        return dao.save(comment)
    }

    @Transactional
    fun delete(id: Long) {
        val story = findById(id)
        dao.delete(story)
    }

    fun findById(id: Long): Comment = dao.findById(id).orElseThrow { NotFoundException(Error("comment_not_found")) }

    fun findByStoryId(storyId: Long): List<Comment> = dao.findByStoryId(storyId)
}
