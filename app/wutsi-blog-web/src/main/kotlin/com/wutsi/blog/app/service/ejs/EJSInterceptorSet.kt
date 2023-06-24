package com.wutsi.blog.app.service.ejs

import com.wutsi.blog.app.model.StoryModel
import com.wutsi.editorjs.dom.EJSDocument

class EJSInterceptorSet(private val interceptors: List<EJSInterceptor>) : EJSInterceptor {
    override fun filter(doc: EJSDocument, story: StoryModel) {
        interceptors.forEach {
            it.filter(doc, story)
        }
    }
}
