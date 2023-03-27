package com.wutsi.application.marketplace.settings.product.page

import com.wutsi.application.Page
import com.wutsi.application.common.page.AbstractPageEndpoint
import com.wutsi.application.marketplace.settings.product.dao.PictureRepository
import com.wutsi.application.marketplace.settings.product.entity.PictureEntity
import com.wutsi.application.widget.UploadWidget
import com.wutsi.flutter.sdui.Button
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.ButtonType
import com.wutsi.platform.core.storage.StorageService
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Path
import java.util.UUID

@RestController
@RequestMapping("/settings/2/products/add/pages/picture")
class AppProduct00PicturePage(
    private val dao: PictureRepository,
    private val storageService: StorageService,
    @Value("\${wutsi.store.pictures.max-width}") private val pictureMaxWidth: Int,
    @Value("\${wutsi.store.pictures.max-width}") private val pictureMaxHeight: Int,
) : AbstractPageEndpoint() {
    companion object {
        const val PAGE_INDEX = 0
    }

    override fun getBaseId() = com.wutsi.application.Page.SETTINGS_CATALOG_ADD
    override fun getPageIndex() = PAGE_INDEX

    override fun getTitle() = getText("page.settings.catalog.add.picture.title")

    override fun getBody(): WidgetAware =
        Column(
            children = listOf(
                Container(
                    padding = 20.0,
                    child = UploadWidget(
                        name = "file",
                        uploadUrl = urlBuilder.build("${Page.getSettingsProductAddUrl()}/pages/picture/upload"),
                        imageMaxWidth = pictureMaxWidth,
                        imageMaxHeight = pictureMaxHeight,
                        action = gotoNextPage(),
                    ),
                ),
                Button(
                    type = ButtonType.Text,
                    caption = getText("page.settings.catalog.add.button.skip"),
                    action = gotoNextPage(),
                ),
            ),
        )

    override fun getButton(): Button? = null

    @PostMapping("/upload")
    fun upload(@RequestParam file: MultipartFile) {
        val contentType = Files.probeContentType(Path.of(file.originalFilename))
        val path = "product/picture/${UUID.randomUUID()}/${file.originalFilename}"
        val url = storageService.store(path, file.inputStream, contentType)

        dao.save(PictureEntity(url.toString()))
    }
}
