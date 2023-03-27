package com.wutsi.application.membership.settings.business.page

import com.wutsi.application.membership.settings.service.ProfileEditorWidgetProvider
import org.springframework.beans.factory.annotation.Autowired

abstract class AbstractBusinessAttributePage : AbstractBusinessPage() {
    @Autowired
    protected lateinit var widgetProvider: ProfileEditorWidgetProvider

    protected abstract fun getAttribute(): String

    override fun getTitle() = getText("page.settings.profile.attribute.${getAttribute()}")

    override fun getSubTitle() = getText("page.settings.profile.attribute.${getAttribute()}.description")
}
