package com.wutsi.editorjs.readability.rule

import com.wutsi.editorjs.dom.Block
import com.wutsi.editorjs.dom.BlockType
import com.wutsi.editorjs.dom.EJSDocument
import com.wutsi.editorjs.readability.ReadabilityContext
import com.wutsi.editorjs.readability.RuleResult
import com.wutsi.editorjs.utils.TextUtils
import org.jsoup.Jsoup

class ShortSentenceRule : AbstractRule() {
    override fun validate(doc: EJSDocument, context: ReadabilityContext): RuleResult {
        val sentences = getAllSentences(doc)
        if (sentences.isEmpty()) {
            return result(false)
        }

        val xsentences = getLongSentences(sentences, context)
        val score = 100 - (100 * xsentences.size) / sentences.size
        return result(score)
    }

    private fun getAllSentences(doc: EJSDocument) = doc.blocks.filter { it.type == BlockType.paragraph }.flatMap { toSentences(it) }

    private fun toSentences(block: Block): List<String> {
        val text = Jsoup.parse(block.data.text).text()
        return TextUtils.sentences(text)
    }

    private fun getLongSentences(sentences: List<String>, context: ReadabilityContext) = sentences.filter { isLong(it, context) }

    private fun isLong(phrase: String, context: ReadabilityContext): Boolean {
        val words = TextUtils.words(phrase)
        return words.size > context.maxWordsPerSentence
    }
}
