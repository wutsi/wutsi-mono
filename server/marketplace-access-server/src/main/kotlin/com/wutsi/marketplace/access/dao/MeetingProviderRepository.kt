package com.wutsi.marketplace.access.dao

import com.wutsi.marketplace.access.entity.MeetingProviderEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface MeetingProviderRepository : CrudRepository<MeetingProviderEntity, Long>
