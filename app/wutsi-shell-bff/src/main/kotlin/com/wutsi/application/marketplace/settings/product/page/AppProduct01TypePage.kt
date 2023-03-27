package com.wutsi.application.marketplace.settings.product.page

import com.wutsi.application.Page
import com.wutsi.application.common.page.AbstractPageEndpoint
import com.wutsi.application.marketplace.service.ProductEditorWidgetProvider
import com.wutsi.application.marketplace.settings.product.dao.PictureRepository
import com.wutsi.application.marketplace.settings.product.dto.SubmitAttributeRequest
import com.wutsi.application.widget.PictureWidget
import com.wutsi.enums.ProductType
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Input
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.InputType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings/2/products/add/pages/type")
class AppProduct01TypePage(
    private val dao: PictureRepository,
    private val editor: ProductEditorWidgetProvider,
) : AbstractPageEndpoint() {
    companion object {
        const val PAGE_INDEX = 1
    }

    override fun getBaseId() = com.wutsi.application.Page.SETTINGS_CATALOG_ADD
    override fun getPageIndex() = PAGE_INDEX
    override fun getTitle() = getText("page.settings.catalog.add.type.title")

    override fun getBody(): WidgetAware {
        val picture = dao.get()

        return Column(
            children = listOfNotNull(
                picture.url?.let {
                    PictureWidget(url = it, width = 100.0, height = 100.0)
                },
                Container(
                    padding = 10.0,
                    child = editor.get("type", null),
                ),
            ),
        )
    }

    override fun getButton() = Input(
        name = "submit",
        type = InputType.Submit,
        caption = getText("page.settings.catalog.add.button.save"),
        action = executeCommand(
            url = urlBuilder.build("${Page.getSettingsProductAddUrl()}/pages/type/submit"),
        ),
    )

    @PostMapping("/submit")
    fun submit(@RequestBody request: SubmitAttributeRequest): Action {
        val picture = dao.get()
        picture.type = ProductType.valueOf(request.value)
        dao.save(picture)
        return gotoNextPage()
    }
}
