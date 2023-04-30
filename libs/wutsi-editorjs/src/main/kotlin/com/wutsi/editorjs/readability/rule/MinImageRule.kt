package com.wutsi.editorjs.readability.rule

import com.wutsi.editorjs.dom.EJSDocument
import com.wutsi.editorjs.readability.ReadabilityContext
import com.wutsi.editorjs.readability.RuleResult

class MinImageRule: AbstractRule() {
    override fun validate(doc: EJSDocument, context: ReadabilityContext): RuleResult {
        val html = super.toHtml(doc, context)
        val headers = collect(html, arrayListOf("img"))
        return result(headers.isNotEmpty())
    }
}
