package com.wutsi.application.marketplace.service

import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.DropdownButton
import com.wutsi.flutter.sdui.DropdownMenuItem
import com.wutsi.flutter.sdui.Image
import com.wutsi.flutter.sdui.Input
import com.wutsi.flutter.sdui.Row
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.InputType
import com.wutsi.flutter.sdui.enums.MainAxisAlignment
import com.wutsi.marketplace.manager.dto.Fundraising
import com.wutsi.regulation.RegulationEngine
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class FundraisingEditorWidgetProvider(
    private val regulationEngine: RegulationEngine,
    @Value("\${wutsi.application.asset-url}") private val assetUrl: String,
) {
    fun get(name: String, fundraising: Fundraising, country: String): WidgetAware =
        when (name) {
            "description" -> get(name, fundraising.description, country)
            "amount" -> get(name, fundraising.amount, country)
            "video-url" -> get(name, fundraising.videoUrl, country)
            else -> throw IllegalStateException("Not supported: $name")
        }

    fun get(name: String, value: Any?, countryCode: String): WidgetAware {
        val country = regulationEngine.country(countryCode)
        val fmt = country.createMoneyFormat()
        val amounts = IntRange(1, 10).toList().map { it * country.donationBaseAmount }
        return when (name) {
            "description" -> getInputWidget(value, 2000, maxLines = 3)
            "video-url" -> Column(
                children = listOf(
                    getInputWidget(value, 1000, type = InputType.Url),
                    Row(
                        mainAxisAlignment = MainAxisAlignment.start,
                        children = listOf(
                            toIconWidget("images/social/youtube.png"),
                            toIconWidget("images/social/dailymotion.png"),
                            toIconWidget("images/social/vimeo.png"),
                        ),
                    ),
                ),
            )
            "amount" -> DropdownButton(
                name = "value",
                value = value?.let {
                    if (amounts.contains(value.toString().toLong())) value.toString() else null
                },
                required = true,
                children = amounts.map {
                    DropdownMenuItem(
                        caption = fmt.format(it),
                        value = (it).toString(),
                    )
                },
            )
            else -> throw IllegalStateException("Not supported: $name")
        }
    }

    private fun toIconWidget(url: String): WidgetAware =
        Container(
            padding = 5.0,
            child = Image(
                url = "$assetUrl/$url",
                width = 24.0,
                height = 24.0,
            ),
        )

    private fun getInputWidget(
        value: Any?,
        maxlength: Int? = null,
        type: InputType = InputType.Text,
        maxLines: Int? = null,
        required: Boolean = false,
        decimal: Boolean? = null,
        suffix: String? = null,
    ) = Input(
        name = "value",
        value = value?.toString(),
        type = type,
        maxLength = maxlength,
        maxLines = maxLines,
        required = required,
        inputFormatterRegex = if (decimal == false) "[0-9]" else null,
        suffix = suffix,
    )
}
