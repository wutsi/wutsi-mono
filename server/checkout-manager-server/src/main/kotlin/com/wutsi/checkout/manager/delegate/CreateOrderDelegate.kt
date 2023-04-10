package com.wutsi.checkout.manager.delegate

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.checkout.access.CheckoutAccessApi
import com.wutsi.checkout.access.dto.Business
import com.wutsi.checkout.access.dto.CreateOrderDiscountRequest
import com.wutsi.checkout.access.dto.CreateOrderItemRequest
import com.wutsi.checkout.manager.dto.CreateOrderRequest
import com.wutsi.checkout.manager.dto.CreateOrderResponse
import com.wutsi.enums.OrderType
import com.wutsi.enums.ProductStatus
import com.wutsi.enums.ProductType
import com.wutsi.error.ErrorURN
import com.wutsi.marketplace.access.MarketplaceAccessApi
import com.wutsi.marketplace.access.dto.CreateReservationRequest
import com.wutsi.marketplace.access.dto.Fundraising
import com.wutsi.marketplace.access.dto.OfferPrice
import com.wutsi.marketplace.access.dto.OfferSummary
import com.wutsi.marketplace.access.dto.ProductSummary
import com.wutsi.marketplace.access.dto.ReservationItem
import com.wutsi.marketplace.access.dto.SearchDiscountRequest
import com.wutsi.marketplace.access.dto.SearchOfferRequest
import com.wutsi.marketplace.access.dto.Store
import com.wutsi.membership.access.MembershipAccessApi
import com.wutsi.membership.access.dto.Account
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.ErrorResponse
import com.wutsi.platform.core.error.exception.ConflictException
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.regulation.RuleSet
import com.wutsi.regulation.rule.AccountShouldBeActiveRule
import com.wutsi.regulation.rule.AccountShouldHaveFundraisingRule
import com.wutsi.regulation.rule.AccountShouldHaveStoreRule
import com.wutsi.regulation.rule.BusinessShouldBeActive
import com.wutsi.regulation.rule.FundraisingShouldBeActiveRule
import com.wutsi.regulation.rule.StoreShouldBeActiveRule
import feign.FeignException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.OffsetDateTime

