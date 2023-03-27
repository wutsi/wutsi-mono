package com.wutsi.application.marketplace.product.screen

import com.wutsi.application.Page
import com.wutsi.application.Theme
import com.wutsi.application.common.endpoint.AbstractEndpoint
import com.wutsi.application.util.DateTimeUtil
import com.wutsi.application.util.SecurityUtil
import com.wutsi.application.util.StringUtil
import com.wutsi.application.widget.BusinessToolbarWidget
import com.wutsi.enums.ProductType
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.AspectRatio
import com.wutsi.flutter.sdui.CarouselSlider
import com.wutsi.flutter.sdui.Chip
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Divider
import com.wutsi.flutter.sdui.ExpandablePanel
import com.wutsi.flutter.sdui.Icon
import com.wutsi.flutter.sdui.Image
import com.wutsi.flutter.sdui.MoneyText
import com.wutsi.flutter.sdui.Row
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.SingleChildScrollView
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.Wrap
import com.wutsi.flutter.sdui.enums.Axis
import com.wutsi.flutter.sdui.enums.CrossAxisAlignment
import com.wutsi.flutter.sdui.enums.MainAxisAlignment
import com.wutsi.flutter.sdui.enums.TextDecoration
import com.wutsi.marketplace.manager.MarketplaceManagerApi
import com.wutsi.marketplace.manager.dto.Offer
import com.wutsi.marketplace.manager.dto.Product
import com.wutsi.membership.manager.MembershipManagerApi
import com.wutsi.membership.manager.dto.Member
import com.wutsi.platform.core.image.Dimension
import com.wutsi.platform.core.image.ImageService
import com.wutsi.platform.core.image.Transformation
import com.wutsi.regulation.Country
import com.wutsi.regulation.RegulationEngine
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.format.DateTimeFormatter

