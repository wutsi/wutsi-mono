package com.wutsi.blog.app.service.ejs

import com.wutsi.blog.app.model.StoryModel
import org.jsoup.nodes.Document

interface EJSFilter {
    fun filter(story: StoryModel, html: Document)
}
