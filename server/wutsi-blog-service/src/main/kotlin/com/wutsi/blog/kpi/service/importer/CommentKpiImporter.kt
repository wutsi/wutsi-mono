package com.wutsi.blog.kpi.service.importer

import com.wutsi.blog.kpi.service.KpiImporter
import com.wutsi.blog.kpi.service.KpiPersister
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class CommentKpiImporter(private val persister: KpiPersister) : KpiImporter {
    @Transactional
    override fun import(date: LocalDate): Long =
        persister.persistStoryComment(date).toLong() +
            persister.persistUserComment(date)
}
