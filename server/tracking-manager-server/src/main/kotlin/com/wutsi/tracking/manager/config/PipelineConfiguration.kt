package com.wutsi.tracking.manager.config

import com.wutsi.tracking.manager.backend.IpApiBackend
import com.wutsi.tracking.manager.dao.TrackRepository
import com.wutsi.tracking.manager.service.ChannelDetector
import com.wutsi.tracking.manager.service.pipeline.Pipeline
import com.wutsi.tracking.manager.service.pipeline.filter.BotFilter
import com.wutsi.tracking.manager.service.pipeline.filter.ChannelFilter
import com.wutsi.tracking.manager.service.pipeline.filter.CountryFilter
import com.wutsi.tracking.manager.service.pipeline.filter.DeviceTypeFilter
import com.wutsi.tracking.manager.service.pipeline.filter.PersisterFilter
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class PipelineConfiguration(
    private val dao: TrackRepository,
    private val ipApiBackend: IpApiBackend,
    @Value("\${wutsi.application.backend.ip-api.enabled}") private val enabled: Boolean,
) {
    @Bean
    fun pipeline() = Pipeline(
        arrayListOf(
            BotFilter(),
            DeviceTypeFilter(),
            ChannelFilter(ChannelDetector()),
            CountryFilter(ipApiBackend, enabled),

            // IMPORTANT: Always the last!!!
            persisterFilter(),
        ),
    )

    @Bean(destroyMethod = "destroy")
    fun persisterFilter() = PersisterFilter(dao, 10000)
}
