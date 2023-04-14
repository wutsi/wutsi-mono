package com.wutsi.application.marketplace.settings.product.screen

import com.wutsi.application.Page
import com.wutsi.application.Theme
import com.wutsi.application.common.endpoint.AbstractSecuredEndpoint
import com.wutsi.application.util.ChartDataType
import com.wutsi.application.util.DateTimeUtil
import com.wutsi.application.util.HandleGenerator
import com.wutsi.application.util.KpiUtil
import com.wutsi.application.widget.KpiListWidget
import com.wutsi.application.widget.OrderWidget
import com.wutsi.application.widget.PictureListViewWidget
import com.wutsi.application.widget.PictureWidget
import com.wutsi.application.widget.UploadWidget
import com.wutsi.checkout.manager.CheckoutManagerApi
import com.wutsi.checkout.manager.dto.SearchOrderRequest
import com.wutsi.checkout.manager.dto.SearchSalesKpiRequest
import com.wutsi.enums.OrderStatus
import com.wutsi.enums.ProductStatus
import com.wutsi.enums.ProductType
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Button
import com.wutsi.flutter.sdui.Chart
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.DefaultTabController
import com.wutsi.flutter.sdui.Dialog
import com.wutsi.flutter.sdui.Divider
import com.wutsi.flutter.sdui.ExpandablePanel
import com.wutsi.flutter.sdui.Flexible
import com.wutsi.flutter.sdui.Icon
import com.wutsi.flutter.sdui.ListItem
import com.wutsi.flutter.sdui.ListView
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.SingleChildScrollView
import com.wutsi.flutter.sdui.TabBar
import com.wutsi.flutter.sdui.TabBarView
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.ActionType
import com.wutsi.flutter.sdui.enums.ButtonType
import com.wutsi.flutter.sdui.enums.CrossAxisAlignment
import com.wutsi.flutter.sdui.enums.MainAxisAlignment
import com.wutsi.marketplace.manager.MarketplaceManagerApi
import com.wutsi.marketplace.manager.dto.CreatePictureRequest
import com.wutsi.marketplace.manager.dto.PictureSummary
import com.wutsi.marketplace.manager.dto.Product
import com.wutsi.membership.manager.dto.Member
import com.wutsi.platform.core.image.ImageService
import com.wutsi.platform.core.storage.StorageService
import com.wutsi.regulation.RegulationEngine
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Path
import java.time.Clock
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.UUID

