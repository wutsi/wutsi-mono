package com.wutsi.marketplace.manager.endpoint

import com.nhaarman.mockitokotlin2.verify
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DeleteFileControllerTest : AbstractProductControllerTest<Void>() {
    companion object {
        const val FILE_ID = 111L
    }

    override fun url() = "http://localhost:$port/v1/files/$FILE_ID"

    override fun createRequest(): Void? = null

    override fun submit() {
        rest.delete(url())
    }

    @Test
    fun delete() {
        submit()

        verify(marketplaceAccessApi).deleteFile(FILE_ID)
    }

    override fun notProductOwner() {
        // IGNORE THIS TEST
    }
}
