package com.wutsi.security.manager.service

import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.Parameter
import com.wutsi.platform.core.error.ParameterType
import com.wutsi.platform.core.error.exception.BadRequestException
import com.wutsi.platform.core.error.exception.ConflictException
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.messaging.Message
import com.wutsi.platform.core.messaging.MessagingService
import com.wutsi.platform.core.messaging.MessagingServiceProvider
import com.wutsi.platform.core.messaging.MessagingType
import com.wutsi.platform.core.messaging.Party
import com.wutsi.security.manager.dto.CreateOTPRequest
import com.wutsi.security.manager.dto.VerifyOTPRequest
import com.wutsi.security.manager.entity.OtpEntity
import com.wutsi.security.manager.error.ErrorURN
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Service
import java.util.UUID
import javax.annotation.PostConstruct

@Service
@ConfigurationProperties(prefix = "wutsi.application.otp")
public class OtpService(
    private val dao: com.wutsi.security.manager.dao.OtpRepository,
    private val messagingProvider: MessagingServiceProvider,
    private val messageSource: MessageSource,
    private val logger: KVLogger,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(OtpService::class.java)
        const val OTP_TTL_MILLIS = 300L * 1000 // 5 mins
    }

    var testAddresses: MutableList<String> = mutableListOf()

    @PostConstruct
    fun init() {
        LOGGER.info("Test Addresses: $testAddresses")
    }

    fun create(request: CreateOTPRequest): OtpEntity {
        // Create the OTP
        val otp = dao.save(
            OtpEntity(
                token = UUID.randomUUID().toString(),
                code = generateCode(6),
                expires = System.currentTimeMillis() + OTP_TTL_MILLIS,
                address = request.address,
            ),
        )

        // Send
        if (isTestAddress(request.address)) { // Never send SMS to test addresses
            logger.add("test_address", true)
            return otp
        }

        val locale = LocaleContextHolder.getLocale()
        val messageId = getMessaging(request).send(
            Message(
                recipient = Party(
                    phoneNumber = request.address,
                    email = request.address,
                    deviceToken = request.address,
                ),
                subject = messageSource.getMessage(
                    "verification_subject",
                    arrayOf(),
                    locale,
                ),
                body = messageSource.getMessage(
                    "verification_message",
                    arrayOf(otp.code),
                    locale,
                ),
                mimeType = "text/plain;charset=UTF-8",
            ),
        )
        logger.add("message_id", messageId)

        return otp
    }

    fun verify(token: String, request: VerifyOTPRequest): OtpEntity {
        val otp = dao.findById(token)
            .orElseThrow {
                ConflictException(
                    error = Error(
                        code = ErrorURN.OTP_EXPIRED.urn,
                    ),
                )
            }

        if (otp.expires < System.currentTimeMillis()) {
            throw ConflictException(
                error = Error(
                    code = ErrorURN.OTP_EXPIRED.urn,
                ),
            )
        }

        val test = isTestAddress(otp.address)
        logger.add("test_address", test)
        if (test) {
            // Done
        } else if (otp.code != request.code) {
            throw ConflictException(
                error = Error(
                    code = ErrorURN.OTP_NOT_VALID.urn,
                    data = mapOf(
                        "otp-code" to otp.code,
                        "request-code" to request.code,
                    ),
                ),
            )
        }
        return otp
    }

    private fun isTestAddress(address: String): Boolean =
        testAddresses.contains(address.lowercase())

    private fun getMessaging(request: CreateOTPRequest): MessagingService =
        when (request.type.uppercase()) {
            MessagingType.SMS.name -> messagingProvider.get(MessagingType.SMS)
            MessagingType.EMAIL.name -> messagingProvider.get(MessagingType.EMAIL)
            MessagingType.WHATSTAPP.name -> messagingProvider.get(MessagingType.WHATSTAPP)
            MessagingType.PUSH_NOTIFICATION.name -> messagingProvider.get(MessagingType.PUSH_NOTIFICATION)
            else -> throw BadRequestException(
                error = Error(
                    code = ErrorURN.OTP_ADDRESS_TYPE_NOT_VALID.urn,
                    parameter = Parameter(
                        type = ParameterType.PARAMETER_TYPE_PAYLOAD,
                        name = "type",
                        value = request.type,
                    ),
                ),
            )
        }

    private fun generateCode(length: Int): String {
        val buff = StringBuilder()
        while (buff.length < length) {
            val digit = (Math.random() * 10).toInt()
            buff.append(digit)
        }
        return buff.toString()
    }
}
