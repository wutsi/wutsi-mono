package com.wutsi.blog.story.config

import com.wutsi.extractor.ContentExtractor
import com.wutsi.extractor.Downloader
import com.wutsi.extractor.ImageExtractor
import com.wutsi.extractor.PublishedDateExtractor
import com.wutsi.extractor.SiteNameExtractor
import com.wutsi.extractor.TagExtractor
import com.wutsi.extractor.TitleExtractor
import com.wutsi.extractor.URLExtractor
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class WebExtractorConfiguration {
    @Bean
    fun downloader() = Downloader()

    @Bean
    fun contentExtractor(
        @Value("\${wutsi.extractor.min-bloc-length}") min: Int
    ) = ContentExtractor.create(min)

    @Bean
    fun tagExtractor() = TagExtractor()

    @Bean
    fun titleExtractor() = TitleExtractor()

    @Bean
    fun publishedDateExtractor() = PublishedDateExtractor()

    @Bean
    fun siteNameExtractor() = SiteNameExtractor()

    @Bean
    fun urlExtractor() = URLExtractor()

    @Bean
    fun imageExtractor() = ImageExtractor()
}
