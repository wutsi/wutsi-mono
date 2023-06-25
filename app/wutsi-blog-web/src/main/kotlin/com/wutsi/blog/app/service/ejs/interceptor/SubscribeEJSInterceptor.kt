package com.wutsi.blog.app.service.ejs.interceptor

import com.wutsi.blog.app.model.StoryModel
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.ejs.EJSInterceptor
import com.wutsi.editorjs.dom.Block
import com.wutsi.editorjs.dom.BlockData
import com.wutsi.editorjs.dom.BlockType
import com.wutsi.editorjs.dom.EJSDocument

class SubscribeEJSInterceptor(
    private val requestContext: RequestContext,
) : EJSInterceptor {
    /**
     *  IF user can subscribe to blog
     *   Add the button at position 1/4
     *  END
     */
    override fun filter(doc: EJSDocument, story: StoryModel) {
        val user = requestContext.currentUser()
        if (user == null || user.canSubscribeTo(story.user)) {
            val url = "${story.user.slug}/subscribe?return-url=${story.slug}"
            val index1 = doc.blocks.size * .25
            if (index1 > 1) {
                insertAt(index1.toInt(), doc, url)
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
