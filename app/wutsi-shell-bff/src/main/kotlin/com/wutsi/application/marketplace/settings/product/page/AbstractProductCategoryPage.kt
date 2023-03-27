package com.wutsi.application.marketplace.settings.product.page

import com.wutsi.application.Page
import com.wutsi.application.Theme
import com.wutsi.application.common.page.AbstractPageEndpoint
import com.wutsi.flutter.sdui.Flexible
import com.wutsi.flutter.sdui.ListItem
import com.wutsi.flutter.sdui.ListView
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.marketplace.manager.MarketplaceManagerApi
import com.wutsi.marketplace.manager.dto.SearchCategoryRequest
import org.springframework.beans.factory.annotation.Autowired
import javax.servlet.http.HttpServletRequest

abstract class AbstractProductCategoryPage : AbstractPageEndpoint() {
    companion object {
        const val LIMIT = 200
    }

    @Autowired
    private lateinit var httpRequest: HttpServletRequest

    @Autowired
    protected lateinit var marketplaceManagerApi: MarketplaceManagerApi

    protected abstract fun getParentCategoryId(): Long?
    protected abstract fun getSubmitUrl(): String

    override fun getBaseId() = Page.SETTINGS_CATALOG_CATEGORY
    override fun getTitle() = getText("page.settings.store.category.title")
    override fun showDividerBeforeBody() = true

    override fun getSubTitle(): String? {
        val parentId = getParentCategoryId()
        return parentId?.let {
            marketplaceManagerApi.getCategory(it).category
        }?.longTitle
    }

    override fun getBody(): WidgetAware {
        val parentId = getParentCategoryId()
        val categories = marketplaceManagerApi.searchCategory(
            request = SearchCategoryRequest(
                level = if (parentId == null) 0 else null,
                parentId = parentId,
                limit = LIMIT,
            ),
        ).categories

        return Flexible(
            child = ListView(
                separator = true,
                separatorColor = Theme.COLOR_DIVIDER,
                children = categories.map {
                    ListItem(
                        caption = it.title,
                        iconRight = Theme.ICON_CHEVRON_RIGHT,
                        action = executeCommand(
                            url = urlBuilder.build(getSubmitUrl()),
                            parameters = mapOf(
                                "product-id" to getProductId().toString(),
                                "category-id" to it.id.toString(),
                            ),
                        ),
                    )
                },
            ),
        )
    }

    override fun getButton(): WidgetAware? = null

    private fun getProductId(): Long =
        httpRequest.getParameter("id").toLong()
}