@Service
public class CreateOrderDelegate(
    private val marketplaceAccessApi: MarketplaceAccessApi,
    private val checkoutAccessApi: CheckoutAccessApi,
    private val membershipAccessApi: MembershipAccessApi,
    private val logger: KVLogger,
    private val objectMapper: ObjectMapper,
    private val clock: Clock,

    @Value("\${wutsi.application.order.ttl-minutes}") private val ttlMinutes: Long,
) {
    fun invoke(request: CreateOrderRequest): CreateOrderResponse {
        logger.add("request_customer_email", request.customerEmail)
        logger.add("request_customer_name", request.customerName)
        logger.add("request_business_id", request.businessId)
        logger.add("request_channel_type", request.channelType)
        logger.add("request_device_type", request.deviceType)

        val business = checkoutAccessApi.getBusiness(request.businessId).business
        val account = membershipAccessApi.getAccount(business.accountId).account
        val store = if (request.type == OrderType.SALES.name && account.storeId != null) {
            marketplaceAccessApi.getStore(account.storeId!!).store
        } else {
            null
        }
        val fundraising = if (request.type == OrderType.DONATION.name && account.fundraisingId != null) {
            marketplaceAccessApi.getFundraising(account.fundraisingId!!).fundraising
        } else {
            null
        }
        validate(account, business, store, fundraising, request)

        // Order
        val response = createOrder(request, business)
        logger.add("order_id", response.orderId)
        logger.add("order_status", response.orderStatus)

        // Reserve products
        if (request.type != OrderType.DONATION.name) {
            val reservationId = reserveProducts(request, response.orderId)
            logger.add("reservation_id", reservationId)
        }

        return CreateOrderResponse(
            orderId = response.orderId,
            orderStatus = response.orderStatus,
        )
    }

    private fun validate(
        account: Account,
        business: Business,
        store: Store?,
        fundraising: Fundraising?,
        request: CreateOrderRequest,
    ) =
        RuleSet(
            listOfNotNull(
                AccountShouldBeActiveRule(account),
                BusinessShouldBeActive(business),

                if (request.type == OrderType.SALES.name) AccountShouldHaveStoreRule(account) else null,
                store?.let { StoreShouldBeActiveRule(store) },

                if (request.type == OrderType.DONATION.name) AccountShouldHaveFundraisingRule(account) else null,
                fundraising?.let { FundraisingShouldBeActiveRule(fundraising) },
            ),
        ).check()

    private fun createOrder(
        request: CreateOrderRequest,
        business: Business,
    ): com.wutsi.checkout.access.dto.CreateOrderResponse {
        // Offers
        val offers = searchOffers(request, business).associateBy { it.product.id }

        // Discounts
        val discountIds = offers.mapNotNull { it.value.price.discountId }.toSet()
        val discounts = if (discountIds.isEmpty()) {
            emptyMap()
        } else {
            marketplaceAccessApi.searchDiscount(
                request = SearchDiscountRequest(
                    discountIds = discountIds.toList(),
                    limit = discountIds.size,
                ),
            ).discounts.associateBy { it.id }
        }

        return checkoutAccessApi.createOrder(
            request = com.wutsi.checkout.access.dto.CreateOrderRequest(
                type = request.type,
                businessId = business.id,
                notes = request.notes,
                currency = business.currency,
                customerEmail = request.customerEmail,
                customerName = request.customerName,
                deviceType = request.deviceType,
                channelType = request.channelType,
                items = request.items.map {
                    val offer = offers[it.productId]
                    if (offer == null) {
                        throw ConflictException(
                            error = Error(
                                code = ErrorURN.PRODUCT_NOT_AVAILABLE.urn,
                                data = mapOf(
                                    "product-id" to it.productId,
                                ),
                            ),
                        )
                    } else {
                        val discount = offer.price.discountId?.let { discounts[it] }
                        val product = offer.product
                        CreateOrderItemRequest(
                            productId = it.productId,
                            productType = product.type,
                            quantity = it.quantity,
                            title = product.title,
                            pictureUrl = product.thumbnailUrl,
                            unitPrice = product.price ?: 0,
                            discounts = if (discount != null) {
                                listOf(
                                    CreateOrderDiscountRequest(
                                        discountId = discount.id,
                                        name = discount.name,
                                        type = discount.type,
                                        amount = offer.price.savings,
                                    ),
                                )
                            } else {
                                emptyList()
                            },
                        )
                    }
                },
                expires = OffsetDateTime.now(clock).plusMinutes(ttlMinutes),
            ),
        )
    }

    private fun searchOffers(
        request: CreateOrderRequest,
        business: Business,
    ): List<OfferSummary> {
        if (request.type == OrderType.DONATION.name) {
            val member = membershipAccessApi.getAccount(business.accountId).account
            val fundraising = marketplaceAccessApi.getFundraising(member.fundraisingId!!).fundraising
            val item = request.items[0]
            return listOf(
                OfferSummary(
                    product = ProductSummary(
                        id = -1,
                        storeId = -1,
                        title = "Donation",
                        quantity = item.quantity,
                        status = ProductStatus.UNKNOWN.name,
                        type = ProductType.DONATION.name,
                        price = fundraising.amount,
                    ),
                    price = OfferPrice(
                        productId = item.productId,
                        price = fundraising.amount,
                    ),
                ),
            )
        } else {
            return marketplaceAccessApi.searchOffer(
                request = SearchOfferRequest(
                    limit = request.items.size,
                    productIds = request.items.map { it.productId },
                ),
            ).offers
        }
    }

    private fun reserveProducts(request: CreateOrderRequest, orderId: String): Long {
        try {
            return marketplaceAccessApi.createReservation(
                request = CreateReservationRequest(
                    orderId = orderId,
                    items = request.items.map {
                        ReservationItem(
                            productId = it.productId,
                            quantity = it.quantity,
                        )
                    },
                ),
            ).reservationId
        } catch (ex: FeignException) {
            throw handleAvailabilityException(ex)
        }
    }

    private fun handleAvailabilityException(ex: FeignException): Throwable {
        val response = objectMapper.readValue(ex.contentUTF8(), ErrorResponse::class.java)
        return if (response.error.code == com.wutsi.marketplace.access.error.ErrorURN.PRODUCT_NOT_AVAILABLE.urn) {
            ConflictException(
                error = response.error.copy(code = ErrorURN.PRODUCT_NOT_AVAILABLE.urn),
            )
        } else {
            ex
        }
    }
}
