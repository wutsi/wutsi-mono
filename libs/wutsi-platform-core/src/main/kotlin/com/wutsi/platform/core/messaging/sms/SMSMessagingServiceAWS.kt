package com.wutsi.platform.core.messaging.sms

import com.amazonaws.services.sns.AmazonSNS
import com.amazonaws.services.sns.model.MessageAttributeValue
import com.amazonaws.services.sns.model.PublishRequest
import com.amazonaws.services.sns.model.PublishResult
import com.wutsi.platform.core.messaging.Message
import com.wutsi.platform.core.messaging.UrlShortener
import java.text.Normalizer

class SMSMessagingServiceAWS(
    private val sns: AmazonSNS,
    private val urlShortener: UrlShortener,
) : SMSMessagingService {
    companion object {
        private val REGEX_UNACCENT = "\\p{InCombiningDiacriticalMarks}+".toRegex()
    }

    override fun send(message: Message): String {
        val body = StringBuilder()
        body.append(normalizeMessage(message.body))
        if (message.url != null) {
            body.append(" ").append(urlShortener.shorten(message.url))
        }

        val result: PublishResult = sns.publish(
            PublishRequest()
                .withMessage(body.toString())
                .withPhoneNumber(message.recipient.phoneNumber)
                .withMessageAttributes(
                    mapOf(
                        "AWS.SNS.SMS.SMSType" to MessageAttributeValue()
                            .withStringValue("Transactional")
                            .withDataType("String"),
                    ),
                ),
        )
        return result.messageId
    }

    private fun normalizeMessage(message: String): String {
        val temp = Normalizer.normalize(message, Normalizer.Form.NFD)
        return REGEX_UNACCENT.replace(temp, "")
    }
}
