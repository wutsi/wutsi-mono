package com.wutsi.platform.payment.provider.flutterwave

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.platform.payment.provider.flutterwave.model.FWChargeRequest
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

open class FWEncryptor(
    private val objectMapper: ObjectMapper,
    private val encryptionKey: String,
) {
    open fun encrypt(request: FWChargeRequest): Map<String, String> {
        val json = objectMapper.writeValueAsString(request)
        return mapOf("client" to encrypt(json))
    }

    private fun encrypt(input: String): String {
        val key = SecretKeySpec(encryptionKey.toByteArray(Charsets.UTF_8), "DESede")
        val cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, key)

        val encryptedText = cipher.doFinal(input.toByteArray())
        return Base64.getEncoder().encodeToString(encryptedText)
    }
}
