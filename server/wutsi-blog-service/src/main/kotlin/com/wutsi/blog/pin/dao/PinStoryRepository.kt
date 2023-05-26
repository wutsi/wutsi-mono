package com.wutsi.blog.pin.dao

import com.wutsi.blog.pin.domain.PinStoryEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PinStoryRepository : CrudRepository<PinStoryEntity, Long>
