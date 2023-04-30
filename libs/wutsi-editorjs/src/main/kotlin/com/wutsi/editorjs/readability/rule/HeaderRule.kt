package com.wutsi.editorjs.readability.rule

import com.wutsi.editorjs.dom.EJSDocument
import com.wutsi.editorjs.readability.ReadabilityContext
import com.wutsi.editorjs.readability.RuleResult

class HeaderRule : AbstractRule() {
    override fun validate(doc: EJSDocument, context: ReadabilityContext): RuleResult {
        val html = super.toHtml(doc, context)
        val headers = collect(html, arrayListOf("h1", "h2", "h3", "h4", "h5", "h6"))
        return result(headers.isNotEmpty())
    }
}
