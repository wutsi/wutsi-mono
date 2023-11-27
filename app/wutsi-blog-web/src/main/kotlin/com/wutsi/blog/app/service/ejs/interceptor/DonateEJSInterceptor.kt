package com.wutsi.blog.app.service.ejs.interceptor

import com.wutsi.blog.app.model.StoryModel
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.Toggles
import com.wutsi.blog.app.service.ejs.EJSInterceptor
import com.wutsi.editorjs.dom.Block
import com.wutsi.editorjs.dom.BlockData
import com.wutsi.editorjs.dom.BlockType
import com.wutsi.editorjs.dom.EJSDocument

@Deprecated("Stop request donation in stories")
class DonateEJSInterceptor(
    private val requestContext: RequestContext,
    private val toggles: Toggles,
) : EJSInterceptor {
    /**
     * IF monetization is enabled
     *   Add the button at position 3/4
     *  END
     */
    override fun filter(doc: EJSDocument, story: StoryModel) {
        if (toggles.monetization && story.user.walletId != null) {
            val url = "${story.user.slug}/donate"
            val index = doc.blocks.size * .75
            insertAt(index.toInt(), doc, url)
        }
    }

    private fun insertAt(index: Int, doc: EJSDocument, url: String) {
        doc.blocks.add(
            index,
            Block(
                type = BlockType.button,
                data = BlockData(
                    text = requestContext.getMessage("button.make_a_donation"),
                    centered = true,
                    url = url,
                ),
            ),
        )
    }
}
