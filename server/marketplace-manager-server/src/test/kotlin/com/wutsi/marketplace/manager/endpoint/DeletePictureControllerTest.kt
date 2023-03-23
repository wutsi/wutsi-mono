package com.wutsi.marketplace.manager.endpoint

import com.nhaarman.mockitokotlin2.verify
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DeletePictureControllerTest : AbstractProductControllerTest<Void>() {
    companion object {
        const val PICTURE_ID = 111L
    }

    override fun url() = "http://localhost:$port/v1/pictures/$PICTURE_ID"

    override fun createRequest(): Void? = null

    override fun submit() {
        rest.delete(url())
    }

    @Test
    fun delete() {
        submit()

        verify(marketplaceAccessApi).deletePicture(PICTURE_ID)
    }

    override fun notProductOwner() {
        // IGNORE THIS TEST
    }
}
