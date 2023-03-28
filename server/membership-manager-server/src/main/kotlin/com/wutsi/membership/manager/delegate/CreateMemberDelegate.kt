package com.wutsi.membership.manager.delegate

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.checkout.access.CheckoutAccessApi
import com.wutsi.checkout.access.dto.CreatePaymentMethodRequest
import com.wutsi.checkout.access.dto.SearchPaymentProviderRequest
import com.wutsi.enums.PaymentMethodType
import com.wutsi.membership.access.MembershipAccessApi
import com.wutsi.membership.access.dto.CreateAccountRequest
import com.wutsi.membership.access.error.ErrorURN
import com.wutsi.membership.manager.dto.CreateMemberRequest
import com.wutsi.membership.manager.util.PhoneUtil
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.ErrorResponse
import com.wutsi.platform.core.error.exception.ConflictException
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.stream.Event
import com.wutsi.platform.core.stream.EventStream
import com.wutsi.security.manager.SecurityManagerApi
import com.wutsi.security.manager.dto.CreatePasswordRequest
import feign.FeignException
import org.springframework.context.event.EventListener
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Service

@Service
class CreateMemberDelegate(
    private val membershipAccessApi: MembershipAccessApi,
    private val securityManagerApi: SecurityManagerApi,
    private val checkoutAccessApi: CheckoutAccessApi,
    private val eventStream: EventStream,
    private val objectMapper: ObjectMapper,
    private val logger: KVLogger,
) {
    companion object {
        const val CREATE_PASSWORD = "urn:wutsi:event:membership-manager:create-password"
        const val CREATE_PAYMENT_METHOD = "urn:wutsi:event:membership-manager:create-payment-method"
    }

    fun invoke(request: CreateMemberRequest) {
        logger.add("request_phone_number", request.phoneNumber)
        logger.add("request_country", request.country)
        logger.add("request_city_id", request.cityId)
        logger.add("request_display_name", request.displayName)

        val accountId = createAccount(request)
        createPassword(accountId, request)
        createPaymentMethod(accountId, request)
    }

    @EventListener
    fun onEvent(event: Event) {
        when (event.type) {
            CREATE_PASSWORD -> {
                val payload = toCreateMemberEventPayload(event)
                onCreatePassword(payload.accountId, payload.request)
            }
            CREATE_PAYMENT_METHOD -> {
                val payload = toCreateMemberEventPayload(event)
                onCreatePaymentMethod(payload.accountId, payload.request)
            }
            else -> {}
        }
    }

    private fun toCreateMemberEventPayload(event: Event) =
        objectMapper.readValue(event.payload, CreateMemberEventPayload::class.java)

    private fun createAccount(request: CreateMemberRequest): Long =
        try {
            membershipAccessApi.createAccount(
                request = CreateAccountRequest(
                    phoneNumber = request.phoneNumber,
                    displayName = request.displayName,
                    country = PhoneUtil.detectCountry(request.phoneNumber),
                    language = LocaleContextHolder.getLocale().language,
                    cityId = request.cityId,
                ),
            ).accountId
        } catch (ex: FeignException) {
            val errorResponse = toErrorResponse(ex)
            if (errorResponse.error.code == ErrorURN.PHONE_NUMBER_ALREADY_ASSIGNED.urn) {
                throw ConflictException(
                    error = Error(
                        code = com.wutsi.error.ErrorURN.PHONE_NUMBER_ALREADY_ASSIGNED.urn,
                        data = mapOf(
                            "phone-number" to request.phoneNumber,
                        ),
                    ),
                )
            } else {
                throw ex
            }
        }

    private fun createPassword(accountId: Long, request: CreateMemberRequest) {
        eventStream.enqueue(CREATE_PASSWORD, CreateMemberEventPayload(accountId, request))
    }

    private fun onCreatePassword(accountId: Long, request: CreateMemberRequest) {
        securityManagerApi.createPassword(
            CreatePasswordRequest(
                accountId = accountId,
                username = request.phoneNumber,
                value = request.pin,
            ),
        )
    }

    private fun createPaymentMethod(accountId: Long, request: CreateMemberRequest) {
        eventStream.enqueue(CREATE_PAYMENT_METHOD, CreateMemberEventPayload(accountId, request))
    }

    private fun onCreatePaymentMethod(accountId: Long, request: CreateMemberRequest) {
        try {
            val providers = checkoutAccessApi.searchPaymentProvider(
                SearchPaymentProviderRequest(
                    country = request.country,
                    number = request.phoneNumber,
                    type = PaymentMethodType.MOBILE_MONEY.name,
                ),
            ).paymentProviders

            if (providers.size == 1) {
                checkoutAccessApi.createPaymentMethod(
                    request = CreatePaymentMethodRequest(
                        accountId = accountId,
                        type = providers[0].type,
                        number = request.phoneNumber,
                        country = request.country,
                        ownerName = request.displayName,
                        providerId = providers[0].id,
                    ),
                )
            }
        } catch (ex: FeignException) {
            // Ignore the error
        }
    }

    private fun toErrorResponse(ex: FeignException): ErrorResponse =
        objectMapper.readValue(ex.contentUTF8(), ErrorResponse::class.java)
}

data class CreateMemberEventPayload(
    val accountId: Long = -1,
    val request: CreateMemberRequest = CreateMemberRequest(),
)
