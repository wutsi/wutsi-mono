package com.wutsi.blog.app.page.admin.ads

import com.wutsi.blog.ads.dto.AdsType
import com.wutsi.blog.app.page.SeleniumTestSupport
import com.wutsi.blog.app.util.PageName
import org.junit.jupiter.api.Test

class AdsFormatsControllerTest : SeleniumTestSupport() {
    @Test
    fun formats() {
        navigate(url("/ads/formats"))

        assertCurrentPageIs(PageName.ADS_FORMATS)
        assertElementCount(".ads-panel", AdsType.values().size - 1)

        click(".ads-panel .btn-learn-more")
//        assertCurrentPageIs(PageName.ADS_FORMATS_VIEW)
    }
}