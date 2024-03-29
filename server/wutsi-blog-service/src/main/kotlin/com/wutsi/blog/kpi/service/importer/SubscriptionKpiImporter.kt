package com.wutsi.blog.kpi.service.importer

import com.wutsi.blog.kpi.service.KpiImporter
import com.wutsi.blog.kpi.service.KpiPersister
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class SubscriptionKpiImporter(
    private val persister: KpiPersister,
) : KpiImporter {
    @Transactional
    override fun import(date: LocalDate): Long =
        persister.persistStorySubscriptions(date).toLong() +
            persister.persistUserSubscriptions(date).toLong()
}
