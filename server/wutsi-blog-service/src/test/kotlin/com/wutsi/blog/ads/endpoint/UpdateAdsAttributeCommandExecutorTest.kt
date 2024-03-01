package com.wutsi.blog.ads.endpoint

import com.wutsi.blog.ads.dao.AdsRepository
import com.wutsi.blog.ads.domain.AdsEntity
import com.wutsi.blog.ads.dto.AdsAttributeUpdatedEventPayload
import com.wutsi.blog.ads.dto.AdsCTAType
import com.wutsi.blog.ads.dto.AdsType
import com.wutsi.blog.ads.dto.UpdateAdsAttributeCommand
import com.wutsi.blog.event.EventType
import com.wutsi.blog.event.StreamId
import com.wutsi.event.store.EventStore
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.test.context.jdbc.Sql

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/ads/UpdateAdsAttributeCommand.sql"])
class UpdateAdsAttributeCommandExecutorTest {
    @Autowired
    private lateinit var rest: TestRestTemplate

    @Autowired
    private lateinit var dao: AdsRepository

    @Autowired
    private lateinit var eventStore: EventStore

    @Test
    fun title() {
        val ads = updateAttribute("title", "Sample product")
        assertEquals("Sample product", ads.title)
        assertEvent("title", "Sample product")
    }

    @Test
    fun `duration days`() {
        val ads = updateAttribute("duration_days", "10")
        assertEquals(10, ads.durationDays)
        assertEvent("duration_days", "10")
    }

    @Test
    fun `CTA type`() {
        val ads = updateAttribute("cta_type", AdsCTAType.BUY_NOW.name)
        assertEquals(AdsCTAType.BUY_NOW, ads.ctaType)
        assertEvent("cta_type", AdsCTAType.BUY_NOW.name)
    }

    @Test
    fun type() {
        val ads = updateAttribute("type", AdsType.BANNER_HORIZONTAL.name)
        assertEquals(AdsType.BANNER_HORIZONTAL, ads.type)
        assertEvent("type", AdsType.BANNER_HORIZONTAL.name)
    }

    @Test
    fun url() {
        val ads = updateAttribute("url", "https://www.google.ca")
        assertEquals("https://www.google.ca", ads.url)
        assertEvent("url", "https://www.google.ca")
    }

    @Test
    fun `image url`() {
        val ads = updateAttribute("image_url", "https://www.google.ca")
        assertEquals("https://www.google.ca", ads.imageUrl)
        assertEvent("image_url", "https://www.google.ca")
    }

    private fun updateAttribute(name: String, value: String): AdsEntity {
        val request = UpdateAdsAttributeCommand(
            adsId = "100",
            name = name,
            value = value,
        )

        val response = rest.postForEntity("/v1/ads/commands/update-attribute", request, Any::class.java)
        assertEquals(200, response.statusCode.value())
        return dao.findById(request.adsId).get()
    }

    private fun assertEvent(name: String, value: String) {
        val events = eventStore.events(
            streamId = StreamId.ADS,
            entityId = "100",
            type = EventType.ADS_ATTRIBUTE_UPDATED_EVENT
        )
        assertEquals(1, events.size)

        val payload = events[0].payload as AdsAttributeUpdatedEventPayload
        assertEquals(name, payload.name)
        assertEquals(value, payload.value)
    }
}