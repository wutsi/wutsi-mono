package com.wutsi.checkout.access.dao

import com.wutsi.checkout.access.entity.BusinessEntity
import com.wutsi.checkout.access.entity.DonationKpiEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.Date
import java.util.Optional

@Repository
interface DonationKpiRepository : CrudRepository<DonationKpiEntity, Long> {
    fun findByBusinessAndDate(
        business: BusinessEntity,
        date: Date,
    ): Optional<DonationKpiEntity>
}
