package com.wutsi.blog.kpi.service.importer

import com.wutsi.blog.kpi.dto.KpiType
import com.wutsi.blog.kpi.dto.TrafficSource
import com.wutsi.blog.kpi.service.KpiImporter
import com.wutsi.blog.kpi.service.KpiPersister
import com.wutsi.blog.kpi.service.TrackingStorageService
import org.slf4j.LoggerFactory
import org.springframework.transaction.annotation.Transactional
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

abstract class AbstractImporter(
    protected val storage: TrackingStorageService,
    protected val persister: KpiPersister,
) : KpiImporter {
    protected abstract fun import(date: LocalDate, file: File): Long

    protected abstract fun getFilePath(date: LocalDate): String

    @Transactional
    override fun import(date: LocalDate): Long {
        val logger = LoggerFactory.getLogger(javaClass)
        val path = getFilePath(date)
        val result = try {
            val file = downloadTrackingFile(path)
            try {
                import(date, file)
            } finally {
                file.delete()
            }
        } catch (ex: Exception) {
            logger.warn(">>> Unable to log KPIs for $date from $path", ex)
            0L
        }

        logger.info(date.format(DateTimeFormatter.ofPattern("yyyy-MM")) + " - Importing Monthly data from $path - $result imported")
        return result
    }

    protected fun aggregateUserKpis(
        date: LocalDate,
        type: KpiType,
        userIds: Collection<Long>,
        trafficSources: List<TrafficSource>
    ) {
        userIds.forEach { userId ->
            trafficSources.forEach { source ->
                try {
                    persister.persistUser(date, type, userId, source)
                } catch (ex: Exception) {
                    LoggerFactory.getLogger(javaClass)
                        .warn("Unable to store UserKPI - type=$type, user-id=$userId, source=$source", ex)
                }
            }
        }
    }

    protected fun downloadTrackingFile(path: String): File {
        val file = File.createTempFile(UUID.randomUUID().toString(), "csv")
        val out = FileOutputStream(file)
        out.use {
            storage.get(path, out)
        }
        return file
    }
}
