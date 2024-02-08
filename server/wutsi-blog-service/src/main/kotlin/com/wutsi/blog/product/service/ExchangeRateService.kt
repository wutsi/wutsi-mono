package com.wutsi.blog.product.service

import org.springframework.stereotype.Service
import java.math.RoundingMode

@Service
class ExchangeRateService {
    companion object {
        private val EXCHANGE_RATE: Map<String, Double> = mapOf(
            "XAF-CAD" to 1.0 / 452.0,
            "XAF-EUR" to 1.0 / 656.0,
            "XAF-USD" to 1.0 / 610.0,
            "XAF-XOF" to 1.0,

            "XOF-CAD" to 1.0 / 452.0,
            "XOF-EUR" to 1.0 / 656.0,
            "XOF-USD" to 1.0 / 610.0,
            "XOF-XAF" to 1.0,
        )
    }

    fun getExchangeRate(sourceCurrency: String, targetCurrency: String): Double =
        if (sourceCurrency == targetCurrency) {
            1.0
        } else {
            EXCHANGE_RATE["$sourceCurrency-$targetCurrency"]
                ?: throw IllegalStateException("$sourceCurrency-$targetCurrency: Not supported")
        }

    fun convert(amount: Long, exchangeRate: Double): Double =
        (amount.toDouble() * exchangeRate).toBigDecimal()
            .setScale(0, RoundingMode.CEILING)
            .toDouble()
}
