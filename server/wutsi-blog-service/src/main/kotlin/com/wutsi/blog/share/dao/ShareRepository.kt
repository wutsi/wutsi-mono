package com.wutsi.blog.share.dao

import com.wutsi.blog.share.domain.ShareEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ShareRepository : CrudRepository<ShareEntity, Long> {
    fun findByStoryIdInAndUserId(storyId: List<Long>, userId: Long): List<ShareEntity>
    fun findByStoryId(storyId: Long): List<ShareEntity>
}
