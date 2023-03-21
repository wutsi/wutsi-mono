package com.wutsi.marketplace.access.dao

import com.wutsi.marketplace.access.entity.PictureEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PictureRepository : CrudRepository<PictureEntity, Long>
