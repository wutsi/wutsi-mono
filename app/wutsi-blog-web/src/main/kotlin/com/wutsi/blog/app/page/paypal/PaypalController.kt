package com.wutsi.blog.app.page.paypal

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.blog.app.form.BuyForm
import com.wutsi.blog.app.service.ProductService
import com.wutsi.platform.core.logging.KVLogger
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseBody
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpRequest.BodyPublishers
import java.net.http.HttpResponse
import java.util.Base64

@Controller
class PaypalController(
    private val objectMapper: ObjectMapper,
    private val productService: ProductService,
    private val logger: KVLogger,

    @Value("\${wutsi.application.paypal.client-id}") private val clientId: String,
    @Value("\${wutsi.application.paypal.secret-key}") private val secretKey: String,
    @Value("\${wutsi.application.paypal.url}") private val url: String,
) {
    val client: HttpClient = HttpClient.newHttpClient()

    @ResponseBody
    @PostMapping("/paypal/orders", produces = ["application/json"], consumes = ["application/json"])
    fun createOrder(@RequestBody form: BuyForm): PPCreateOrderResponse {
        val product = productService.get(form.productId)
        val amount = PPMoney(5.0, "USD")

        val body = PPCreateOrderRequest(
            intent = "CAPTURE",
            purchase_units = listOf(
                PPPurchaseUnit(
                    reference_id = product.id.toString(),
                    amount = amount,
                )
            )
        )
        val request: HttpRequest = HttpRequest.newBuilder()
            .uri(URI("$url/v2/checkout/orders"))
            .POST(
                BodyPublishers.ofString(
                    objectMapper.writeValueAsString(body)
                )
            )
            .headers("Authorization", "Bearer " + getAccessToken())
            .headers("Content-Type", "application/json")
            .build()
        val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())
        logger.add("paypal_status_code", response.statusCode())

        val payload = objectMapper.readValue(response.body(), PPCreateOrderResponse::class.java)
        logger.add("paypal_order_id", payload.id)
        logger.add("paypal_order_status", payload.status)

        return payload
    }

    @ResponseBody
    @PostMapping("/paypal/orders/{id}/capture", produces = ["application/json"], consumes = ["application/json"])
    fun captureOrder(@PathVariable id: String): PPCaptureOrderResponse {
        logger.add("paypal_order_id", id)

        val request: HttpRequest = HttpRequest.newBuilder()
            .uri(URI("$url/v2/checkout/orders/$id/capture"))
            .POST(BodyPublishers.noBody())
            .headers("Authorization", "Bearer " + getAccessToken())
            .headers("Content-Type", "application/json")
            .build()
        val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())

        val payload = objectMapper.readValue(response.body(), PPCaptureOrderResponse::class.java)
        logger.add("paypal_order_status", payload.status)

        return payload
    }

    private fun getAccessToken(): String {
        val request: HttpRequest = HttpRequest.newBuilder()
            .uri(URI("$url/v1/oauth2/token"))
            .POST(BodyPublishers.ofString("grant_type=client_credentials"))
            .headers(
                "Authorization",
                "Basic " + Base64.getEncoder().encodeToString("$clientId:$secretKey".toByteArray())
            )
            .headers("Content-Type", "application/x-www-form-urlencoded")
            .build()
        val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())
        return objectMapper.readValue(response.body(), PPAuthResponse::class.java).access_token
    }
}