package com.wutsi.blog.app.service.ejs

import com.wutsi.blog.app.model.StoryModel
import com.wutsi.editorjs.dom.EJSDocument

interface EJSInterceptor {
    fun filter(doc: EJSDocument, story: StoryModel)
}
