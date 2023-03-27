package com.wutsi.application.membership.settings.picture.screen

import com.wutsi.application.Page
import com.wutsi.application.Theme
import com.wutsi.application.common.endpoint.AbstractSecuredEndpoint
import com.wutsi.application.util.SecurityUtil
import com.wutsi.application.widget.UploadWidget
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Divider
import com.wutsi.flutter.sdui.IconButton
import com.wutsi.flutter.sdui.Image
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.enums.Alignment
import com.wutsi.flutter.sdui.enums.CrossAxisAlignment
import com.wutsi.membership.manager.dto.UpdateMemberAttributeRequest
import com.wutsi.platform.core.storage.StorageService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Path
import java.util.UUID

@RestController
@RequestMapping("/settings/2/picture")
class SettingsV2PictureScreen(
    private val storageService: StorageService,
) : AbstractSecuredEndpoint() {
    @PostMapping
    fun index(): Widget {
        val me = getCurrentMember()
        return Screen(
            id = Page.SETTINGS_PICTURE,
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.COLOR_WHITE,
                foregroundColor = Theme.COLOR_BLACK,
                title = getText("page.settings.picture.app-bar.title"),
                automaticallyImplyLeading = false,
                actions = listOf(
                    IconButton(
                        icon = Theme.ICON_CANCEL,
                        color = Theme.COLOR_BLACK,
                        action = gotoPreviousScreen(),
                    ),
                ),
            ),
            child = Column(
                crossAxisAlignment = CrossAxisAlignment.center,
                children = listOf(
                    Container(
                        padding = 10.0,
                        alignment = Alignment.Center,
                        child = Image(
                            url = me.pictureUrl ?: "",
                            width = 256.0,
                            height = 256.0,
                        ),
                    ),
                    Divider(color = Theme.COLOR_DIVIDER),
                    Container(
                        padding = 10.0,
                        child = UploadWidget(
                            name = "file",
                            uploadUrl = urlBuilder.build("${Page.getSettingsUrl()}/picture/upload"),
                            imageMaxWidth = 512,
                            imageMaxHeight = 512,
                            action = gotoPreviousScreen(),
                        ),
                    ),
                ),
            ),
        ).toWidget()
    }

    @PostMapping("/upload")
    fun upload(@RequestParam file: MultipartFile) {
        val contentType = Files.probeContentType(Path.of(file.originalFilename))
        logger.add("file_name", file.originalFilename)
        logger.add("file_content_type", contentType)

        // Upload file
        val memberId = SecurityUtil.getMemberId()
        val path = "user/$memberId/picture/${UUID.randomUUID()}-${file.originalFilename}"
        val url = storageService.store(path, file.inputStream, contentType)
        logger.add("picture_url", url)

        // Update user profile
        membershipManagerApi.updateMemberAttribute(
            request = UpdateMemberAttributeRequest(
                name = "picture-url",
                value = url.toString(),
            ),
        )
    }
}
