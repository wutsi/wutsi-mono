package com.wutsi.tracking.manager.service

import com.wutsi.platform.core.storage.StorageService
import com.wutsi.tracking.manager.dao.LegacyTrackRepository
import com.wutsi.tracking.manager.dao.TrackRepository
import com.wutsi.tracking.manager.entity.LegacyTrackEntity
import com.wutsi.tracking.manager.entity.TrackEntity
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.net.URL
import java.time.LocalDate

@Service
public class MigrationService(
    private val legacyDao: LegacyTrackRepository,
    private val dao: TrackRepository,
    private val storage: StorageService,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(MigrationService::class.java)
    }

    fun migrate(year: Int) {
        LOGGER.info("Migrating $year")
        for (month in 1..12) {
            val date = LocalDate.of(year, month, 1)
            val urls = legacyDao.getURLs(date)
            urls.forEach {
                val url = migrate(date, it)
                LOGGER.info("$year/$month - $it migrated to $url")
            }
        }
    }

    fun migrate(date: LocalDate, url: URL): URL? {
        val out = ByteArrayOutputStream()
        storage.get(url, out)
        val tracks = legacyDao.read(ByteArrayInputStream(out.toByteArray())).map { map(it) }
        return if (tracks.isNotEmpty()) {
            dao.save(tracks, date)
        } else {
            null
        }
    }

    fun map(legacy: LegacyTrackEntity) = TrackEntity(
        productId = legacy.productId,
        deviceId = legacy.deviceId,
        accountId = legacy.userId,
        referrer = legacy.referer,
        page = legacy.page,
        value = legacy.value,
        ua = legacy.userAgent,
        url = legacy.url,
        event = legacy.event,
        correlationId = legacy.hitId,
        businessId = null,
        campaign = legacy.campaign,
        channel = null,
        deviceType = legacy.device,
        lat = legacy.latitude,
        long = legacy.longitude,
        merchantId = null,
        revenue = null,
        ip = legacy.ip,
        bot = legacy.bot,
        time = legacy.time ?: -1,
        source = legacy.source,
    )
}
