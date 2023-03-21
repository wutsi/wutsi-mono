package com.wutsi.membership.access.service

import com.wutsi.membership.access.dao.NameRepository
import com.wutsi.membership.access.entity.AccountEntity
import com.wutsi.membership.access.entity.NameEntity
import com.wutsi.membership.access.error.ErrorURN
import com.wutsi.membership.access.util.AccountHandleUtil
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.Parameter
import com.wutsi.platform.core.error.ParameterType
import com.wutsi.platform.core.error.exception.ConflictException
import com.wutsi.platform.core.error.exception.NotFoundException
import org.springframework.stereotype.Service
import java.util.Date
import javax.transaction.Transactional

@Service
class NameService(private val dao: NameRepository) {
    companion object {
        const val MAX_LENGTH = 30
    }

    fun findByName(name: String): NameEntity =
        dao.findByValue(name.lowercase())
            .orElseThrow {
                NotFoundException(
                    error = Error(
                        code = ErrorURN.ACCOUNT_NOT_FOUND.urn,
                        parameter = Parameter(
                            name = "name",
                            value = name,
                            type = ParameterType.PARAMETER_TYPE_PATH,
                        ),
                    ),
                )
            }

    @Transactional
    fun save(value: String, account: AccountEntity, throwExceptionOnError: Boolean = true): NameEntity? {
        val xvalue = AccountHandleUtil.generate(value, MAX_LENGTH)
        val name = dao.findByValue(xvalue)
        if (account.name != null) {
            // Same value?
            if (account.name?.value == xvalue) {
                return account.name!!
            }

            // Name already assigned?
            if (name.isPresent) {
                if (throwExceptionOnError) {
                    throw alreadyAssignedException(xvalue)
                } else {
                    return null
                }
            }

            // Update the name
            account.name!!.value = xvalue
            account.name!!.updated = Date()
            dao.save(account.name)
            return account.name!!
        } else {
            // Already assigned?
            if (name.isPresent) {
                if (throwExceptionOnError) {
                    throw alreadyAssignedException(xvalue)
                } else {
                    return null
                }
            }

            // Set name
            return dao.save(
                NameEntity(
                    value = xvalue,
                ),
            )
        }
    }

    @Transactional
    fun delete(name: NameEntity) {
        dao.delete(name)
    }

    private fun alreadyAssignedException(value: String) = ConflictException(
        error = Error(
            code = ErrorURN.NAME_ALREADY_ASSIGNED.urn,
            parameter = Parameter(
                name = "name",
                value = value,
            ),
        ),
    )
}
