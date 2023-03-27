package com.wutsi.application.marketplace.service

import com.wutsi.enums.ProductType
import com.wutsi.flutter.sdui.DropdownButton
import com.wutsi.flutter.sdui.DropdownMenuItem
import com.wutsi.flutter.sdui.Input
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.InputType
import com.wutsi.marketplace.manager.dto.Product
import com.wutsi.regulation.RegulationEngine
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Service
import java.util.Locale

@Service
class ProductEditorWidgetProvider(
    private val regulationEngine: RegulationEngine,
    private val messages: MessageSource,
) {
    fun get(name: String, product: Product, countryCode: String): WidgetAware =
        when (name) {
            "title" -> get(name, product.title)
            "summary" -> get(name, product.summary)
            "description" -> get(name, product.description)
            "quantity" -> get(name, product.quantity)
            "price" -> get(name, product.price, countryCode)
            "type" -> get(name, product.type)
            else -> throw IllegalStateException("Not supported: $name")
        }

    fun get(name: String, value: Any?, countryCode: String? = null): WidgetAware {
        val country = countryCode?.let { regulationEngine.country(countryCode) }

        return when (name) {
            "title" -> getInputWidget(value, 100, required = true)
            "summary" -> getInputWidget(value?.toString(), 160, maxLines = 2)
            "description" -> getInputWidget(value, 1000, maxLines = 5)
            "quantity" -> getInputWidget(value, type = InputType.Number, decimal = false)
            "price" -> getInputWidget(
                value,
                maxlength = 10,
                decimal = country?.let {
                    country.monetaryFormat.indexOf(".") >= -1
                } ?: true,
                suffix = country?.currencySymbol,
            )
            "type" -> DropdownButton(
                name = "value",
                value = value?.toString(),
                required = true,
                children = listOf(
                    DropdownMenuItem(
                        caption = "",
                        value = "",
                    ),
                    DropdownMenuItem(
                        caption = getText("product.type.PHYSICAL_PRODUCT"),
                        value = ProductType.PHYSICAL_PRODUCT.name,
                    ),
                    DropdownMenuItem(
                        caption = getText("product.type.EVENT"),
                        value = ProductType.EVENT.name,
                    ),
                    DropdownMenuItem(
                        caption = getText("product.type.DIGITAL_DOWNLOAD"),
                        value = ProductType.DIGITAL_DOWNLOAD.name,
                    ),
                ),
            )
            else -> throw IllegalStateException("Not supported: $name")
        }
    }

    private fun getInputWidget(
        value: Any?,
        maxlength: Int? = null,
        type: InputType = InputType.Text,
        maxLines: Int? = null,
        required: Boolean = false,
        decimal: Boolean? = null,
        suffix: String? = null,
    ) =
        Input(
            name = "value",
            value = value?.toString(),
            type = type,
            maxLength = maxlength,
            maxLines = maxLines,
            required = required,
            inputFormatterRegex = if (decimal == false) "[0-9]" else null,
            suffix = suffix,
        )

    private fun getLocale(): Locale = LocaleContextHolder.getLocale()

    protected fun getText(key: String, args: Array<Any?> = emptyArray()): String =
        messages.getMessage(key, args, getLocale())
}
