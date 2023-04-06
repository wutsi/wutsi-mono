package com.wutsi.checkout.access

import com.wutsi.checkout.access.dto.CreateBusinessRequest
import com.wutsi.checkout.access.dto.CreateBusinessResponse
import com.wutsi.checkout.access.dto.CreateCashoutRequest
import com.wutsi.checkout.access.dto.CreateCashoutResponse
import com.wutsi.checkout.access.dto.CreateChargeRequest
import com.wutsi.checkout.access.dto.CreateChargeResponse
import com.wutsi.checkout.access.dto.CreateOrderRequest
import com.wutsi.checkout.access.dto.CreateOrderResponse
import com.wutsi.checkout.access.dto.CreatePaymentMethodRequest
import com.wutsi.checkout.access.dto.CreatePaymentMethodResponse
import com.wutsi.checkout.access.dto.GetBusinessResponse
import com.wutsi.checkout.access.dto.GetOrderResponse
import com.wutsi.checkout.access.dto.GetPaymentMethodResponse
import com.wutsi.checkout.access.dto.GetTransactionResponse
import com.wutsi.checkout.access.dto.SearchOrderRequest
import com.wutsi.checkout.access.dto.SearchOrderResponse
import com.wutsi.checkout.access.dto.SearchPaymentMethodRequest
import com.wutsi.checkout.access.dto.SearchPaymentMethodResponse
import com.wutsi.checkout.access.dto.SearchPaymentProviderRequest
import com.wutsi.checkout.access.dto.SearchPaymentProviderResponse
import com.wutsi.checkout.access.dto.SearchSalesKpiRequest
import com.wutsi.checkout.access.dto.SearchSalesKpiResponse
import com.wutsi.checkout.access.dto.SearchTransactionRequest
import com.wutsi.checkout.access.dto.SearchTransactionResponse
import com.wutsi.checkout.access.dto.SyncTransactionStatusResponse
import com.wutsi.checkout.access.dto.UpdateBusinessStatusRequest
import com.wutsi.checkout.access.dto.UpdateOrderStatusRequest
import com.wutsi.checkout.access.dto.UpdatePaymentMethodStatusRequest
import feign.Headers
import feign.Param
import feign.RequestLine
import kotlin.Long
import kotlin.String
import kotlin.Unit

public interface CheckoutAccessApi {
    @RequestLine("POST /v1/payment-providers/search")
    @Headers(value = ["Content-Type: application/json"])
    public fun searchPaymentProvider(request: SearchPaymentProviderRequest):
        SearchPaymentProviderResponse

    @RequestLine("POST /v1/payment-methods")
    @Headers(value = ["Content-Type: application/json"])
    public fun createPaymentMethod(request: CreatePaymentMethodRequest): CreatePaymentMethodResponse

    @RequestLine("POST /v1/payment-methods/search")
    @Headers(value = ["Content-Type: application/json"])
    public fun searchPaymentMethod(request: SearchPaymentMethodRequest): SearchPaymentMethodResponse

    @RequestLine("GET /v1/payment-methods/{token}")
    @Headers(value = ["Content-Type: application/json"])
    public fun getPaymentMethod(@Param("token") token: String): GetPaymentMethodResponse

    @RequestLine("POST /v1/payment-methods/{token}/status")
    @Headers(value = ["Content-Type: application/json"])
    public fun updatePaymentMethodStatus(
        @Param("token") token: String,
        request: UpdatePaymentMethodStatusRequest,
    ): Unit

    @RequestLine("POST /v1/businesses")
    @Headers(value = ["Content-Type: application/json"])
    public fun createBusiness(request: CreateBusinessRequest): CreateBusinessResponse

    @RequestLine("GET /v1/businesses/{id}")
    @Headers(value = ["Content-Type: application/json"])
    public fun getBusiness(@Param("id") id: Long): GetBusinessResponse

    @RequestLine("POST /v1/businesses/{id}/status")
    @Headers(value = ["Content-Type: application/json"])
    public fun updateBusinessStatus(@Param("id") id: Long, request: UpdateBusinessStatusRequest): Unit

    @RequestLine("POST /v1/orders")
    @Headers(value = ["Content-Type: application/json"])
    public fun createOrder(request: CreateOrderRequest): CreateOrderResponse

    @RequestLine("POST /v1/orders/search")
    @Headers(value = ["Content-Type: application/json"])
    public fun searchOrder(request: SearchOrderRequest): SearchOrderResponse

    @RequestLine("GET /v1/orders/{id}")
    @Headers(value = ["Content-Type: application/json"])
    public fun getOrder(@Param("id") id: String): GetOrderResponse

    @RequestLine("POST /v1/orders/{id}/status")
    @Headers(value = ["Content-Type: application/json"])
    public fun updateOrderStatus(@Param("id") id: String, request: UpdateOrderStatusRequest): Unit

    @RequestLine("POST /v1/transactions/charge")
    @Headers(value = ["Content-Type: application/json"])
    public fun createCharge(request: CreateChargeRequest): CreateChargeResponse

    @RequestLine("POST /v1/transactions/cashout")
    @Headers(value = ["Content-Type: application/json"])
    public fun createCashout(request: CreateCashoutRequest): CreateCashoutResponse

    @RequestLine("POST /v1/transactions/search")
    @Headers(value = ["Content-Type: application/json"])
    public fun searchTransaction(request: SearchTransactionRequest): SearchTransactionResponse

    @RequestLine("GET /v1/transactions/{id}")
    @Headers(value = ["Content-Type: application/json"])
    public fun getTransaction(@Param("id") id: String): GetTransactionResponse

    @RequestLine("GET /v1/transactions/{id}/status/sync")
    @Headers(value = ["Content-Type: application/json"])
    public fun syncTransactionStatus(@Param("id") id: String): SyncTransactionStatusResponse

    @RequestLine("POST /v1/kpis/sales/search")
    @Headers(value = ["Content-Type: application/json"])
    public fun searchSalesKpi(request: SearchSalesKpiRequest): SearchSalesKpiResponse
}
