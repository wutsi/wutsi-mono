package com.wutsi.blog.app.service.ejs.interceptor

import com.wutsi.blog.app.model.StoryModel
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.Toggles
import com.wutsi.blog.app.service.ejs.EJSInterceptor
import com.wutsi.editorjs.dom.Block
import com.wutsi.editorjs.dom.BlockData
import com.wutsi.editorjs.dom.BlockType
import com.wutsi.editorjs.dom.EJSDocument

class SubscribeEJSInterceptor(
    private val requestContext: RequestContext,
    private val toggles: Toggles,
) : EJSInterceptor {
    /**
     *  If user can subscribe to blog
     *   Add the button at position 1/4
     *   if monetization not enabled
     *    Add the button at position 1/4 and 3/4
     *   end
     *  end
     */
    override fun filter(doc: EJSDocument, story: StoryModel) {
        val user = requestContext.currentUser()
        if (user == null || user.canSubscribeTo(story.user)) {
            // position 1/4
            val url = "${story.user.slug}/subscribe?return-url=${story.slug}"
            val index1 = doc.blocks.size * .25
            if (index1 > 1) {
                insertAt(index1.toInt(), doc, url)
            }

            // position 3/4
            if (!toggles.monetization || story.user.walletId == null) {
                val index2 = doc.blocks.size * .75
                if (index2 > index1 + 2) {
                    insertAt(index2.toInt(), doc, url)
                }
            }
        }
    }

    private fun insertAt(index: Int, doc: EJSDocument, url: String) {
        doc.blocks.add(
            index,
            Block(
                type = BlockType.button,
                data = BlockData(
                    text = requestContext.getMessage("button.subscribe_to_my_blog"),
                    centered = true,
                    url = url,
                ),
            ),
        )
    }
}
