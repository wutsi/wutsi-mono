package com.wutsi.blog.share.dao

import com.wutsi.blog.share.domain.ShareStoryEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ShareStoryRepository : CrudRepository<ShareStoryEntity, Long>
