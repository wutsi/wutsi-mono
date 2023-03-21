package com.wutsi.membership.access.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.enums.AccountStatus
import com.wutsi.enums.PlaceType
import com.wutsi.membership.access.dto.GetAccountResponse
import com.wutsi.membership.access.dto.GetCategoryResponse
import com.wutsi.membership.access.error.ErrorURN
import com.wutsi.platform.core.error.ErrorResponse
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/GetAccountControllerByName.sql"])
public class GetAccountByNameControllerTest {
    @LocalServerPort
    public val port: Int = 0

    private val rest = RestTemplate()

    @Test
    public fun invoke() {
        val response = rest.getForEntity(url("yo-name"), GetAccountResponse::class.java)

        assertEquals(200, response.statusCodeValue)

        val account = response.body!!.account
        assertEquals(103, account.id)
        assertEquals("Yo Name", account.displayName)
        assertNull(account.pictureUrl)
        assertEquals(AccountStatus.ACTIVE.name, account.status)
        assertEquals("en", account.language)
        assertNull(null, account.email)
        assertEquals("CM", account.country)
        assertNull(account.businessId)
        assertNull(account.storeId)
        assertFalse(account.superUser)
        assertFalse(account.business)
        assertNotNull(account.created)
        assertNotNull(account.updated)
        assertNull(account.deactivated)
        assertEquals("yo-name", account.name)

        assertEquals("+237221234103", account.phone.number)
        assertEquals("CM", account.phone.country)
        assertNotNull(account.phone.created)

        assertEquals(100, account.city?.id)
        assertEquals("Yaounde", account.city?.name)
        assertEquals("Yaounde, Cameroon", account.city?.longName)
        assertEquals("CM", account.city?.country)
        assertEquals("Africa/Douala", account.city?.timezoneId)
        assertEquals(PlaceType.CITY.name, account.city?.type)

        assertNotNull(account.category)
        assertEquals(1001, account.category?.id)
        assertEquals("Agriculture", account.category?.title)
    }

    @Test
    fun notFound() {
        val ex = assertThrows<HttpClientErrorException> {
            rest.getForEntity(url("xxxx"), GetCategoryResponse::class.java)
        }

        // THEN
        assertEquals(HttpStatus.NOT_FOUND, ex.statusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(ErrorURN.ACCOUNT_NOT_FOUND.urn, response.error.code)
    }

    private fun url(name: String) = "http://localhost:$port/v1/accounts/@$name"
}
