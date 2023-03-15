package com.wutsi.platform.core.messaging.whatsapp

import com.wutsi.platform.core.messaging.Message

class WAMessagingServiceCloud(
    private val client: WAClient,
) : WAMessagingService {
    override fun send(message: Message): String {
        val response = client.messages(
            WAMessage(
                to = formatPhoneNumber(message.recipient.phoneNumber),
                text = WAText(
                    body = message.body,
                    preview_url = true,
                ),
            ),
        )
        return if (response.messages.isEmpty()) {
            ""
        } else {
            response.messages[0].id
        }
    }

    private fun formatPhoneNumber(phoneNumber: String): String =
        if (phoneNumber.startsWith("+")) {
            phoneNumber.substring(1)
        } else {
            phoneNumber
        }
}
