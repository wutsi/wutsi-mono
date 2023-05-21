package com.wutsi.blog.like.dao

import com.wutsi.blog.like.domain.LikeStoryEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface LikeStoryRepository : CrudRepository<LikeStoryEntity, Long>
