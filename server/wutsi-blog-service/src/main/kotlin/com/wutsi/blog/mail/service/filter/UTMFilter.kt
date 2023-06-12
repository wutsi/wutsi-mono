package com.wutsi.blog.mail.service.filter

import com.wutsi.blog.mail.service.MailContext
import com.wutsi.blog.mail.service.MailFilter
import java.util.regex.Pattern

class UTMFilter : MailFilter {
    companion object {
        private val HREF_REGEXP = Pattern.compile("href=[\"|'](.*?)[\"|']")
    }

    override fun filter(html: String, context: MailContext): String {
        val m = HREF_REGEXP.matcher(html)
        val sb = StringBuffer()
        while (m.find()) {
            val url = m.group(0)
            val xurl = appendUTMParametersToURL(url.substring(6, url.length - 1))
            m.appendReplacement(sb, "href=\"$xurl\"")
        }
        m.appendTail(sb)
        return sb.toString()
    }

    private fun appendUTMParametersToURL(url: String): String {
        val params = "utm_medium=email"
        return if (url.contains('?')) "$url&$params" else "$url?$params"
    }
}
