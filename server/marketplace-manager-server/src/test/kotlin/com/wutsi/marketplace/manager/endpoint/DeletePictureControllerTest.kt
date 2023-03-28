package com.wutsi.marketplace.manager.endpoint

import com.nhaarman.mockitokotlin2.verify
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DeletePictureControllerTest : AbstractSecuredControllerTest() {
    companion object {
        const val PICTURE_ID = 111L
    }

    @LocalServerPort
    val port: Int = 0

    fun url() = "http://localhost:$port/v1/pictures/$PICTURE_ID"

    @Test
    fun delete() {
        rest.delete(url())

        verify(marketplaceAccessApi).deletePicture(PICTURE_ID)
    }
}
