package com.wutsi.blog.app.config

import com.wutsi.extractor.DescriptionExtractor
import com.wutsi.extractor.Downloader
import com.wutsi.extractor.ImageExtractor
import com.wutsi.extractor.SiteNameExtractor
import com.wutsi.extractor.TitleExtractor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class HtmlExtractorConfiguration {

    @Bean
    fun downDownloader() = Downloader()

    @Bean
    fun titleExtractor() = TitleExtractor()

    @Bean
    fun siteNameExtractor() = SiteNameExtractor()

    @Bean
    fun imageExtractor() = ImageExtractor()

    @Bean
    fun descriptionExtractor() = DescriptionExtractor()
}
