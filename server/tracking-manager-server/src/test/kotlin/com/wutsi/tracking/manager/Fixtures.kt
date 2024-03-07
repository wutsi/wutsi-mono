package com.wutsi.tracking.manager

import com.wutsi.tracking.manager.dto.ChannelType
import com.wutsi.tracking.manager.dto.DeviceType
import com.wutsi.tracking.manager.dto.PushTrackRequest
import com.wutsi.tracking.manager.entity.ClickEntity
import com.wutsi.tracking.manager.entity.EmailEntity
import com.wutsi.tracking.manager.entity.FromEntity
import com.wutsi.tracking.manager.entity.ReadEntity
import com.wutsi.tracking.manager.entity.ReaderEntity
import com.wutsi.tracking.manager.entity.TrackEntity
import com.wutsi.tracking.manager.entity.ViewEntity
import java.util.UUID

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
        campaign = "campaign-11111",
    )

    fun createTrackEntity(
        correlationId: String = UUID.randomUUID().toString(),
        time: Long = 1223,
        bot: Boolean = false,
        event: String? = "load",
        productId: String? = "123",
        page: String? = "SR",
        value: String? = null,
        accountId: String? = null,
        deviceId: String = UUID.randomUUID().toString(),
        url: String = "https://www.wutsi.com/read/123/this-is-nice?utm_source=email&utm_campaign=test&utm_medium=email",
        referer: String? = "https://www.google.ca",
        source: String? = "facebook",
        channel: ChannelType? = ChannelType.WEB,
        ua: String? = "Mozilla/5.0 (compatible; MSIE 9.0; Windows Phone OS 7.5; Trident/5.0; IEMobile/9.0)",
    ) = TrackEntity(
        time = time,
        ua = ua,
        correlationId = correlationId,
        bot = bot,
        event = event,
        productId = productId,
        page = page,
        value = value,
        revenue = 1000,
        long = 111.0,
        lat = 222.0,
        ip = "1.1.2.3",
        deviceId = deviceId,
        accountId = accountId,
        merchantId = "555",
        referrer = referer,
        url = url,
        deviceType = DeviceType.DESKTOP.name,
        source = source,
        channel = channel?.name,
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

    fun createViewEntity(
        productId: String = "123",
        totalViews: Long = 1000,
    ) = ViewEntity(
        productId = productId,
        totalViews = totalViews,
    )

    fun createClickEntity(
        productId: String = "123",
        totalClicks: Long = 1000,
    ) = ClickEntity(
        productId = productId,
        totalClicks = totalClicks,
    )

    fun createReaderEntity(
        accountId: String = "1",
        deviceId: String? = null,
        productId: String = "123",
        totalReads: Long = 1000,
    ) = ReaderEntity(
        accountId = accountId,
        deviceId = deviceId,
        productId = productId,
        totalReads = totalReads,
    )

    fun createEmailEntity(
        accountId: String = "1",
        productId: String = "123",
        totalReads: Long = 1000,
    ) = EmailEntity(
        accountId = accountId,
        productId = productId,
        totalReads = totalReads,
    )

    fun createFromEntity(
        productId: String = "123",
        from: String = "read-also",
        totalReads: Long = 111,
    ) = FromEntity(
        productId = productId,
        from = from,
        totalReads = totalReads,
    )
}
