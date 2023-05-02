package com.wutsi.blog.like.dao

import com.wutsi.blog.account.domain.User
import com.wutsi.blog.like.domain.Like
import com.wutsi.blog.story.domain.Story
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface LikeRepository : CrudRepository<Like, Long> {
    fun findByUserAndStory(user: User, story: Story): Like
}
