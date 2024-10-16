package com.wutsi.blog.story.service

import com.wutsi.editorjs.dom.BlockType
import com.wutsi.editorjs.dom.EJSDocument
import com.wutsi.editorjs.html.EJSHtmlReader
import com.wutsi.editorjs.html.EJSHtmlWriter
import com.wutsi.editorjs.json.EJSJsonReader
import com.wutsi.editorjs.json.EJSJsonWriter
import com.wutsi.editorjs.readability.ReadabilityCalculator
import com.wutsi.editorjs.readability.ReadabilityContext
import com.wutsi.editorjs.readability.ReadabilityResult
import com.wutsi.editorjs.utils.TextUtils
import org.apache.tika.language.detect.LanguageDetector
import org.jsoup.Jsoup
import org.springframework.stereotype.Service
import java.io.StringWriter

@Service
class EditorJSService(
    private val htmlWriter: EJSHtmlWriter,
    private val htmlReader: EJSHtmlReader,
    private val jsonReader: EJSJsonReader,
    private val jsonWriter: EJSJsonWriter,
    private val readabilityCalculator: ReadabilityCalculator,
    private val readabilityContext: ReadabilityContext,
    private val languageDetector: LanguageDetector,
) {
    fun readabilityScore(doc: EJSDocument): ReadabilityResult = readabilityCalculator.compute(doc, readabilityContext)

    fun fromJson(json: String?, summary: Boolean = false): EJSDocument =
        if (json == null || json.isEmpty()) EJSDocument() else jsonReader.read(json, summary)

    fun toJson(doc: EJSDocument): String {
        val json = StringWriter()
        jsonWriter.write(doc, json)
        return json.toString()
    }

    fun fromHtml(html: String) = htmlReader.read(html)

    fun toHtml(doc: EJSDocument): String {
        val writer = StringWriter()
        htmlWriter.write(doc, writer)
        return writer.toString()
    }

    fun toText(doc: EJSDocument): String {
        val html = toHtml(doc)
        return html2text(html)
    }

    fun wordCount(doc: EJSDocument): Int {
        val text = toText(doc)
        return TextUtils.words(text).size
    }

    fun detectLanguage(title: String?, summary: String?, doc: EJSDocument): String? {
        val text = StringBuilder()
        title?.let { text.append(title).append('\n') }
        summary?.let { text.append(summary).append('\n') }
        text.append(toText(doc))

        return languageDetector.detect(text.toString()).language
    }

    fun detectVideo(doc: EJSDocument): Boolean =
        hasEmbed(doc, listOf("youtube", "vimeo"))

    private fun hasEmbed(doc: EJSDocument, services: List<String>) = doc
        .blocks
        .find { it.type == BlockType.embed && services.contains(it.data.service) } != null

    fun extractSummary(doc: EJSDocument, maxLength: Int): String {
        val blocks = doc.blocks.filter { it.type == BlockType.paragraph }
        val text = html2text(
            blocks.map { it.data.text }.joinToString(separator = "\n")
        )

        return if (text.length > maxLength) text.substring(0, maxLength) + "..." else text
    }

    fun extractThumbnailUrl(doc: EJSDocument): String {
        val block = doc.blocks.find { it.type == BlockType.image }
        return if (block != null) block.data.file.url else ""
    }

    private fun html2text(html: String) = Jsoup
        .parse(html)
        .body()
        .text()
        .trim()
}
