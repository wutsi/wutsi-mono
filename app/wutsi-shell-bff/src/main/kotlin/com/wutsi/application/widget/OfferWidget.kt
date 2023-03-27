package com.wutsi.application.widget

import com.wutsi.application.Theme
import com.wutsi.application.util.DateTimeUtil
import com.wutsi.application.util.StringUtil
import com.wutsi.application.widget.WidgetL10n.getText
import com.wutsi.enums.ProductType
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.AspectRatio
import com.wutsi.flutter.sdui.Chip
import com.wutsi.flutter.sdui.ClipRRect
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Icon
import com.wutsi.flutter.sdui.Image
import com.wutsi.flutter.sdui.MoneyText
import com.wutsi.flutter.sdui.Positioned
import com.wutsi.flutter.sdui.Row
import com.wutsi.flutter.sdui.Stack
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.Wrap
import com.wutsi.flutter.sdui.enums.Alignment
import com.wutsi.flutter.sdui.enums.Axis
import com.wutsi.flutter.sdui.enums.BoxFit
import com.wutsi.flutter.sdui.enums.CrossAxisAlignment
import com.wutsi.flutter.sdui.enums.MainAxisAlignment
import com.wutsi.flutter.sdui.enums.TextDecoration
import com.wutsi.flutter.sdui.enums.TextOverflow
import com.wutsi.marketplace.manager.dto.OfferSummary
import com.wutsi.platform.core.image.Dimension
import com.wutsi.platform.core.image.Focus
import com.wutsi.platform.core.image.ImageService
import com.wutsi.platform.core.image.Transformation
import com.wutsi.regulation.Country
import com.wutsi.regulation.RegulationEngine
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class OfferWidget(
    private val title: String,
    private val price: Long?,
    private val country: Country,
    private val thumbnailUrl: String?,
    private val quantity: Int?,
    private val type: String,
    private val lowStockThreshold: Int,
    private val action: Action,
    private val margin: Double = 10.0,
    private val imageService: ImageService,
    private val eventStartDate: String? = null,
    private val eventMeetingProviderLogoUrl: String? = null,
    private val eventMeetingProviderName: String? = null,
    private val referencePrice: Long? = null,
    private val savingsPercentage: Int? = null,
    private val outOfStock: Boolean = false,
) : CompositeWidgetAware() {
    companion object {
        private const val PICTURE_HEIGHT = 150.0
        private const val PICTURE_ASPECT_RATIO_WIDTH = 4.0
        private const val PICTURE_ASPECT_RATIO_HEIGHT = 4.0

        fun of(
            offer: OfferSummary,
            country: Country,
            action: Action,
            imageService: ImageService,
            timezoneId: String?,
            regulationEngine: RegulationEngine,
        ): OfferWidget {
            return OfferWidget(
                title = offer.product.title,
                price = offer.price.price,
                country = country,
                thumbnailUrl = offer.product.thumbnailUrl,
                action = action,
                imageService = imageService,
                eventStartDate = if ((offer.product.type == ProductType.EVENT.name) && (offer.product.event != null)) {
                    offer.product.event!!.starts?.let {
                        convert(it, timezoneId).format(DateTimeFormatter.ofPattern(country.dateTimeFormat))
                    }
                } else {
                    null
                },
                eventMeetingProviderLogoUrl = if (offer.product.type == ProductType.EVENT.name) {
                    offer.product.event?.meetingProvider?.logoUrl
                } else {
                    null
                },
                eventMeetingProviderName = if (offer.product.type == ProductType.EVENT.name) {
                    offer.product.event?.meetingProvider?.name
                } else {
                    null
                },
                quantity = offer.product.quantity,
                referencePrice = offer.price.referencePrice,
                savingsPercentage = offer.price.referencePrice?.let { offer.price.savingsPercentage },
                outOfStock = offer.product.outOfStock,
                type = offer.product.type,
                lowStockThreshold = regulationEngine.lowStockThreshold(),
            )
        }

        private fun convert(date: OffsetDateTime, timezoneId: String?): OffsetDateTime =
            if (timezoneId == null) {
                date
            } else {
                DateTimeUtil.convert(date, timezoneId)
            }
    }

    override fun toWidgetAware(): WidgetAware =
        Container(
            margin = margin,
            action = action,
            child = Column(
                mainAxisAlignment = MainAxisAlignment.start,
                crossAxisAlignment = CrossAxisAlignment.start,
                children = listOfNotNull(
                    toThumbnailWidget(),
                    toInfoWidget(),
                ),
            ),
        )

    private fun toThumbnailWidget(): WidgetAware? {
        val widget = thumbnailUrl?.let {
            ClipRRect(
                borderRadius = 5.0,
                child = AspectRatio(
                    aspectRatio = PICTURE_ASPECT_RATIO_WIDTH / PICTURE_ASPECT_RATIO_HEIGHT,
                    child = Container(
                        alignment = Alignment.Center,
                        child = Image(
                            url = resize(it),
                            fit = BoxFit.fitHeight,
                        ),
                    ),
                ),
            )
        }
        return if (widget == null || savingsPercentage == null) {
            widget
        } else {
            Stack(
                children = listOf(
                    widget,
                    Positioned(
                        left = 0.0,
                        top = 0.0,
                        child = Chip(
                            backgroundColor = Theme.COLOR_SUCCESS,
                            color = Theme.COLOR_WHITE,
                            caption = "-$savingsPercentage%",
                        ),
                    ),
                ),
            )
        }
    }

    private fun toInfoWidget(): WidgetAware = Column(
        mainAxisAlignment = MainAxisAlignment.start,
        crossAxisAlignment = CrossAxisAlignment.start,
        children = listOfNotNull(
            Container(padding = 5.0),
            Text(
                caption = StringUtil.capitalizeFirstLetter(title),
                overflow = TextOverflow.Elipsis,
                maxLines = 2,
                bold = true,
            ),

            eventStartDate?.let {
                Container(padding = 5.0)
            },
            eventStartDate?.let {
                Row(
                    mainAxisAlignment = MainAxisAlignment.start,
                    crossAxisAlignment = CrossAxisAlignment.start,
                    children = listOf(
                        Icon(
                            size = 16.0,
                            code = Theme.ICON_CALENDAR,
                        ),
                        Container(padding = 2.0),
                        Text(it),
                    ),
                )
            },
            eventMeetingProviderName?.let {
                Container(padding = 2.0)
            },
            eventMeetingProviderName?.let {
                Row(
                    mainAxisAlignment = MainAxisAlignment.start,
                    crossAxisAlignment = CrossAxisAlignment.start,
                    children = listOfNotNull(
                        if (eventMeetingProviderLogoUrl != null) {
                            Image(
                                width = 16.0,
                                height = 16.0,
                                url = eventMeetingProviderLogoUrl,
                            )
                        } else {
                            null
                        },
                        if (eventMeetingProviderLogoUrl != null) {
                            Container(padding = 2.0)
                        } else {
                            null
                        },
                        Text(it),
                    ),
                )
            },

            if (type == ProductType.DIGITAL_DOWNLOAD.name) {
                Container(padding = 5.0)
            } else {
                null
            },
            if (type == ProductType.DIGITAL_DOWNLOAD.name) {
                Row(
                    mainAxisAlignment = MainAxisAlignment.start,
                    crossAxisAlignment = CrossAxisAlignment.start,
                    children = listOf(
                        Icon(size = 16.0, code = Theme.ICON_DOWNLOAD, color = Theme.COLOR_PRIMARY),
                        Container(padding = 2.0),
                        Text(getText("widget.product-card.download")),
                    ),
                )
            } else {
                null
            },

            Container(padding = 5.0),
            if (outOfStock) {
                Text(
                    caption = getText("widget.product-card.out-of-stock"),
                    color = Theme.COLOR_DANGER,
                    size = Theme.TEXT_SIZE_SMALL,
                )
            } else if (quantity != null && quantity < lowStockThreshold) {
                Text(
                    caption = getText("widget.product-card.low-stock", arrayOf(quantity)),
                    color = Theme.COLOR_WARNING,
                    size = Theme.TEXT_SIZE_SMALL,
                )
            } else {
                null
            },

            price?.let { toPriceWidget(it) },
        ),
    )

    private fun toPriceWidget(price: Long): WidgetAware {
        val widget = MoneyText(
            color = Theme.COLOR_PRIMARY,
            value = price.toDouble(),
            currency = country.currencySymbol,
            numberFormat = country.monetaryFormat,
            valueFontSize = Theme.TEXT_SIZE_DEFAULT,
            currencyFontSize = Theme.TEXT_SIZE_SMALL,
            locale = country.locale,
        )

        return if (referencePrice == null) {
            widget
        } else {
            Wrap(
                direction = Axis.Horizontal,
                spacing = 10.0,
                runSpacing = 10.0,
                children = listOf(
                    widget,
                    Text(
                        caption = country.createMoneyFormat().format(referencePrice),
                        decoration = TextDecoration.Strikethrough,
                        size = Theme.TEXT_SIZE_SMALL,
                    ),
                ),
            )
        }
    }

    private fun resize(url: String): String =
        imageService.transform(
            url,
            Transformation(
                focus = Focus.AUTO,
                dimension = Dimension(height = PICTURE_HEIGHT.toInt()),
                aspectRatio = com.wutsi.platform.core.image.AspectRatio(
                    width = PICTURE_ASPECT_RATIO_WIDTH.toInt(),
                    height = PICTURE_ASPECT_RATIO_HEIGHT.toInt(),
                ),
            ),
        )
}
