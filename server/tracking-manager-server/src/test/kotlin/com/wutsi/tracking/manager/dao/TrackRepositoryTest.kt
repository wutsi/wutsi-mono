package com.wutsi.tracking.manager.dao

import com.wutsi.platform.core.storage.StorageService
import com.wutsi.tracking.manager.Fixtures
import com.wutsi.tracking.manager.dto.ChannelType
import com.wutsi.tracking.manager.dto.DeviceType
import com.wutsi.tracking.manager.entity.TrackEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.time.LocalDate
import kotlin.test.assertNull

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class TrackRepositoryTest {
    @Autowired
    private lateinit var dao: TrackRepository

    @Autowired
    private lateinit var storageService: StorageService

    @Value("\${wutsi.platform.storage.local.directory}")
    private lateinit var storageDirectory: String

    @BeforeEach
    fun setUp() {
        File("$storageDirectory/track").deleteRecursively()
    }

    @Test
    fun save() {
        // WHEN
        val url = dao.save(arrayListOf(createTrack()))

        // THEN
        val out = ByteArrayOutputStream()
        storageService.get(url, out)
        assertEquals(
            """
                time,correlation_id,device_id,account_id,merchant_id,product_id,page,event,value,revenue,ip,long,lat,bot,device_type,channel,source,campaign,url,referrer,ua,business_id,country
                3333,123,sample-device,333,555,1234,SR,pageview,yo,1000,1.1.2.3,111.0,222.0,false,DESKTOP,WEB,facebook,12434554,https://www.wutsi.com/read/123/this-is-nice?utm_source=email&utm_campaign=test&utm_medium=email,https://www.google.ca,Mozilla/5.0 (compatible; MSIE 9.0; Windows Phone OS 7.5; Trident/5.0; IEMobile/9.0),777,CM
            """.trimIndent(),
            out.toString().trimIndent(),
        )
    }

    @Test
    fun read() {
        // GIVEN
        val csv = """
                time,correlation_id,device_id,account_id,merchant_id,product_id,page,event,value,revenue,ip,long,lat,bot,device_type,channel,source,campaign,url,referrer,ua,business_id,country
                3333,123,sample-device,333,555,1234,SR,pageview,yo,1000,1.1.2.3,111.0,222.0,false,DESKTOP,WEB,facebook,12434554,https://www.wutsi.com/read/123/this-is-nice?utm_source=email&utm_campaign=test&utm_medium=email,https://www.google.ca,Mozilla/5.0 (compatible; MSIE 9.0; Windows Phone OS 7.5; Trident/5.0; IEMobile/9.0),777,CM
        """.trimIndent()

        // WHEN
        val tracks = dao.read(ByteArrayInputStream(csv.toByteArray()))

        // THEN
        assertEquals(1, tracks.size)
        assertEquals(3333L, tracks[0].time)
        assertEquals("123", tracks[0].correlationId)
        assertEquals("sample-device", tracks[0].deviceId)
        assertEquals("333", tracks[0].accountId)
        assertEquals("555", tracks[0].merchantId)
        assertEquals("1234", tracks[0].productId)
        assertEquals("SR", tracks[0].page)
        assertEquals("pageview", tracks[0].event)
        assertEquals("yo", tracks[0].value)
        assertEquals(1000L, tracks[0].revenue)
        assertEquals("1.1.2.3", tracks[0].ip)
        assertEquals(111.0, tracks[0].long)
        assertEquals(222.0, tracks[0].lat)
        assertEquals(false, tracks[0].bot)
        assertEquals("DESKTOP", tracks[0].deviceType)
        assertEquals("WEB", tracks[0].channel)
        assertEquals("facebook", tracks[0].source)
        assertEquals("12434554", tracks[0].campaign)
        assertEquals(
            "https://www.wutsi.com/read/123/this-is-nice?utm_source=email&utm_campaign=test&utm_medium=email",
            tracks[0].url,
        )
        assertEquals("https://www.google.ca", tracks[0].referrer)
        assertEquals(
            "Mozilla/5.0 (compatible; MSIE 9.0; Windows Phone OS 7.5; Trident/5.0; IEMobile/9.0)",
            tracks[0].ua,
        )
        assertEquals("777", tracks[0].businessId)
        assertEquals("CM", tracks[0].country)
    }

    @Test
    fun readNoBusinessId() {
        // GIVEN
        val csv = """
                time,correlation_id,device_id,account_id,merchant_id,product_id,page,event,value,revenue,ip,long,lat,bot,device_type,channel,source,campaign,url,referrer,ua
                3333,123,sample-device,333,555,1234,SR,pageview,yo,1000,1.1.2.3,111.0,222.0,false,DESKTOP,WEB,facebook,12434554,https://www.wutsi.com/read/123/this-is-nice?utm_source=email&utm_campaign=test&utm_medium=email,https://www.google.ca,Mozilla/5.0 (compatible; MSIE 9.0; Windows Phone OS 7.5; Trident/5.0; IEMobile/9.0)
        """.trimIndent()

        // WHEN
        val tracks = dao.read(ByteArrayInputStream(csv.toByteArray()))

        // THEN
        assertEquals(1, tracks.size)
        assertEquals(3333L, tracks[0].time)
        assertEquals("123", tracks[0].correlationId)
        assertEquals("sample-device", tracks[0].deviceId)
        assertEquals("333", tracks[0].accountId)
        assertEquals("555", tracks[0].merchantId)
        assertEquals("1234", tracks[0].productId)
        assertEquals("SR", tracks[0].page)
        assertEquals("pageview", tracks[0].event)
        assertEquals("yo", tracks[0].value)
        assertEquals(1000L, tracks[0].revenue)
        assertEquals("1.1.2.3", tracks[0].ip)
        assertEquals(111.0, tracks[0].long)
        assertEquals(222.0, tracks[0].lat)
        assertEquals(false, tracks[0].bot)
        assertEquals("DESKTOP", tracks[0].deviceType)
        assertEquals("WEB", tracks[0].channel)
        assertEquals("facebook", tracks[0].source)
        assertEquals("12434554", tracks[0].campaign)
        assertEquals(
            "https://www.wutsi.com/read/123/this-is-nice?utm_source=email&utm_campaign=test&utm_medium=email",
            tracks[0].url,
        )
        assertEquals("https://www.google.ca", tracks[0].referrer)
        assertEquals(
            "Mozilla/5.0 (compatible; MSIE 9.0; Windows Phone OS 7.5; Trident/5.0; IEMobile/9.0)",
            tracks[0].ua,
        )
        assertNull(tracks[0].businessId)
    }

    @Test
    fun readNoCountry() {
        // GIVEN
        val csv = """
                time,correlation_id,device_id,account_id,merchant_id,product_id,page,event,value,revenue,ip,long,lat,bot,device_type,channel,source,campaign,url,referrer,ua,business_id
                3333,123,sample-device,333,555,1234,SR,pageview,yo,1000,1.1.2.3,111.0,222.0,false,DESKTOP,WEB,facebook,12434554,https://www.wutsi.com/read/123/this-is-nice?utm_source=email&utm_campaign=test&utm_medium=email,https://www.google.ca,Mozilla/5.0 (compatible; MSIE 9.0; Windows Phone OS 7.5; Trident/5.0; IEMobile/9.0),777
        """.trimIndent()

        // WHEN
        val tracks = dao.read(ByteArrayInputStream(csv.toByteArray()))

        // THEN
        assertNull(tracks[0].country)
    }

    @Test
    fun getURLs() {
        // GIVEN
        val date = LocalDate.of(2020, 10, 2)
        dao.save(listOf(Fixtures.createTrackEntity()), date.plusDays(-1))
        dao.save(listOf(Fixtures.createTrackEntity(), Fixtures.createTrackEntity()), date)
        dao.save(listOf(Fixtures.createTrackEntity()), date)
        dao.save(listOf(Fixtures.createTrackEntity()), date.plusDays(1))

        // WHEN
        val urls = dao.getURLs(date)

        // THEN
        assertEquals(2, urls.size)
    }

    private fun createTrack() = TrackEntity(
        time = 3333,
        ua = "Mozilla/5.0 (compatible; MSIE 9.0; Windows Phone OS 7.5; Trident/5.0; IEMobile/9.0)",
        correlationId = "123",
        bot = false,
        event = "pageview",
        productId = "1234",
        page = "SR",
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
        businessId = "777",
        country = "CM",
    )
}
