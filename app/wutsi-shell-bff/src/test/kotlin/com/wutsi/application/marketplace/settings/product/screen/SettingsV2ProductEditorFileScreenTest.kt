package com.wutsi.application.marketplace.settings.product.screen

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.AbstractSecuredEndpointTest
import com.wutsi.application.Fixtures
import com.wutsi.application.Page
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.marketplace.manager.dto.CreateFileRequest
import com.wutsi.marketplace.manager.dto.GetProductResponse
import com.wutsi.platform.core.storage.StorageService
import org.junit.jupiter.api.Test
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import java.net.URL
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class SettingsV2ProductEditorFileScreenTest : AbstractSecuredEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    @MockBean
    private lateinit var storageService: StorageService

    private val productId = 123L
    private val fileId = 555L

    private fun url(action: String = "", fileId: Long? = null) =
        "http://localhost:$port${Page.getSettingsProductEditorUrl()}/files$action?id=$productId" +
            (fileId?.let { "&file-id=$fileId" } ?: "")

    @Test
    fun list() {
        val product = Fixtures.createProduct(
            id = productId,
            pictures = Fixtures.createPictureSummaryList(2),
            files = listOf(
                Fixtures.createFileSummary(1L),
                Fixtures.createFileSummary(2L),
                Fixtures.createFileSummary(3L),
            ),
        )
        doReturn(GetProductResponse(product)).whenever(marketplaceManagerApi).getProduct(any())

        assertEndpointEquals("/marketplace/settings/product/screens/editor-digital-download.json", url())
    }

    @Test
    fun filesLimit() {
        val product = Fixtures.createProduct(
            id = productId,
            pictures = Fixtures.createPictureSummaryList(2),
            files = listOf(
                Fixtures.createFileSummary(1L),
                Fixtures.createFileSummary(2L),
                Fixtures.createFileSummary(3L),
                Fixtures.createFileSummary(4L),
                Fixtures.createFileSummary(5L),
            ),
        )
        doReturn(GetProductResponse(product)).whenever(marketplaceManagerApi).getProduct(any())

        assertEndpointEquals("/marketplace/settings/product/screens/editor-digital-download-limit.json", url())
    }

    @Test
    fun delete() {
        val response = rest.postForEntity(url(action = "/delete", fileId = fileId), null, Action::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val action = response.body!!
        assertEquals(ActionType.Route, action.type)
        assertEquals("http://localhost:0${Page.getSettingsProductEditorUrl()}/files", action.url)
        assertEquals(
            mapOf("id" to productId.toString()),
            action.parameters,
        )
        assertEquals(true, action.replacement)

        verify(marketplaceManagerApi).deleteFile(fileId)
    }

    @Test
    fun upload() {
        // GIVEN
        val filename = "toto.png"
        val fileUrl = URL("http://www.wutsi.com/asset/1/$filename")
        doReturn(fileUrl).whenever(storageService).store(any(), any(), anyOrNull(), anyOrNull(), anyOrNull())

        // WHEN
        uploadFile(url("/upload"), filename)

        // THEN
        val path = argumentCaptor<String>()
        verify(storageService).store(path.capture(), any(), eq("image/png"), anyOrNull(), anyOrNull())
        assertTrue(path.firstValue.startsWith("product/$productId/file/"))
        assertTrue(path.firstValue.endsWith(filename))

        verify(marketplaceManagerApi).createFile(
            request = CreateFileRequest(
                productId = productId,
                url = fileUrl.toString(),
                contentSize = 4,
                contentType = "image/png",
                name = filename,
            ),
        )
    }
}