@RestController
@RequestMapping("/products/2")
class ProductV2Screen(
    private val marketplaceManagerApi: MarketplaceManagerApi,
    private val membershipManagerApi: MembershipManagerApi,
    private val regulationEngine: RegulationEngine,
    private val imageService: ImageService,
) : AbstractEndpoint() {
    companion object {
        const val PICTURE_HEIGHT = 250.0
        const val PICTURE_ASPECT_RATIO_WIDTH = 8.0
        const val PICTURE_ASPECT_RATIO_HEIGHT = 10.0
    }

    @PostMapping
    fun index(@RequestParam id: Long): Widget {
        val offer = marketplaceManagerApi.getOffer(id).offer
        val product = offer.product
        val merchant = membershipManagerApi.getMember(product.store.accountId).member
        val country = regulationEngine.country(merchant.country)
        val member = membershipManagerApi.getMember(SecurityUtil.getMemberId()).member
        val descriptionWidget = toDescriptionWidget(product)
        val deliveryWidget = toDeliveryWidget(product)

        // Screen
        return Screen(
            id = Page.PRODUCT,
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.COLOR_WHITE,
                foregroundColor = Theme.COLOR_BLACK,
                title = merchant.displayName,
            ),
            bottomNavigationBar = createBottomNavigationBarWidget(member),
            backgroundColor = Theme.COLOR_WHITE,
            child = SingleChildScrollView(
                child = Column(
                    mainAxisAlignment = MainAxisAlignment.start,
                    crossAxisAlignment = CrossAxisAlignment.start,
                    children = listOfNotNull(
                        toTitleWidget(product),

                        toPictureCarouselWidget(product),

                        Divider(color = Theme.COLOR_DIVIDER),
                        toPriceWidget(offer, country),

                        Divider(color = Theme.COLOR_DIVIDER),
                        BusinessToolbarWidget.of(product, merchant, webappUrl, urlBuilder),

                        if (product.type == ProductType.EVENT.name) Divider(color = Theme.COLOR_DIVIDER) else null,
                        toEventWidget(product, country, merchant),

                        deliveryWidget?.let { Divider(color = Theme.COLOR_DIVIDER) },
                        deliveryWidget,

                        descriptionWidget,
                    ),
                ),
            ),
        ).toWidget()
    }

    private fun toTitleWidget(product: Product): WidgetAware =
        Container(
            padding = 10.0,
            background = Theme.COLOR_WHITE,
            child = Text(
                caption = StringUtil.capitalizeFirstLetter(product.title),
                size = Theme.TEXT_SIZE_LARGE,
                bold = true,
            ),
        )

    private fun toPictureCarouselWidget(product: Product): WidgetAware =
        CarouselSlider(
            viewportFraction = .9,
            enableInfiniteScroll = false,
            reverse = false,
            height = PICTURE_HEIGHT,
            children = product.pictures.map {
                AspectRatio(
                    aspectRatio = PICTURE_ASPECT_RATIO_WIDTH / PICTURE_ASPECT_RATIO_HEIGHT,
                    child = Image(
                        url = imageService.transform(
                            url = it.url,
                            transformation = Transformation(
                                dimension = Dimension(height = PICTURE_HEIGHT.toInt()),
                                aspectRatio = com.wutsi.platform.core.image.AspectRatio(
                                    width = PICTURE_ASPECT_RATIO_WIDTH.toInt(),
                                    height = PICTURE_ASPECT_RATIO_HEIGHT.toInt(),
                                ),
                            ),
                        ),
                        height = PICTURE_HEIGHT,
                    ),
                )
            },
        )

    private fun toAvailabilityWidget(product: Product): WidgetAware? {
        if (product.quantity == null || product.quantity!! > regulationEngine.lowStockThreshold()) {
            return null
        }
        return Text(
            caption = if (product.quantity == 0) {
                getText("page.product.out-of-stock")
            } else if (product.quantity == 1) {
                getText("page.product.low-stock-1")
            } else {
                getText("page.product.low-stock-n", arrayOf(product.quantity))
            },
            color = if (product.quantity == 0) {
                Theme.COLOR_DANGER
            } else {
                Theme.COLOR_WARNING
            },
        )
    }

    private fun toPriceWidget(offer: Offer, country: Country): WidgetAware {
        val price = offer.price
        val widget = MoneyText(
            currency = country.currencySymbol,
            color = Theme.COLOR_PRIMARY,
            valueFontSize = Theme.TEXT_SIZE_X_LARGE,
            value = price.price.toDouble(),
            numberFormat = country.monetaryFormat,
            bold = true,
            locale = country.locale,
        )
        return Container(
            padding = 10.0,
            child = Column(
                mainAxisAlignment = MainAxisAlignment.start,
                crossAxisAlignment = CrossAxisAlignment.start,
                children = listOfNotNull(
                    if (price.referencePrice == null) {
                        widget
                    } else {
                        Wrap(
                            direction = Axis.Horizontal,
                            spacing = 10.0,
                            runSpacing = 10.0,
                            children = listOf(
                                widget,
                                Text(
                                    caption = country.createMoneyFormat().format(price.referencePrice),
                                    decoration = TextDecoration.Strikethrough,
                                ),
                            ),
                        )
                    },
                    if (price.savingsPercentage > 0) {
                        Chip(
                            backgroundColor = Theme.COLOR_SUCCESS,
                            color = Theme.COLOR_WHITE,
                            caption = "${price.savingsPercentage}%",
                        )
                    } else {
                        null
                    },
                    toAvailabilityWidget(offer.product),
                ),
            ),
        )
    }

    private fun toEventWidget(product: Product, country: Country, merchant: Member): WidgetAware? {
        if (product.type != ProductType.EVENT.name) {
            return null
        }

        val event = product.event!!
        val locale = LocaleContextHolder.getLocale()
        val dateFormat = DateTimeFormatter.ofPattern(country.dateFormat, locale)
        val timeFormat = DateTimeFormatter.ofPattern(country.timeFormat, locale)
        val starts = event.starts?.let { DateTimeUtil.convert(it, merchant.timezoneId) }
        val ends = event.ends?.let { DateTimeUtil.convert(it, merchant.timezoneId) }

        return Container(
            padding = 10.0,
            child = Column(
                mainAxisAlignment = MainAxisAlignment.start,
                crossAxisAlignment = CrossAxisAlignment.start,
                children = listOfNotNull(
                    Text(
                        caption = if (event.online) {
                            getText("page.product.event-online.title")
                        } else {
                            getText("page.product.event.title")
                        },
                        size = Theme.TEXT_SIZE_LARGE,
                        bold = true,
                    ),

                    starts?.let { Container(padding = 5.0) },
                    starts?.let {
                        Row(
                            mainAxisAlignment = MainAxisAlignment.start,
                            crossAxisAlignment = CrossAxisAlignment.center,
                            children = listOfNotNull(
                                Icon(
                                    code = Theme.ICON_CALENDAR,
                                    color = Theme.COLOR_PRIMARY,
                                    size = 24.0,
                                ),
                                Container(padding = 5.0),
                                Text(starts.format(dateFormat)),
                            ),
                        )
                    },

                    Container(padding = 5.0),
                    Row(
                        mainAxisAlignment = MainAxisAlignment.start,
                        crossAxisAlignment = CrossAxisAlignment.center,
                        children = listOfNotNull(
                            Icon(
                                code = Theme.ICON_CLOCK,
                                color = Theme.COLOR_PRIMARY,
                                size = 24.0,
                            ),
                            Container(padding = 5.0),
                            starts?.let {
                                Text(starts.format(timeFormat))
                            },
                            Text(" - "),
                            ends?.let {
                                Text(ends.format(timeFormat))
                            },
                        ),
                    ),

                    product.event?.meetingProvider?.let { Container(padding = 5.0) },
                    product.event?.meetingProvider?.let {
                        Row(
                            mainAxisAlignment = MainAxisAlignment.start,
                            crossAxisAlignment = CrossAxisAlignment.center,
                            children = listOfNotNull(
                                Icon(
                                    code = Theme.ICON_LOCATION,
                                    color = Theme.COLOR_PRIMARY,
                                    size = 24.0,
                                ),
                                Container(padding = 5.0),
                                Image(
                                    url = it.logoUrl,
                                    width = 24.0,
                                    height = 24.0,
                                ),
                                Container(padding = 2.0),
                                Text(it.name),
                            ),
                        )
                    },
                ),
            ),
        )
    }

    private fun toDescriptionWidget(product: Product): WidgetAware? =
        if (!product.description.isNullOrEmpty()) {
            Container(
                padding = 10.0,
                border = 1.0,
                borderColor = Theme.COLOR_GRAY_LIGHT,
                background = Theme.COLOR_WHITE,
                child = ExpandablePanel(
                    header = getText("page.product.product-details"),
                    expanded = Container(
                        padding = 10.0,
                        child = Text(product.description!!),
                    ),
                ),
            )
        } else {
            null
        }

    private fun toDeliveryWidget(product: Product): WidgetAware? {
        if (product.type != ProductType.EVENT.name) {
            return null
        }

        return Container(
            padding = 10.0,
            child = Column(
                mainAxisAlignment = MainAxisAlignment.start,
                crossAxisAlignment = CrossAxisAlignment.start,
                children = listOfNotNull(
                    Text(
                        caption = getText("page.product.delivery"),
                        size = Theme.TEXT_SIZE_LARGE,
                        bold = true,
                    ),
                    Container(padding = 5.0),
                    Text(
                        caption = if (product.event?.online == true) {
                            getText("page.product.delivery-event-online")
                        } else {
                            getText("page.product.delivery-event-offline")
                        },
                    ),
                ),
            ),
        )
    }
}
