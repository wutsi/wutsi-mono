package com.wutsi.blog.ads.endpoint

import com.wutsi.blog.ads.dao.AdsRepository
import com.wutsi.blog.ads.domain.AdsEntity
import com.wutsi.blog.ads.dto.AdsAttributeUpdatedEventPayload
import com.wutsi.blog.ads.dto.AdsCTAType
import com.wutsi.blog.ads.dto.AdsType
import com.wutsi.blog.ads.dto.Gender
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
import java.text.SimpleDateFormat

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
    fun `start date`() {
        val ads = updateAttribute("start_date", "2024-10-11")
        assertEquals("2024-10-11", SimpleDateFormat("yyyy-MM-dd").format(ads.startDate))
        assertEvent("start_date", "2024-10-11")
    }

    @Test
    fun `end date`() {
        val ads = updateAttribute("end_date", "2024-10-11")
        assertEquals("2024-10-11", SimpleDateFormat("yyyy-MM-dd").format(ads.endDate))
        assertEvent("end_date", "2024-10-11")
    }

    @Test
    fun `CTA type`() {
        val ads = updateAttribute("cta_type", AdsCTAType.BUY_NOW.name)
        assertEquals(AdsCTAType.BUY_NOW, ads.ctaType)
        assertEvent("cta_type", AdsCTAType.BUY_NOW.name)
    }

    @Test
    fun type() {
        val ads = updateAttribute("type", AdsType.BANNER_MOBILE.name)
        assertEquals(AdsType.BANNER_MOBILE, ads.type)
        assertEvent("type", AdsType.BANNER_MOBILE.name)
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

    @Test
    fun country() {
        val ads = updateAttribute("country", "cm")
        assertEquals("cm", ads.country)
        assertEvent("country", "cm")
    }

    @Test
    fun language() {
        val ads = updateAttribute("language", "fr")
        assertEquals("fr", ads.language)
        assertEvent("language", "fr")
    }

    @Test
    fun gender() {
        val ads = updateAttribute("gender", "male")
        assertEquals(Gender.MALE, ads.gender)
        assertEvent("gender", "male")
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