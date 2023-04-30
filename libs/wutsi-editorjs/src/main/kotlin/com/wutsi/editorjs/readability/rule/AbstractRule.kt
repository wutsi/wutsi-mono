package com.wutsi.editorjs.readability.rule

import com.wutsi.editorjs.dom.EJSDocument
import com.wutsi.editorjs.readability.ReadabilityCalculator
import com.wutsi.editorjs.readability.ReadabilityContext
import com.wutsi.editorjs.readability.ReadabilityRule
import com.wutsi.editorjs.readability.RuleResult
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.io.StringWriter

abstract class AbstractRule : ReadabilityRule {
    override fun name() = javaClass.simpleName

    protected fun toHtml(doc: EJSDocument, context: ReadabilityContext): Document {
        val html = StringWriter()
        context.htmlWriter.write(doc, html)

        return Jsoup.parse(html.toString())
    }

    protected fun collect(doc: Document, tags: List<String>): List<Element> {
        return tags.flatMap { doc.select(it) }
    }

    protected fun result(score: Int) = RuleResult(
        score = score,
        rule = this,
    )

    protected fun result(score: Boolean) = result(
        if (score) ReadabilityCalculator.MAX_SCORE else 0,
    )
}
