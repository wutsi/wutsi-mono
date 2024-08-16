package com.wutsi.blog.ads.config

import com.wutsi.blog.ads.service.AdsFilterSet
import com.wutsi.blog.ads.service.filter.AdsCountryFilter
import com.wutsi.blog.ads.service.filter.AdsDeviceTypeFilter
import com.wutsi.blog.ads.service.filter.AdsEmailFilter
import com.wutsi.blog.ads.service.filter.AdsImpressionFilter
import com.wutsi.blog.ads.service.filter.AdsLanguageFilter
import com.wutsi.blog.ads.service.filter.AdsOSFilter
import com.wutsi.blog.ads.service.filter.PreferredCategoryAdsFilter
import com.wutsi.blog.backend.IpApiBackend
import com.wutsi.blog.user.dao.PreferredCategoryRepository
import com.wutsi.platform.core.logging.KVLogger
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AdsConfiguration(
    private val ipApi: IpApiBackend,
    private val logger: KVLogger,
    private val preferredCategoryDao: PreferredCategoryRepository,
) {
    @Bean
    fun adsFilterSet(): AdsFilterSet = AdsFilterSet(
        listOf(
            AdsOSFilter(),
            AdsEmailFilter(),
            AdsLanguageFilter(),
            AdsDeviceTypeFilter(),
            AdsCountryFilter(ipApi),
            PreferredCategoryAdsFilter(preferredCategoryDao),
            AdsImpressionFilter() // Should be the last one
        ),
        logger
    )
}
