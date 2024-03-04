package com.wutsi.blog.ads.config

import com.wutsi.blog.ads.service.AdsFilterSet
import com.wutsi.blog.ads.service.filter.AdsDeviceTypeFilter
import com.wutsi.blog.ads.service.filter.AdsImpressionFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AdsConfiguration {
    @Bean
    fun adsFilterSet(): AdsFilterSet {
        return AdsFilterSet(
            listOf(
                AdsDeviceTypeFilter(),
                AdsImpressionFilter() // Should be the last one
            )
        )
    }
}
