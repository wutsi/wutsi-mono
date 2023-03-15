package com.wutsi.platform.core.messaging.push

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Notification
import com.wutsi.platform.core.messaging.Message

class PushMessagingServiceFirebase(private val fm: FirebaseMessaging) : PushMessagingService {
    override fun send(message: Message): String {
        val data = mutableMapOf<String, String>()
        data.putAll(message.data)
        data["click_action"] = "FLUTTER_NOTIFICATION_CLICK" // Flutter integration

        return fm.send(
            com.google.firebase.messaging.Message.builder()
                .setNotification(
                    Notification.builder()
                        .setTitle(message.subject)
                        .setBody(message.body)
                        .setImage(message.imageUrl)
                        .build(),
                )
                .putAllData(data)
                .setToken(message.recipient.deviceToken)
                .build(),
        )
    }
}
