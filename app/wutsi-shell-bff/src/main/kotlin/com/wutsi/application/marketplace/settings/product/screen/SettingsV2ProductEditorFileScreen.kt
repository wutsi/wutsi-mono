package com.wutsi.application.marketplace.settings.product.screen

import com.wutsi.application.Page
import com.wutsi.application.Theme
import com.wutsi.application.common.endpoint.AbstractEndpoint
import com.wutsi.application.util.NumberUtil
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Divider
import com.wutsi.flutter.sdui.Flexible
import com.wutsi.flutter.sdui.Icon
import com.wutsi.flutter.sdui.Image
import com.wutsi.flutter.sdui.Input
import com.wutsi.flutter.sdui.ListItem
import com.wutsi.flutter.sdui.ListView
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.InputType
import com.wutsi.marketplace.manager.MarketplaceManagerApi
import com.wutsi.marketplace.manager.dto.CreateFileRequest
import com.wutsi.platform.core.storage.StorageService
import com.wutsi.regulation.RegulationEngine
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
@RequestMapping("/settings/2/products/editor/files")
class SettingsV2ProductEditorFileScreen(
    private val marketplaceManagerApi: MarketplaceManagerApi,
    private val regulationEngine: RegulationEngine,
    private val storageService: StorageService,

    @Value("\${wutsi.application.asset-url}") private val assetUrl: String,
) : AbstractEndpoint() {
    @PostMapping
    fun index(@RequestParam id: Long): Widget {
        val product = marketplaceManagerApi.getProduct(id).product
        val children = mutableListOf<WidgetAware>()
        children.addAll(
            product.files.map {
                ListItem(
                    caption = it.name,
                    subCaption = NumberUtil.toHumanReadableByteCountSI(it.contentSize.toLong()),
                    trailing = Container(
                        padding = 10.0,
                        child = Icon(
                            code = Theme.ICON_DELETE,
                            color = Theme.COLOR_DANGER,
                            size = 24.0,
                        ),
                        action = executeCommand(
                            url = urlBuilder.build("${Page.getSettingsProductEditorUrl()}/files/delete"),
                            parameters = mapOf(
                                "id" to id.toString(),
                                "file-id" to it.id.toString(),
                            ),
                            confirm = getText("page.settings.catalog.file.confirm.delete", arrayOf(it.name)),
                        ),
                    ),
                    leading = toExtension(it.name)?.let {
                        Image(
                            width = 32.0,
                            height = 32.0,
                            url = "$assetUrl/images/file-types/$it.png",
                        )
                    },
                    action = navigateTo(it.url),
                )
            },
        )
        if (product.files.size < regulationEngine.maxDigitalDownloadFiles()) {
            children.add(
                Container(
                    padding = 10.0,
                    child = Input(
                        name = "file",
                        caption = getText("page.settings.catalog.file.button.add"),
                        type = InputType.File,
                        uploadUrl = urlBuilder.build("${Page.getSettingsProductEditorUrl()}/files/upload?id=$id"),
                        action = gotoUrl(
                            url = urlBuilder.build("${Page.getSettingsProductEditorUrl()}/files"),
                            parameters = mapOf(
                                "id" to id.toString(),
                            ),
                            replacement = true,
                        ),
                    ),
                ),
            )
        }

        return Screen(
            id = Page.SETTINGS_CATALOG_EDITOR_FILE,
            backgroundColor = Theme.COLOR_WHITE,
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.COLOR_WHITE,
                foregroundColor = Theme.COLOR_BLACK,
                title = getText("page.settings.catalog.file.app-bar.title"),
            ),
            child = Column(
                children = listOf(
                    Container(
                        padding = 10.0,
                        child = Text(
                            caption = getText("page.settings.catalog.file.n_files", arrayOf(product.files.size)),
                        ),
                    ),
                    Divider(color = Theme.COLOR_DIVIDER),
                    Flexible(
                        child = ListView(
                            separator = true,
                            separatorColor = Theme.COLOR_DIVIDER,
                            children = children,
                        ),
                    ),
                ),
            ),
        ).toWidget()
    }

    private fun toExtension(filename: String): String? {
        val i = filename.lastIndexOf(".")
        return if (i > 0) {
            filename.substring(i + 1).lowercase()
        } else {
            null
        }
    }

    @PostMapping("/delete")
    fun delete(@RequestParam id: Long, @RequestParam("file-id") fileId: Long): Action {
        marketplaceManagerApi.deleteFile(fileId)
        return gotoUrl(
            url = urlBuilder.build("${Page.getSettingsProductEditorUrl()}/files"),
            parameters = mapOf(
                "id" to id.toString(),
            ),
            replacement = true,
        )
    }

    @PostMapping("/upload")
    fun upload(@RequestParam id: Long, @RequestParam file: MultipartFile) {
        val contentType = Files.probeContentType(Path.of(file.originalFilename))
        val path = "product/$id/file/${UUID.randomUUID()}/${file.originalFilename}"
        val url = storageService.store(path, file.inputStream, contentType)

        marketplaceManagerApi.createFile(
            request = CreateFileRequest(
                productId = id,
                url = url.toString(),
                contentType = contentType,
                contentSize = file.size.toInt(),
                name = file.originalFilename,
            ),
        )
    }
}
