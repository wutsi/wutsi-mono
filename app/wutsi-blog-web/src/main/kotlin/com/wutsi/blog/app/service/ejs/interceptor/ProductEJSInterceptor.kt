package com.wutsi.blog.app.service.ejs.interceptor

import com.wutsi.blog.app.model.StoryModel
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.ejs.EJSInterceptor
import com.wutsi.editorjs.dom.Block
import com.wutsi.editorjs.dom.BlockData
import com.wutsi.editorjs.dom.BlockType
import com.wutsi.editorjs.dom.EJSDocument
import java.util.Locale

class ProductEJSInterceptor(private val requestContext: RequestContext) : EJSInterceptor {
    /**
     *  IF user can subscribe to blog
     *   Add the button at position 25% and 75%
     *  END
     */
    override fun filter(doc: EJSDocument, story: StoryModel) {
        val index1 = (doc.blocks.size * .25).toInt()
        if (index1 >= 3) {
            addLink(doc, index1, story)
        }

        val index2 = (doc.blocks.size * .75).toInt()
        if (index2 > index1) {
            addLink(doc, index2, story)
        }
    }

    private fun addLink(doc: EJSDocument, index: Int, story: StoryModel) {
        if (story.product == null) {
            return
        }

        doc.blocks.add(
            index,
            Block(
                type = BlockType.paragraph,
                data = BlockData(
                    centered = true,
                    text = if (story.product.comics || story.product.ebook) {
                        "<b><a href='${story.product.url}'>${getPrefix(story)}: ${story.product.title}</a></b>"
                    } else {
                        "<b><a href='${story.product.url}'>${story.product.title}</a></b>"
                    },
                ),
            ),
        )
    }

    private fun getPrefix(story: StoryModel): String =
        requestContext.getMessage("button.buy_the_book", locale = Locale(story.language ?: "fr")).uppercase()
}
