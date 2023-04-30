package com.wutsi.editorjs.html

import com.wutsi.editorjs.dom.EJSDocument
import com.wutsi.editorjs.html.tag.TagProvider
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

class EJSHtmlReader(private val provider: TagProvider) {
    fun read(html: String): EJSDocument {
        val ejs = EJSDocument()
        val body = Jsoup.parse(html).body()
        body.children().forEach {
            load(it, ejs)
        }
        return ejs
    }

    private fun load(elt: Element, ejs: EJSDocument) {
        val tag = provider.get(elt)
        if (tag != null) {
            val block = tag.read(elt)
            if (block != null) {
                ejs.blocks.add(block)
                return
            }
        }
        elt.children().forEach { load(it, ejs) }
    }
}
