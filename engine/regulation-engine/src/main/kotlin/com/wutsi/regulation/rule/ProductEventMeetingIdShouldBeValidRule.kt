package com.wutsi.regulation.rule

import com.wutsi.enums.ProductType
import com.wutsi.error.ErrorURN
import com.wutsi.marketplace.access.dto.Product
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.exception.ConflictException
import com.wutsi.regulation.Rule
import java.net.HttpURLConnection
import java.net.URL

class ProductEventMeetingIdShouldBeValidRule(
    private val product: Product,
) : Rule {
    override fun check() {
        if (
            product.type == ProductType.EVENT.name &&
            product.event?.meetingJoinUrl != null
        ) {
            val url = URL(product.event!!.meetingJoinUrl)
            val cnn = url.openConnection() as HttpURLConnection
            try {
                cnn.requestMethod = "HEAD"

                val responseCode = cnn.responseCode
                if (responseCode / 100 != 2) {
                    throw error(product.event?.meetingJoinUrl ?: "", responseCode)
                }
            } catch (ex: Exception) {
                throw error(product.event?.meetingJoinUrl ?: "")
            } finally {
                cnn.disconnect()
            }
        }
    }

    private fun error(url: String, responseCode: Int? = null) = ConflictException(
        error = Error(
            code = ErrorURN.PRODUCT_EVENT_MEETING_ID_NOT_VALID.urn,
            data = mapOf(
                "meeting-join-url" to url,
                "response-code" to (responseCode?.toString() ?: ""),
            ),
        ),
    )
}
