package com.wutsi.mail.filter

import com.wutsi.mail.MailContext
import com.wutsi.mail.MailFilter
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Entities.EscapeMode.extended

class CSSFilter : MailFilter {
    companion object {
        private val STYLES = mapOf(
            "h4" to """
                font-size: 1.1em;
            """.trimIndent(),
            "h3" to """
                font-size: 1.25em;
            """.trimIndent(),
            "h2" to """
                font-size: 1.5em;
            """.trimIndent(),
            "h1" to """
                font-size: 1.75em;
            """.trimIndent(),

            ".social" to """
                text-decoration: none
            """.trimIndent(),

            ".body" to """
                background: #f8f8f8;
            """.trimIndent(),

            ".content" to """
                border: 1px solid lightgray;
                background: white;
                font-family: 'PT Sans', sans-serif;
                font-size: 1em;
                margin: 0 auto;
                max-width: 600px;
            """.trimIndent(),

            ".btn-primary" to """
                display: inline-block;
                font-weight: 400;
                color: #FFFFFF;
                background-color: #1D7EDF;
                text-align: center;
                vertical-align: middle;
                border: 1px solid transparent;
                padding: .375rem .75rem;
                font-size: 1rem;
                line-height: 1.5;
                text-decoration: none;
            """.trimIndent(),

            ".btn-success" to """
                display: inline-block;
                font-weight: 400;
                color: #FFFFFF;
                background-color: #4CAF50;
                text-align: center;
                vertical-align: middle;
                border: 1px solid transparent;
                padding: .375rem .75rem;
                font-size: 1rem;
                line-height: 1.5;
                text-decoration: none;
            """.trimIndent(),

            ".btn-secondary" to """
                display: inline-block;
                font-weight: 400;
                color: gray;
                background-color: #e4edf7;
                text-align: center;
                vertical-align: middle;
                border: 1px solid lightgray;
                padding: .375rem .75rem;
                line-height: 1.5;
                text-decoration: none;
            """.trimIndent(),

            ".text-center" to """
                text-align: center;
            """.trimIndent(),

            ".text-larger" to """
                font-size: larger;
            """.trimIndent(),

            ".text-small" to """
                font-size: small;
            """.trimIndent(),

            ".text-x-small" to """
                font-size: x-small;
            """.trimIndent(),

            ".no-margin" to """
                margin: 0
            """.trimIndent(),

            ".no-padding" to """
                padding: 0
            """.trimIndent(),

            ".no-text-decoration" to """
                text-decoration: none
            """.trimIndent(),

            ".border" to """
                border: 1px solid lightgray;
            """.trimIndent(),

            ".padding" to """
                padding: 10px 10px 10px 10px;
            """.trimIndent(),

            ".padding-top" to """
                padding-top: 10px;
            """.trimIndent(),

            ".padding-bottom" to """
                padding-bottom: 10px;
            """.trimIndent(),

            ".border-top" to """
                border-top: 1px solid lightgray;
            """.trimIndent(),

            ".border-bottom" to """
                border-bottom: 1px solid lightgray;
            """.trimIndent(),

            ".rounded" to """
                border-radius: 5px
            """.trimIndent(),

            ".box-highlight" to """
                background: #e4edf7;
                border: 1px solid #1D7EDF;
            """.trimIndent(),

            ".highlight" to """
                color: #1D7EDF
            """.trimIndent(),

            ".success" to """
                color: #4CAF50
            """.trimIndent(),
        )
    }

    override fun filter(html: String, context: MailContext): String {
        val doc = Jsoup.parse(html)
        STYLES.keys.forEach {
            apply(it, doc)
        }
        doc.outputSettings()
            .charset("ASCII")
            .escapeMode(extended)
            .indentAmount(2)
            .prettyPrint(true)
            .outline(true)
        return doc.toString()
    }

    private fun apply(selector: String, doc: Document) {
        val style = STYLES[selector]?.replace("\n", "") ?: return
        doc.select(selector).forEach {
            if (it.hasAttr("style")) {
                it.attr("style", it.attr("style") + ";$style")
            } else {
                it.attr("style", style)
            }
        }
    }
}