@RestController
@RequestMapping("/settings/2/products")
class SettingsV2ProductScreen(
    private val marketplaceManagerApi: MarketplaceManagerApi,
    private val checkoutManagerApi: CheckoutManagerApi,
    private val regulationEngine: RegulationEngine,
    private val storageService: StorageService,
    private val imageService: ImageService,
    private val clock: Clock,

    @Value("\${wutsi.store.pictures.max-width}") private val pictureMaxWidth: Int,
    @Value("\${wutsi.store.pictures.max-width}") private val pictureMaxHeight: Int,
    @Value("\${wutsi.application.webapp-url}") private val webAppUrl: String,
) : AbstractSecuredEndpoint() {
    @PostMapping
    fun index(
        @RequestParam id: Long,
        @RequestParam(required = false) errors: Array<String>? = null,
    ): Widget {
        val member = getCurrentMember()
        val product = marketplaceManagerApi.getProduct(id).product

        val tabs = TabBar(
            tabs = listOfNotNull(
                Text(getText("page.settings.store.product.tab.information").uppercase(), bold = true),
                Text(getText("page.settings.store.product.tab.orders").uppercase(), bold = true),
                Text(getText("page.settings.store.product.tab.stats").uppercase(), bold = true),
            ),
        )
        val tabViews = TabBarView(
            children = listOfNotNull(
                toInfoTab(product, member),
                toOrdersTab(product, member),
                toStatsTab(product, member),
            ),
        )

        return DefaultTabController(
            id = Page.SETTINGS_CATALOG_PRODUCT,
            length = tabs.tabs.size,
            child = Screen(
                backgroundColor = Theme.COLOR_WHITE,
                appBar = AppBar(
                    elevation = 0.0,
                    backgroundColor = Theme.COLOR_PRIMARY,
                    foregroundColor = Theme.COLOR_WHITE,
                    title = getText("page.settings.store.product.app-bar.title"),
                    bottom = tabs,
                ),
                child = tabViews,
            ),
        ).toWidget()
    }

    private fun toInfoTab(product: Product, member: Member): WidgetAware {
        val country = regulationEngine.country(member.country)
        val price = product.price?.let { country.createMoneyFormat().format(it) }
        val dateFormat = DateTimeFormatter.ofPattern(country.dateTimeFormat, LocaleContextHolder.getLocale())

        return Column(
            mainAxisAlignment = MainAxisAlignment.start,
            crossAxisAlignment = CrossAxisAlignment.start,
            children = listOfNotNull(
                toPictureListViewWidget(product),
                toCTAWidget(product),
                Divider(color = Theme.COLOR_DIVIDER, height = 1.0),
                Flexible(
                    flex = 10,
                    child = ListView(
                        separatorColor = Theme.COLOR_DIVIDER,
                        separator = true,
                        children = listOfNotNull(
                            toListItemWidget(
                                "page.settings.catalog.product.attribute.type",
                                getText("product.type.${product.type}"),
                                urlBuilder.build("${Page.getSettingsProductEditorUrl()}?name=type&id=${product.id}"),
                            ),
                            toListItemWidget(
                                "page.settings.catalog.product.attribute.title",
                                product.title,
                                urlBuilder.build("${Page.getSettingsProductEditorUrl()}?name=title&id=${product.id}"),
                            ),
                            toListItemWidget(
                                "page.settings.catalog.product.attribute.price",
                                price,
                                urlBuilder.build("${Page.getSettingsProductEditorUrl()}?name=price&id=${product.id}"),
                            ),
                            toListItemWidget(
                                "page.settings.catalog.product.attribute.quantity",
                                product.quantity?.toString(),
                                urlBuilder.build("${Page.getSettingsProductEditorUrl()}?name=quantity&id=${product.id}"),
                            ),
                            toListItemWidget(
                                "page.settings.catalog.product.attribute.category",
                                product.category?.longTitle,
                                urlBuilder.build("${Page.getSettingsProductCategoryUrl()}?id=${product.id}"),
                            ),
                            toListItemWidget(
                                "page.settings.catalog.product.attribute.summary",
                                description(product.summary),
                                urlBuilder.build("${Page.getSettingsProductEditorUrl()}?name=summary&id=${product.id}"),
                            ),
                            toListItemWidget(
                                "page.settings.catalog.product.attribute.description",
                                description(product.description),
                                urlBuilder.build("${Page.getSettingsProductEditorUrl()}?name=description&id=${product.id}"),
                            ),

                            if (product.type == ProductType.EVENT.name) {
                                ListItem(
                                    leading = Icon(
                                        code = Theme.ICON_EVENT,
                                        size = 32.0,
                                        color = Theme.COLOR_PRIMARY,
                                    ),
                                    caption = getText("page.settings.catalog.product.attribute.event"),
                                    subCaption = product.event?.starts?.let {
                                        DateTimeUtil.convert(it, member.timezoneId).format(dateFormat)
                                    },
                                    trailing = Icon(
                                        code = Theme.ICON_EDIT,
                                        size = 24.0,
                                        color = Theme.COLOR_BLACK,
                                    ),
                                    action = Action(
                                        type = ActionType.Route,
                                        url = urlBuilder.build("${Page.getSettingsProductEditorUrl()}/event?id=${product.id}"),
                                    ),
                                )
                            } else {
                                null
                            },

                            if (product.type == ProductType.DIGITAL_DOWNLOAD.name) {
                                ListItem(
                                    caption = getText("page.settings.catalog.product.attribute.file"),
                                    subCaption = getText(
                                        "page.settings.catalog.product.n_files",
                                        arrayOf(product.files.size),
                                    ),
                                    leading = Icon(
                                        code = Theme.ICON_FILES,
                                        size = 32.0,
                                        color = Theme.COLOR_PRIMARY,
                                    ),
                                    trailing = Icon(
                                        code = Theme.ICON_EDIT,
                                        size = 24.0,
                                        color = Theme.COLOR_BLACK,
                                    ),
                                    action = Action(
                                        type = ActionType.Route,
                                        url = urlBuilder.build("${Page.getSettingsProductEditorUrl()}/files?id=${product.id}"),
                                    ),
                                )
                            } else {
                                null
                            },

                            toDangerWidget(product),
                        ),
                    ),
                ),
            ),
        )
    }

    private fun toOrdersTab(product: Product, member: Member): WidgetAware {
        val today = OffsetDateTime.now()
        val orders = checkoutManagerApi.searchOrder(
            request = SearchOrderRequest(
                productId = product.id,
                createdFrom = today.minusDays(28),
                createdTo = today,
                status = listOf(
                    OrderStatus.OPENED.name,
                    OrderStatus.IN_PROGRESS.name,
                    OrderStatus.COMPLETED.name,
                    OrderStatus.CANCELLED.name,
                ),
            ),
        ).orders

        return Column(
            children = listOf(
                Container(
                    padding = 10.0,
                    child = Text(
                        caption = getText(
                            "page.settings.store.product.n_orders-in-past-28d",
                            arrayOf(orders.size),
                        ),
                    ),
                ),
                Divider(color = Theme.COLOR_DIVIDER, height = 1.0),
                Flexible(
                    child = ListView(
                        separatorColor = Theme.COLOR_DIVIDER,
                        separator = true,
                        children = orders.map {
                            OrderWidget.of(
                                order = it,
                                country = regulationEngine.country(member.country),
                                action = gotoUrl(
                                    url = urlBuilder.build(Page.getOrderUrl()),
                                    parameters = mapOf("id" to it.id),
                                ),
                                imageService = imageService,
                                showProductImage = false,
                                timezoneId = member.timezoneId,
                            )
                        },
                    ),
                ),
            ),
        )
    }

    private fun toStatsTab(product: Product, member: Member): WidgetAware {
        val today = LocalDate.ofInstant(clock.instant(), ZoneOffset.UTC)
        return SingleChildScrollView(
            child = Column(
                children = listOfNotNull(
                    toKpiWidget(member, product),
                    toChartWidget(product, product.created.toLocalDate(), today),
                ),
            ),
        )
    }

    private fun toKpiWidget(
        member: Member,
        product: Product,
    ): WidgetAware {
        val kpis = checkoutManagerApi.searchSalesKpi(
            request = SearchSalesKpiRequest(
                aggregate = true,
                productId = product.id,
            ),
        ).kpis
        if (kpis.isEmpty()) {
            return Container()
        }

        val country = regulationEngine.country(member.country)
        val kpi = kpis[0]
        return Container(
            margin = 10.0,
            border = 1.0,
            borderColor = Theme.COLOR_DIVIDER,
            child = KpiListWidget.of(kpi, country),
        )
    }

    private fun toChartWidget(
        product: Product,
        from: LocalDate,
        to: LocalDate,
    ): WidgetAware {
        val request = SearchSalesKpiRequest(
            productId = product.id,
            fromDate = from,
            toDate = to,
        )
        val kpis = checkoutManagerApi.searchSalesKpi(request = request).kpis

        return Column(
            children = listOf(
                Container(
                    border = 1.0,
                    margin = 10.0,
                    borderColor = Theme.COLOR_DIVIDER,
                    child = Chart(
                        title = getText("page.settings.store.product.stats-orders"),
                        series = listOf(
                            KpiUtil.toSalesChartDataList(kpis, from, to, ChartDataType.ORDERS),
                        ),
                    ),
                ),
                Container(
                    border = 1.0,
                    margin = 10.0,
                    borderColor = Theme.COLOR_DIVIDER,
                    child = Chart(
                        title = getText("page.settings.store.product.stats-views"),
                        series = listOf(
                            KpiUtil.toSalesChartDataList(kpis, from, to, ChartDataType.VIEWS),
                        ),
                    ),
                ),
            ),
        )
    }

    private fun toCTAWidget(product: Product): WidgetAware? =
        if (product.status == ProductStatus.DRAFT.name) {
            Container(
                padding = 10.0,
                child = Button(
                    caption = getText("page.settings.catalog.product.button.publish"),
                    action = executeCommand(
                        url = urlBuilder.build("${Page.getSettingsProductUrl()}/publish?id=${product.id}"),
                    ),
                ),
            )
        } else if (product.status == ProductStatus.PUBLISHED.name) {
            Container(
                padding = 10.0,
                child = Button(
                    caption = getText("page.settings.catalog.product.button.share"),
                    action = Action(
                        type = ActionType.Share,
                        message = "$webAppUrl/p/${product.id}/" + HandleGenerator.generate(product.title),
                    ),
                ),
            )
        } else {
            null
        }

    private fun toDangerWidget(product: Product): WidgetAware =
        Container(
            padding = 10.0,
            child = ExpandablePanel(
                header = getText("page.settings.catalog.product.button.more"),
                expanded = Container(
                    padding = 20.0,
                    borderColor = Theme.COLOR_DANGER,
                    border = 1.0,
                    background = Theme.COLOR_DANGER_LIGHT,
                    child = Column(
                        children = listOfNotNull(
                            if (product.status == "PUBLISHED") {
                                Button(
                                    caption = getText("page.settings.catalog.product.button.unpublish"),
                                    action = executeCommand(
                                        url = urlBuilder.build("${Page.getSettingsProductUrl()}/unpublish?id=${product.id}"),
                                        confirm = getText("page.settings.catalog.product.confirm-unpublish"),
                                    ),
                                )
                            } else {
                                null
                            },

                            if (product.status == "PUBLISHED") {
                                Container(padding = 10.0)
                            } else {
                                null
                            },

                            Button(
                                caption = getText("page.settings.catalog.product.button.delete"),
                                action = executeCommand(
                                    url = urlBuilder.build("${Page.getSettingsProductUrl()}/delete?id=${product.id}"),
                                    confirm = getText("page.settings.catalog.product.confirm-delete"),
                                ),
                            ),
                        ),
                    ),
                ),
            ),
        )

    private fun toListItemWidget(caption: String, value: String?, url: String) = ListItem(
        caption = getText(caption),
        subCaption = value,
        trailing = Icon(
            code = Theme.ICON_EDIT,
            size = 24.0,
            color = Theme.COLOR_BLACK,
        ),
        action = Action(
            type = ActionType.Route,
            url = url,
        ),
    )

    private fun toPictureListViewWidget(product: Product): WidgetAware {
        val images = mutableListOf<PictureWidget>()

        // Thumbnail as 1st image
        if (product.thumbnail != null) {
            images.add(toPictureWidget(product.thumbnail!!))
        }

        // Other pictures
        images.addAll(
            product.pictures
                .filter { it.id != product.thumbnail?.id }
                .map {
                    toPictureWidget(it)
                },
        )

        return Container(
            padding = 10.0,
            height = PictureListViewWidget.IMAGE_HEIGHT + 2 * (10.0 + PictureListViewWidget.IMAGE_PADDING),
            child = PictureListViewWidget(
                children = images,
                action = if (product.pictures.size < regulationEngine.maxPictures()) {
                    Action(
                        type = ActionType.Prompt,
                        prompt = toUploadDialogWidget(product).toWidget(),
                    )
                } else {
                    null
                },
            ),
        )
    }

    private fun toPictureWidget(picture: PictureSummary) = PictureWidget(
        padding = PictureListViewWidget.IMAGE_PADDING,
        width = PictureListViewWidget.IMAGE_WIDTH,
        height = PictureListViewWidget.IMAGE_HEIGHT,
        border = 1.0,
        url = picture.url,
        action = gotoUrl(
            urlBuilder.build(
                "${Page.getSettingsProductUrl()}/pictures?id=${picture.id}&url=" + encodeURLParam(
                    picture.url,
                ),
            ),
        ),
    )

    private fun toUploadDialogWidget(product: Product) = Dialog(
        title = getText("page.settings.catalog.product.add-picture"),
        actions = listOf(
            UploadWidget(
                name = "file",
                uploadUrl = urlBuilder.build("${Page.getSettingsProductUrl()}/upload?id=${product.id}"),
                imageMaxWidth = pictureMaxWidth,
                imageMaxHeight = pictureMaxHeight,
                action = gotoUrl(
                    url = urlBuilder.build("${Page.getSettingsProductUrl()}?id=${product.id}"),
                    replacement = true,
                ),
            ),
            Button(
                type = ButtonType.Text,
                caption = getText("page.settings.catalog.product.button.cancel"),
            ),
        ),
    )

    private fun description(value: String?): String? =
        if (value == null) {
            null
        } else if (value.length < 160) {
            value.replace('\n', ' ')
        } else {
            value.substring(0, 160).replace('\n', ' ') + "..."
        }

    @PostMapping("/upload")
    fun upload(@RequestParam id: Long, @RequestParam file: MultipartFile) {
        val contentType = Files.probeContentType(Path.of(file.originalFilename))
        val path = "product/$id/picture/${UUID.randomUUID()}/${file.originalFilename}"
        val url = storageService.store(path, file.inputStream, contentType)

        marketplaceManagerApi.createPicture(
            request = CreatePictureRequest(
                productId = id,
                url = url.toString(),
            ),
        )
    }

    @PostMapping("/publish")
    fun publish(@RequestParam id: Long): Action {
        marketplaceManagerApi.publishProduct(id)
        return gotoUrl(
            url = urlBuilder.build("${Page.getSettingsProductUrl()}?id=$id"),
            replacement = true,
        )
    }

    @PostMapping("/unpublish")
    fun unpublish(@RequestParam id: Long): Action {
        marketplaceManagerApi.unpublishProduct(id)
        return gotoUrl(
            url = urlBuilder.build("${Page.getSettingsProductUrl()}?id=$id"),
            replacement = true,
        )
    }

    @PostMapping("/delete")
    fun delete(@RequestParam id: Long): Action {
        marketplaceManagerApi.deleteProduct(id)
        return gotoPreviousScreen()
    }
}
