package com.wutsi.membership.access.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.enums.AccountStatus
import com.wutsi.membership.access.dao.AccountRepository
import com.wutsi.membership.access.dao.PhoneRepository
import com.wutsi.membership.access.dto.CreateAccountRequest
import com.wutsi.membership.access.dto.CreateAccountResponse
import com.wutsi.membership.access.error.ErrorURN
import com.wutsi.platform.core.error.ErrorResponse
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/CreateAccountController.sql"])
class CreateAccountControllerTest {
    @LocalServerPort
    val port: Int = 0

    protected val rest = RestTemplate()

    @Autowired
    private lateinit var dao: AccountRepository

    @Autowired
    private lateinit var phoneDao: PhoneRepository

    @Test
    fun create() {
        // WHEN
        val request = CreateAccountRequest(
            phoneNumber = "+23774511111",
            language = "fr",
            country = "CM",
            displayName = "Ray Sponsible",
            pictureUrl = "https://www.google.ca/img/1.ong",
            cityId = 100L,
        )
        val response = rest.postForEntity(url(), request, CreateAccountResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val account = dao.findById(response.body!!.accountId).get()

        assertEquals(request.displayName, account.displayName)
        assertEquals(request.pictureUrl, account.pictureUrl)
        assertEquals(request.language, account.language)
        assertEquals(request.country, account.country)
        assertEquals(request.cityId, account.city?.id)
        assertEquals(AccountStatus.ACTIVE, account.status)
        assertEquals("Africa/Douala", account.timezoneId)
        assertNotNull(account.created)
        assertNotNull(account.updated)

        val phone = phoneDao.findById(account.phone.id).get()
        assertEquals(request.phoneNumber, phone.number)
        assertEquals("CM", phone.country)
        assertNotNull(phone.created)
    }

    @Test
    fun createWithNoCity() {
        // WHEN
        val request = CreateAccountRequest(
            phoneNumber = "+23774511111",
            language = "fr",
            country = "CM",
            displayName = "Ray Sponsible",
            pictureUrl = "https://www.google.ca/img/1.ong",
            cityId = null,
        )
        val response = rest.postForEntity(url(), request, CreateAccountResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val account = dao.findById(response.body!!.accountId).get()

        assertEquals("Africa/Douala", account.timezoneId)
    }

    @Test
    fun `create account with existing phone number`() {
        // WHEN
        val request = CreateAccountRequest(
            phoneNumber = "+237221234100",
            language = "fr",
            country = "CM",
            displayName = "James Bond",
            pictureUrl = "https://www.google.ca/img/1.ong",
            cityId = 100L,
        )
        val response = rest.postForEntity(url(), request, CreateAccountResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val account = dao.findById(response.body!!.accountId).get()

        assertEquals(request.displayName, account.displayName)
        assertEquals(request.pictureUrl, account.pictureUrl)
        assertEquals(request.language, account.language)
        assertEquals(request.country, account.country)
        assertEquals(request.cityId, account.city?.id)
        assertEquals(100L, account.phone.id)
        assertEquals(AccountStatus.ACTIVE, account.status)
        assertEquals("Africa/Douala", account.timezoneId)
        assertNotNull(account.created)
        assertNotNull(account.updated)
    }

    @Test
    fun `create account with phone number associated with suspended account`() {
        // WHEN
        val request = CreateAccountRequest(
            phoneNumber = "+237221234300",
            language = "fr",
            country = "CM",
            displayName = "Omer Simpson",
            pictureUrl = "https://www.google.ca/img/1.ong",
            cityId = 100L,
        )
        val response = rest.postForEntity(url(), request, CreateAccountResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val account = dao.findById(response.body!!.accountId).get()

        assertEquals(request.displayName, account.displayName)
        assertEquals(request.pictureUrl, account.pictureUrl)
        assertEquals(request.language, account.language)
        assertEquals(request.country, account.country)
        assertEquals(request.cityId, account.city?.id)
        assertEquals(300L, account.phone.id)
        assertEquals(AccountStatus.ACTIVE, account.status)
        assertEquals("Africa/Douala", account.timezoneId)
        assertNotNull(account.created)
        assertNotNull(account.updated)
    }

    @Test
    fun `create account with phone already assigned`() {
        // WHEN
        val request = CreateAccountRequest(
            phoneNumber = "+237221234200",
            language = "fr",
            country = "CM",
            displayName = "X Y",
            pictureUrl = "https://www.google.ca/img/1.ong",
            cityId = 100L,
        )
        val ex = assertThrows<HttpClientErrorException> {
            rest.postForEntity(url(), request, CreateAccountResponse::class.java)
        }

        // THEN
        assertEquals(HttpStatus.CONFLICT, ex.statusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(ErrorURN.PHONE_NUMBER_ALREADY_ASSIGNED.urn, response.error.code)
    }

    private fun url() = "http://localhost:$port/v1/accounts"
}
