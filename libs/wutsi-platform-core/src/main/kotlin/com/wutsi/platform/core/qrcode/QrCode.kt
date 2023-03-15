package com.wutsi.platform.core.qrcode

import org.apache.commons.codec.digest.DigestUtils
import java.time.Clock
import java.util.Base64

class QrCode(
    val type: String,
    val value: String,
    private val timeToLiveSeconds: Int? = null,
) {
    companion object {
        fun decode(data: String, keyProvider: KeyProvider, clock: Clock = Clock.systemUTC()): QrCode {
            // URL
            if (data.startsWith("http://") || data.startsWith("https://")) {
                return QrCode(
                    type = "URL",
                    value = data,
                )
            }

            // Decode
            val items = data.split(".")
            if (items.size != 3) {
                throw QrCodeException("Malformed QR code")
            }

            val decoder = Base64.getDecoder()
            val payload = String(decoder.decode(items[0]))
            val keyId = String(decoder.decode(items[1]))
            val signature = String(decoder.decode(items[2]))

            // Verify
            val verify = sign(payload, keyProvider)
            if (verify != signature) {
                throw CorruptedQrCodeException("Corrupted QR code")
            }

            // Parts
            val parts = payload.split(',')
            if (parts.size != 3) {
                throw QrCodeException("Malformed QR code")
            }

            val expires = parts[2].toInt()
            if (expires < clock.millis() / 1000) {
                throw ExpiredQrCodeException(expires.toString())
            }
            return QrCode(
                type = parts[0],
                value = parts[1],
                timeToLiveSeconds = expires - (clock.millis() / 1000).toInt(),
            )
        }

        private fun sign(payload: String, keyProvider: KeyProvider): String {
            val key = keyProvider.getKey(keyProvider.getKeyId())
            return DigestUtils.md5Hex("$payload-$key")
        }
    }

    fun encode(keyProvider: KeyProvider, clock: Clock = Clock.systemUTC()): String {
        val payload = generatePayload(clock)
        val signature = sign(payload, keyProvider)

        val keyId = keyProvider.getKeyId()
        val encoder = Base64.getEncoder()
        return encoder.encodeToString(payload.toByteArray()) + "." +
            encoder.encodeToString(keyId.toByteArray()) + "." +
            encoder.encodeToString(signature.toByteArray())
    }

    private fun generatePayload(clock: Clock): String {
        val expiry = timeToLiveSeconds?.let { (clock.millis() / 1000).toInt() + it } ?: Int.MAX_VALUE
        return "$type,$value,$expiry"
    }
}
