package com.wutsi.application.membership.settings.picture.screen

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.AbstractSecuredEndpointTest
import com.wutsi.application.Page
import com.wutsi.membership.manager.dto.UpdateMemberAttributeRequest
import com.wutsi.platform.core.storage.StorageService
import org.junit.jupiter.api.Test
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.server.LocalServerPort
import java.net.URL
import kotlin.test.assertTrue

internal class SettingsV2PictureScreenTest : AbstractSecuredEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    @MockBean
    private lateinit var storageService: StorageService

    private fun url(action: String = "") =
        "http://localhost:$port${Page.getSettingsUrl()}/picture$action"

    @Test
    fun index() =
        assertEndpointEquals("/membership/settings/picture/screens/picture.json", url())

    @Test
    fun upload() {
        // GIVEN
        val filename = "toto.png"
        val fileUrl = URL("http://www.wutsi.com/asset/1/$filename")
        doReturn(fileUrl).whenever(storageService).store(any(), any(), anyOrNull(), anyOrNull(), anyOrNull())

        // WHEN
        uploadFile(url("/upload"), "toto.png")

        // THEN
        val path = argumentCaptor<String>()
        verify(storageService).store(path.capture(), any(), eq("image/png"), anyOrNull(), anyOrNull())
        assertTrue(path.firstValue.startsWith("user/${member.id}/picture/"))
        assertTrue(path.firstValue.endsWith(filename))

        verify(membershipManagerApi).updateMemberAttribute(
            request = UpdateMemberAttributeRequest(
                name = "picture-url",
                value = fileUrl.toString(),
            ),
        )
    }
}
