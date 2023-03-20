package com.wutsi.security.manager.service

import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.Parameter
import com.wutsi.platform.core.error.ParameterType
import com.wutsi.platform.core.error.exception.NotFoundException
import com.wutsi.security.manager.entity.KeyEntity
import com.wutsi.security.manager.error.ErrorURN
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.util.Base64
import java.util.Date

@Service
class KeyService(private val dao: com.wutsi.security.manager.dao.KeyRepository) {
    companion object {
        const val ALGO = "RSA"
        const val KEY_SIZE = 2048
        const val KEY_TTL_MILLIS = 28 * 84600000L // 28 days
    }

    fun findById(id: Long): KeyEntity {
        val key = dao.findById(id)
            .orElseThrow {
                NotFoundException(
                    error = Error(
                        code = ErrorURN.KEY_NOT_FOUND.urn,
                        parameter = Parameter(
                            name = "id",
                            value = id.toString(),
                            type = ParameterType.PARAMETER_TYPE_PATH,
                        ),
                    ),
                )
            }

        if (expired(key)) {
            throw NotFoundException(
                error = Error(
                    code = ErrorURN.KEY_EXPIRED.urn,
                    parameter = Parameter(
                        name = "id",
                        value = id.toString(),
                        type = ParameterType.PARAMETER_TYPE_PATH,
                    ),
                ),
            )
        }

        return key
    }

    fun findCurrentKey(): KeyEntity {
        val expires = Date()
        val keys = dao.findByExpiresLessThan(expires, PageRequest.of(1, 1))
        return if (keys.isEmpty()) {
            createKey()
        } else {
            keys[0]
        }
    }

    private fun createKey(): KeyEntity {
        val keyPair = createKeyPair()
        val encoder = Base64.getEncoder()
        return dao.save(
            KeyEntity(
                algorithm = ALGO,
                created = Date(),
                expires = Date(System.currentTimeMillis() + KEY_TTL_MILLIS),
                privateKey = encoder.encodeToString(keyPair.private.encoded),
                publicKey = encoder.encodeToString(keyPair.public.encoded),
            ),
        )
    }

    private fun createKeyPair(): KeyPair {
        val generator = KeyPairGenerator.getInstance(ALGO)
        generator.initialize(KEY_SIZE)
        return generator.generateKeyPair()
    }

    private fun expired(key: KeyEntity): Boolean =
        key.expires.time < System.currentTimeMillis()
}
