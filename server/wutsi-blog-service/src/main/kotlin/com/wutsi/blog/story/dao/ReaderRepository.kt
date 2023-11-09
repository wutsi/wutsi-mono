package com.wutsi.blog.story.dao

import com.wutsi.blog.story.domain.ReaderEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import java.util.Optional

interface ReaderRepository : CrudRepository<ReaderEntity, Long> {
    fun findByUserIdAndStoryId(userId: Long, storyId: Long): Optional<ReaderEntity>

    @Query("SELECT COUNT(R) FROM ReaderEntity R WHERE R.storyId=?1 AND R.userId IN (SELECT S.subscriberId FROM SubscriptionEntity S WHERE S.userId=?2)")
    fun countSubscriberByStoryIdAndUserId(storyId: Long, userId: Long): Long?

    fun countByStoryIdAndEmail(storyId: Long, email: Boolean): Long?

    fun findAll(page: Pageable): List<ReaderEntity>
}
