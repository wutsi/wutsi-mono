package com.wutsi.blog.ads.dao

import com.wutsi.blog.ads.domain.AdsEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface AdsRepository : CrudRepository<AdsEntity, String>