package com.wutsi.platform.core.messaging.whatsapp

import org.junit.jupiter.api.Test
import java.net.http.HttpClient
import kotlin.test.Ignore
import kotlin.test.assertEquals

internal class WAClientTest {
    val client = WAClient(
        phoneId = "110568735113393",

        /*Get new temporary token from https://developers.facebook.com/apps/592926305704636/whatsapp-business/wa-dev-console */
        accessToken = "EAAIbQ2nUWrwBAFaB8Q6oJAdw0tnKzgUKo5e86RrFbh3ZBaWAMZBllIWzNXDTCZAByZCutesV6TigZAYlgGUNZBn9cz36EbvdSnvJJwlymBU4yj8ZBbG2bVqRvAZCyBca61ZBHqkVGVFZAhu7JeI6y42u8bh9job2hAnzkZAfXKaevND17QjNynfSzt3wZBLrMRJwZB7BZANVh7FvSlaQZDZD",
        client = HttpClient.newHttpClient(),
    )

    @Test
    fun empty() {
    }

    @Test
    @Ignore
    fun messages() {
        val msg = WAMessage(
            to = "15147580191",
            text = WAText(body = "Hello"),
        )
        val response = client.messages(msg)

        assertEquals(msg.to, response.contacts[0].input)
    }
}
