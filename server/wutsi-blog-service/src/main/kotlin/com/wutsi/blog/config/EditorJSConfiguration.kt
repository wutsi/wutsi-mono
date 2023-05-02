package com.wutsi.blog.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.editorjs.html.EJSHtmlReader
import com.wutsi.editorjs.html.EJSHtmlWriter
import com.wutsi.editorjs.html.tag.TagProvider
import com.wutsi.editorjs.json.EJSJsonReader
import com.wutsi.editorjs.json.EJSJsonWriter
import com.wutsi.editorjs.readability.ReadabilityCalculator
import com.wutsi.editorjs.readability.ReadabilityContext
import com.wutsi.editorjs.readability.rule.BulletRule
import com.wutsi.editorjs.readability.rule.ExternalSourceRule
import com.wutsi.editorjs.readability.rule.HeaderRule
import com.wutsi.editorjs.readability.rule.MinImageRule
import com.wutsi.editorjs.readability.rule.MinParagraphRule
import com.wutsi.editorjs.readability.rule.ShortParagraphRule
import com.wutsi.editorjs.readability.rule.ShortSentenceRule
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class EditorJSConfiguration(private val objectMapper: ObjectMapper) {
    @Bean
    fun htmlWriter() = EJSHtmlWriter(tagProvider())

    @Bean
    fun htmlReader() = EJSHtmlReader(tagProvider())

    @Bean
    fun jsonReader() = EJSJsonReader(objectMapper)

    @Bean
    fun jsonWriter() = EJSJsonWriter(objectMapper)

    @Bean
    fun tagProvider() = TagProvider()

    @Bean
    fun readabilityCalculator() = ReadabilityCalculator(
        rules = arrayListOf(
            BulletRule(),
            ExternalSourceRule(),
            HeaderRule(),
            MinImageRule(),
            MinParagraphRule(),
            ShortParagraphRule(),
            ShortSentenceRule(),
        ),
    )

    @Bean
    fun readabilityContext(
        @Value("\${wutsi.readability.min-paragraph-per-document}") minParagraphsPerDocument: Int,
        @Value("\${wutsi.readability.max-words-per-sentence}") maxWordsPerSentence: Int,
        @Value("\${wutsi.readability.max-sentences-per-paragraph}") maxSentencesPerParagraph: Int,
    ) = ReadabilityContext(
        minParagraphsPerDocument = minParagraphsPerDocument,
        maxWordsPerSentence = maxWordsPerSentence,
        maxSentencesPerParagraph = maxSentencesPerParagraph,
        htmlWriter = htmlWriter(),
    )
}
