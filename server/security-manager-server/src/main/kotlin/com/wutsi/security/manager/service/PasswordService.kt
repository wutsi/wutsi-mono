package com.wutsi.security.manager.service

import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.exception.ConflictException
import com.wutsi.platform.core.error.exception.NotFoundException
import com.wutsi.security.manager.dao.PasswordRepository
import com.wutsi.security.manager.dto.CreatePasswordRequest
import com.wutsi.security.manager.dto.UpdatePasswordRequest
import com.wutsi.security.manager.dto.VerifyPasswordRequest
import com.wutsi.security.manager.entity.PasswordEntity
import com.wutsi.security.manager.error.ErrorURN
import org.apache.commons.codec.digest.DigestUtils
import org.springframework.stereotype.Service
import java.util.Date
import java.util.UUID

@Service
class PasswordService(
    private val dao: PasswordRepository,
) {
    fun create(request: CreatePasswordRequest): PasswordEntity {
        val salt = UUID.randomUUID().toString()
        return dao.save(
            PasswordEntity(
                accountId = request.accountId,
                username = request.username,
                value = hash(request.accountId, request.value, salt),
                salt = salt,
            ),
        )
    }

    fun update(accountId: Long, request: UpdatePasswordRequest) {
        val password = findByAccountId(accountId)
        password.value = hash(password.accountId, request.value, password.salt)
        dao.save(password)
    }

    fun verify(accountId: Long, request: VerifyPasswordRequest): PasswordEntity {
        val password = findByAccountId(accountId)
        val value = hash(password.accountId, request.value, password.salt)
        if (value != password.value) {
            throw ConflictException(
                error = Error(
                    code = ErrorURN.PASSWORD_MISMATCH.urn,
                ),
            )
        }
        return password
    }

    fun delete(accountId: Long) {
        val password = dao.findByAccountIdAndIsDeleted(accountId, false)
        password.forEach {
            delete(it)
        }
    }

    private fun delete(password: PasswordEntity) {
        if (!password.isDeleted) {
            password.isDeleted = true
            password.deleted = Date()
            dao.save(password)
        }
    }

    fun findByAccountId(accountId: Long): PasswordEntity {
        val passwords = dao.findByAccountIdAndIsDeleted(accountId, false)
        if (passwords.isEmpty()) {
            throw NotFoundException(
                error = Error(
                    code = ErrorURN.PASSWORD_NOT_FOUND.urn,
                    data = mapOf(
                        "account-id" to accountId,
                    ),
                ),
            )
        }
        return passwords[0]
    }

    fun findByUsername(username: String): PasswordEntity {
        val passwords = dao.findByUsernameAndIsDeleted(username, false)
        if (passwords.isEmpty()) {
            throw NotFoundException(
                error = Error(
                    code = ErrorURN.PASSWORD_NOT_FOUND.urn,
                    data = mapOf(
                        "username" to username,
                    ),
                ),
            )
        } else {
            return passwords[0]
        }
    }

    private fun hash(accountId: Long, value: String, salt: String): String =
        DigestUtils.md5Hex("$accountId-$value-$salt")
}
