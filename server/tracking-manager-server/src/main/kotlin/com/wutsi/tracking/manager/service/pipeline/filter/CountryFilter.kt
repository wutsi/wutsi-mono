package com.wutsi.tracking.manager.service.pipeline.filter

import com.wutsi.tracking.manager.backend.IpApiBackend
import com.wutsi.tracking.manager.entity.TrackEntity
import com.wutsi.tracking.manager.service.pipeline.Filter
import org.slf4j.LoggerFactory

class CountryFilter(private val ipApiBackend: IpApiBackend) : Filter {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(CountryFilter::class.java)
    }

    override fun filter(track: TrackEntity): TrackEntity {
        if (track.ip != null) {
            try {
                val country = ipApiBackend.resolve(track.ip).countryCode
                return track.copy(country = country)
            } catch (ex: Exception) {
                LOGGER.warn("Unable to resolve country from ${track.ip}", ex)
            }
        }
        return track
    }
}
