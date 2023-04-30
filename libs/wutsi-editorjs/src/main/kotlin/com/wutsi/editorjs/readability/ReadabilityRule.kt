package com.wutsi.editorjs.readability

import com.wutsi.editorjs.dom.EJSDocument

interface ReadabilityRule{
    fun name(): String
    fun validate(doc: EJSDocument, context: ReadabilityContext): RuleResult
}
