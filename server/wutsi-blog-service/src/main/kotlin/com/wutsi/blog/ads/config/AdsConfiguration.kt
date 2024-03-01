package com.wutsi.blog.ads.config

import com.wutsi.blog.ads.service.AdsFilterSet
import com.wutsi.blog.ads.service.filter.AdsExcludeOwnerFilter
import com.wutsi.blog.ads.service.filter.AdsImpressionFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AdsConfiguration {
    @Bean
    fun adsFilterSet(): AdsFilterSet {
        return AdsFilterSet(
            listOf(
                AdsExcludeOwnerFilter(),
                AdsImpressionFilter() // Should be the last one
            )
        )
    }
}
