package com.wutsi.blog.ads.config

import com.wutsi.blog.ads.service.AdsFilterSet
import com.wutsi.blog.ads.service.filter.AdsCountryFilter
import com.wutsi.blog.ads.service.filter.AdsDeviceTypeFilter
import com.wutsi.blog.ads.service.filter.AdsEmailFilter
import com.wutsi.blog.ads.service.filter.AdsImpressionFilter
import com.wutsi.blog.ads.service.filter.AdsLanguageFilter
import com.wutsi.blog.backend.IpApiBackend
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AdsConfiguration(private val ipApi: IpApiBackend) {
    @Bean
    fun adsFilterSet(): AdsFilterSet {
        return AdsFilterSet(
            listOf(
                AdsCountryFilter(ipApi),
                AdsLanguageFilter(),
                AdsDeviceTypeFilter(),
                AdsEmailFilter(),
                AdsImpressionFilter() // Should be the last one
            )
        )
    }
}
