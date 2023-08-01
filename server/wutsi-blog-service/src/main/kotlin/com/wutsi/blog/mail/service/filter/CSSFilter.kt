package com.wutsi.blog.mail.service.filter

import com.wutsi.blog.mail.service.MailContext
import com.wutsi.blog.mail.service.MailFilter
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Entities.EscapeMode.extended

class CSSFilter : MailFilter {
    companion object {
        private val STYLES = mapOf(
            ".social" to """
                text-decoration: none
            """.trimIndent(),

            ".body" to """
                background: #f8f8f8;
                max-width: 800px;
                font-family: 'PT Sans', sans-serif;
                width: 96%;
                border: none;
                border-spacing: 0;
                font-size: 18px;
                margin: 0 auto;
            """.trimIndent(),

            ".btn-primary" to """
                border-radius: 16px;
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
                border-radius: 16px;
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

            ".flex" to """
                display: flex;
            """.trimIndent(),

            ".btn-secondary" to """
                border-radius: 16px;
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

            ".btn-clear" to """
                border-radius: 16px;
                display: inline-block;
                font-weight: 400;
                color: darkgray;
                background-color: #ffffff;
                text-align: center;
                vertical-align: middle;
                border: 1px solid darkgray;
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

            ".text-smaller" to """
                font-size: smaller;
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
                padding: 16px;
            """.trimIndent(),

            ".padding-2x" to """
                padding: 32px;
            """.trimIndent(),

            ".padding-top" to """
                padding-top: 16px;
            """.trimIndent(),

            ".padding-bottom" to """
                padding-bottom: 16px;
            """.trimIndent(),

            ".border-top" to """
                border-top: 1px solid lightgray;
            """.trimIndent(),

            ".border-bottom" to """
                border-bottom: 1px solid lightgray;
            """.trimIndent(),

            ".margin-bottom" to """
                margin-bottom: 16px;
            """.trimIndent(),

            ".margin-top" to """
                margin-top: 16px;
            """.trimIndent(),

            ".margin-right" to """
                margin-right: 16px;
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

            /* Content */
            ".content" to """
                background: white;
                font-family: 'PT Sans', sans-serif;
                margin: 0 auto;
                width: 96%;
            """.trimIndent(),

            ".content > *" to """
                margin-bottom: 1em;
                display: block;
            """.trimIndent(),

            /* Content - block quote */
            ".content blockquote" to """
                background: lightgray;
                border-left: 10px solid grey;
                padding: 1em;
                font-size: 1.4em;
            """.trimIndent(),

            ".content blockquote:before" to """
                color: grey;
                content: open-quote;
                font-size: 4em;
                line-height: 0.1em;
                margin-right: 0.25em;
                vertical-align: -0.4em;
            """.trimIndent(),

            ".content blockquote p" to """
                display: inline;
            """.trimIndent(),

            ".content blockquote footer" to """
                text-decoration: underline;
            """.trimIndent(),

            /* Content - image */
            ".content figure img.stretched" to """
                width: 100%;
            """.trimIndent(),

            /* Content - LinkTool */
            ".content div.link-tool" to """
                color: black;
                text-decoration: none;
            """.trimIndent(),

            ".content div.link-tool" to """
                border: 1px solid var(--border-color);
                border-radius: 0.5em;
                display: flex;
                flex-wrap: wrap;
                padding: 1em;
            """.trimIndent(),

            ".content div.link-tool div.meta" to """
                width: 70%;
                padding-right: 1em;
            """.trimIndent(),

            ".content div.link-tool div.meta h2" to """
                font-size: 1em;
                font-weight: bold;
                margin-bottom: 1em;
                padding-top: 0;
            """.trimIndent(),

            ".content div.link-tool div.meta p" to """
                margin: 0;
            """.trimIndent(),

            ".content div.link-tool div.meta p.site" to """
                color: grey;
            """.trimIndent(),

            ".content div.link-tool div.image" to """
                width: 30%;
                max-height: 150px;
                overflow: hidden;
            """.trimIndent(),

            ".reader .content div.link-tool div.image img" to """
                max-width: 100%;
                margin: 0 auto;
                border: 1px solid var(--border-color);
            """.trimIndent(),

            /* Content - Attaches */
            ".content a.attaches" to """
                color: black;
                text-decoration: none;
            """.trimIndent(),

            ".content div.attaches" to """
                border: 1px solid lightgray;
                border-radius: 0.5em;
                display: flex;
                flex-wrap: wrap;
                padding: 1em;
            """.trimIndent(),

            ".content div.attaches div.ext" to """
                margin-right: 1em;
            """.trimIndent(),

            ".content div.attaches div.ext span" to """
                padding: 0.5em;
                border-radius: 0.5em;
                background: lightgray;
                font-size: smaller;
            """.trimIndent(),

            ".content div.attaches div.ext span.pdf" to """
                background: darkred;
                color: white;
            """.trimIndent(),

            ".content div.attaches div.ext span.csv, .content div.attaches div.ext span.xls, .content div.attaches div.ext span.xlsx" to """
                background: green;
                color: white;
            """.trimIndent(),

            ".content div.attaches div.ext span.doc, .content div.attaches div.ext span.docx" to """
                background: #e4edf7;
                color: white;
            """.trimIndent(),

            ".content div.attaches div.ext span.ppt, .content div.attaches div.ext span.pptx" to """
                background: #indianred;
                color: white;
            """.trimIndent(),

            ".content a.attaches div.attaches .filesize" to """
                color: grey;
                font-size: smaller;
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
