package com.wutsi.tracking.manager

import com.wutsi.enums.ChannelType
import com.wutsi.enums.DeviceType
import com.wutsi.tracking.manager.dto.PushTrackRequest
import com.wutsi.tracking.manager.entity.ReadEntity
import com.wutsi.tracking.manager.entity.TrackEntity

object Fixtures {
    fun createPushTrackRequest() = PushTrackRequest(
        time = 3333,
        ua = "Mozilla/5.0 (compatible; MSIE 9.0; Windows Phone OS 7.5; Trident/5.0; IEMobile/9.0)",
        correlationId = "123",
        event = "pageview",
        productId = "1234",
        page = "SR",
        value = "xxx",
        revenue = 10000,
        long = 111.0,
        lat = 222.0,
        ip = "1.1.2.3",
        deviceId = "sample-device",
        accountId = "333",
        merchantId = "555",
        referrer = "https://www.google.ca",
        url = "https://www.wutsi.com/read/123/this-is-nice?utm_source=email&utm_campaign=test&utm_medium=email",
        businessId = "777",
    )

    fun createTrackEntity(
        time: Long = 1223,
        bot: Boolean = false,
        event: String? = "load",
        productId: String? = "123",
        page: String? = "SR",
    ) = TrackEntity(
        time = time,
        ua = "Mozilla/5.0 (compatible; MSIE 9.0; Windows Phone OS 7.5; Trident/5.0; IEMobile/9.0)",
        correlationId = "123",
        bot = bot,
        event = event,
        productId = productId,
        page = page,
        value = "yo",
        revenue = 1000,
        long = 111.0,
        lat = 222.0,
        ip = "1.1.2.3",
        deviceId = "sample-device",
        accountId = "333",
        merchantId = "555",
        referrer = "https://www.google.ca",
        url = "https://www.wutsi.com/read/123/this-is-nice?utm_source=email&utm_campaign=test&utm_medium=email",
        deviceType = DeviceType.DESKTOP.name,
        source = "facebook",
        channel = ChannelType.WEB.name,
        campaign = "12434554",
        businessId = "333",
    )

    fun createReadEntity(
        productId: String = "123",
        totalReads: Long = 1000,
    ) = ReadEntity(
        productId = productId,
        totalReads = totalReads,
    )
}
