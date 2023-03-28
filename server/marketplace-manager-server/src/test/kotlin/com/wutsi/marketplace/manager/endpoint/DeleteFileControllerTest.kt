package com.wutsi.marketplace.manager.endpoint

import com.nhaarman.mockitokotlin2.verify
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DeleteFileControllerTest : AbstractSecuredControllerTest() {
    companion object {
        const val FILE_ID = 111L
    }

    @LocalServerPort
    val port: Int = 0

    private fun url() = "http://localhost:$port/v1/files/$FILE_ID"

    @Test
    fun delete() {
        rest.delete(url())

        verify(marketplaceAccessApi).deleteFile(FILE_ID)
    }
}
