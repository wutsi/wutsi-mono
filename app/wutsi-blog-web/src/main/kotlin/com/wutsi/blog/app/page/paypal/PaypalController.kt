package com.wutsi.blog.app.page.paypal

import com.wutsi.blog.app.form.BuyForm
import com.wutsi.blog.app.service.TransactionService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseBody
import java.net.http.HttpClient

@Controller
class PaypalController(
    private val transactionService: TransactionService,
) {
    val client: HttpClient = HttpClient.newHttpClient()

    @ResponseBody
    @PostMapping("/paypal/orders", produces = ["application/json"], consumes = ["application/json"])
    fun createOrder(@RequestBody form: BuyForm): Map<String, String?> {
        val id = transactionService.buy(form)
        val tx = transactionService.get(id, false)
        return mapOf(
            "transactionId" to tx.id,
            "orderId" to tx.gatewayTransactionId
        )
    }

    @ResponseBody
    @PostMapping("/paypal/orders/{id}/capture", produces = ["application/json"], consumes = ["application/json"])
    fun captureOrder(@PathVariable id: String): Map<String, String> {
        transactionService.capture(id)
        return mapOf(
            "transactionId" to id
        )
    }
}