package com.wutsi.blog.ads.config

import com.wutsi.blog.ads.service.AdsFilterSet
import com.wutsi.blog.ads.service.filter.AdsCategoryFilter
import com.wutsi.blog.ads.service.filter.AdsCountryFilter
import com.wutsi.blog.ads.service.filter.AdsDeviceTypeFilter
import com.wutsi.blog.ads.service.filter.AdsEmailFilter
import com.wutsi.blog.ads.service.filter.AdsImpressionFilter
import com.wutsi.blog.ads.service.filter.AdsLanguageFilter
import com.wutsi.blog.ads.service.filter.AdsOSFilter
import com.wutsi.blog.backend.IpApiBackend
import com.wutsi.platform.core.logging.KVLogger
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AdsConfiguration(
    private val ipApi: IpApiBackend,
    private val logger: KVLogger,
) {
    @Bean
    fun adsFilterSet(): AdsFilterSet {
        return AdsFilterSet(
            listOf(
                AdsOSFilter(),
                AdsEmailFilter(),
                AdsLanguageFilter(),
                AdsDeviceTypeFilter(),
                AdsCountryFilter(ipApi),
                AdsCategoryFilter(),
                AdsImpressionFilter() // Should be the last one
            ),
            logger
        )
    }
}
