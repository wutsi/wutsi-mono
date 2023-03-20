package com.wutsi.tracking.manager.config

import com.wutsi.enums.util.ChannelDetector
import com.wutsi.tracking.manager.dao.TrackRepository
import com.wutsi.tracking.manager.service.pipeline.Pipeline
import com.wutsi.tracking.manager.service.pipeline.filter.BotFilter
import com.wutsi.tracking.manager.service.pipeline.filter.CampaignFilter
import com.wutsi.tracking.manager.service.pipeline.filter.ChannelFilter
import com.wutsi.tracking.manager.service.pipeline.filter.DeviceTypeFilter
import com.wutsi.tracking.manager.service.pipeline.filter.PersisterFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class PipelineConfiguration(
    private val dao: TrackRepository,
) {
    @Bean
    fun pipeline() = Pipeline(
        arrayListOf(
            BotFilter(),
            DeviceTypeFilter(),
            CampaignFilter(),
            ChannelFilter(ChannelDetector()),

            // IMPORTANT: Always the last!!!
            persisterFilter(),
        ),
    )

    @Bean(destroyMethod = "destroy")
    fun persisterFilter() = PersisterFilter(dao, 10000)
}
