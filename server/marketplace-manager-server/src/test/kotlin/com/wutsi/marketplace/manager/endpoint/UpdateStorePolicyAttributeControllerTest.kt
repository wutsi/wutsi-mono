package com.wutsi.marketplace.manager.endpoint

import com.nhaarman.mockitokotlin2.verify
import com.wutsi.marketplace.access.dto.UpdateStorePolicyAttributeRequest
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UpdateStorePolicyAttributeControllerTest : AbstractPolicyControllerTest() {
    @Test
    public fun invoke() {
        val request = UpdateStorePolicyAttributeRequest("foo", "bar")
        val response = rest.postForEntity(url(100), request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)
        verify(marketplaceAccessApi).updateStorePolicyAttribute(
            100L,
            UpdateStorePolicyAttributeRequest(
                name = request.name,
                value = request.value,
            ),
        )
    }

    private fun url(id: Long) = "http://localhost:$port/v1/stores/$id/policies"
}
