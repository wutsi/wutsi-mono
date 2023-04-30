package com.wutsi.editorjs

import org.apache.commons.io.IOUtils

object ResourceHelper {
    fun loadResourceAsString(path: String): String {
        val resource = this.javaClass.getResourceAsStream(path)
        return IOUtils.toString(resource, "utf-8")
    }
}
