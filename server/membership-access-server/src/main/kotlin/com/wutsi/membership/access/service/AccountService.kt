package com.wutsi.membership.access.service

import com.wutsi.enums.AccountStatus
import com.wutsi.membership.access.dao.AccountRepository
import com.wutsi.membership.access.dto.Account
import com.wutsi.membership.access.dto.AccountSummary
import com.wutsi.membership.access.dto.CreateAccountRequest
import com.wutsi.membership.access.dto.EnableBusinessRequest
import com.wutsi.membership.access.dto.SearchAccountRequest
import com.wutsi.membership.access.dto.UpdateAccountAttributeRequest
import com.wutsi.membership.access.dto.UpdateAccountStatusRequest
import com.wutsi.membership.access.entity.AccountEntity
import com.wutsi.membership.access.entity.PhoneEntity
import com.wutsi.membership.access.error.ErrorURN
import com.wutsi.membership.access.util.AccountHandleUtil
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.Parameter
import com.wutsi.platform.core.error.ParameterType
import com.wutsi.platform.core.error.exception.BadRequestException
import com.wutsi.platform.core.error.exception.ConflictException
import com.wutsi.platform.core.error.exception.NotFoundException
import org.springframework.stereotype.Service
import java.time.ZoneOffset
import java.util.Date
import javax.persistence.EntityManager
import javax.persistence.Query

@Service
class AccountService(
    private val dao: AccountRepository,
    private val placeService: PlaceService,
    private val phoneService: PhoneService,
    private val categoryService: CategoryService,
    private val nameService: NameService,
    private val em: EntityManager,
) {
    companion object {
        const val DEFAULT_LANGUAGE = "en"
    }

    fun create(request: CreateAccountRequest): AccountEntity {
        // Phone number
        val phone = phoneService.findOrCreate(request.phoneNumber)
        ensureNotAssigned(phone)

        // Account
        val city = request.cityId?.let { placeService.findById(it) }
        return dao.save(
            AccountEntity(
                phone = phone,
                city = city,
                country = request.country,
                displayName = request.displayName,
                language = request.language,
                pictureUrl = request.pictureUrl,
                status = AccountStatus.ACTIVE,
                timezoneId = city?.timezoneId ?: getTimezoneByCountry(request.country),
            ),
        )
    }

    fun getTimezoneByCountry(country: String): String? {
        val tzs = com.ibm.icu.util.TimeZone.getAvailableIDs(country)
        return if (tzs.isNotEmpty()) tzs[0] else null
    }

    fun update(id: Long, request: UpdateAccountAttributeRequest): AccountEntity {
        val account = findById(id)

        when (request.name.lowercase()) {
            "display-name" -> account.displayName = toString(request.value)!!
            "picture-url" -> account.pictureUrl = toString(request.value)
            "language" -> account.language = request.value ?: DEFAULT_LANGUAGE
            "biography" -> account.biography = toString(request.value)
            "website" -> account.website = toString(request.value)
            "category-id" -> account.category = toLong(request.value)?.let { categoryService.findById(it) }
            "whatsapp" -> account.whatsapp = toBoolean(request.value) ?: false
            "street" -> account.street = toString(request.value)
            "city-id" -> account.city = toLong(request.value)!!.let { placeService.findById(it) }
            "timezone-id" -> account.timezoneId = toString(request.value)
            "email" -> account.email = toString(request.value)
            "facebook-id" -> account.facebookId = toString(request.value)
            "instagram-id" -> account.instagramId = toString(request.value)
            "twitter-id" -> account.twitterId = toString(request.value)
            "youtube-id" -> account.youtubeId = toString(request.value)
            "business-id" -> account.businessId = toLong(request.value)
            "store-id" -> account.storeId = toLong(request.value)
            "fundraising-id" -> account.fundraisingId = toLong(request.value)
            "name" -> {
                val value = toString(request.value)
                if (value == null) {
                    deleteName(account)
                } else {
                    account.name = nameService.save(value, account)
                }
            }
            else -> throw BadRequestException(
                error = Error(
                    code = ErrorURN.ATTRIBUTE_NOT_VALID.urn,
                    parameter = Parameter(
                        name = "name",
                        value = request.name,
                        type = ParameterType.PARAMETER_TYPE_PAYLOAD,
                    ),
                ),
            )
        }
        dao.save(account)
        return account
    }

    fun status(id: Long, request: UpdateAccountStatusRequest): AccountEntity? {
        val account = findById(id, true)
        when (request.status.uppercase()) {
            AccountStatus.ACTIVE.name -> activate(account)
            AccountStatus.INACTIVE.name -> suspend(account)
            else -> return null
        }
        return dao.save(account)
    }

    fun enableBusiness(id: Long, request: EnableBusinessRequest): AccountEntity {
        val account = findById(id)

        account.category = request.categoryId.let { categoryService.findById(it) }
        account.city = placeService.findById(request.cityId)
        account.business = true
        account.displayName = request.displayName
        account.whatsapp = request.whatsapp
        account.country = request.country
        account.street = request.street
        account.biography = request.biography
        account.email = request.email
        dao.save(account)

        return assignHandle(account)
    }

    private fun assignHandle(account: AccountEntity): AccountEntity {
        val handle = AccountHandleUtil.generate(account.displayName, NameService.MAX_LENGTH)
        account.name = nameService.save(handle, account, false)
        return dao.save(account)
    }

    fun disableBusiness(id: Long): AccountEntity {
        val account = findById(id)
        return disableBusiness(account)
    }

    private fun disableBusiness(account: AccountEntity): AccountEntity {
        if (account.business) {
            account.business = false
            dao.save(account)
        }
        return account
    }

    fun findByName(name: String, acceptSuspended: Boolean = false): AccountEntity {
        val memberName = nameService.findByName(name)
        val account = dao.findByName(memberName)
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

        if (account.status == AccountStatus.INACTIVE && !acceptSuspended) {
            throw NotFoundException(
                error = Error(
                    code = ErrorURN.ACCOUNT_SUSPENDED.urn,
                    parameter = Parameter(
                        name = "name",
                        value = name,
                        type = ParameterType.PARAMETER_TYPE_PATH,
                    ),
                ),
            )
        }
        return account
    }

    fun findById(id: Long, acceptSuspended: Boolean = false): AccountEntity {
        val account = dao.findById(id)
            .orElseThrow {
                NotFoundException(
                    error = Error(
                        code = ErrorURN.ACCOUNT_NOT_FOUND.urn,
                        parameter = Parameter(
                            name = "id",
                            value = id,
                            type = ParameterType.PARAMETER_TYPE_PATH,
                        ),
                    ),
                )
            }

        if (account.status == AccountStatus.INACTIVE && !acceptSuspended) {
            throw NotFoundException(
                error = Error(
                    code = ErrorURN.ACCOUNT_SUSPENDED.urn,
                    parameter = Parameter(
                        name = "id",
                        value = id,
                        type = ParameterType.PARAMETER_TYPE_PATH,
                    ),
                ),
            )
        }
        return account
    }

    fun toAccount(account: AccountEntity, language: String?) = Account(
        id = account.id ?: -1,
        displayName = account.displayName,
        timezoneId = account.timezoneId,
        pictureUrl = account.pictureUrl,
        country = account.country,
        language = account.language,
        status = account.status.name,
        email = account.email,
        whatsapp = account.whatsapp,
        biography = account.biography,
        street = account.street,
        instagramId = account.instagramId,
        youtubeId = account.youtubeId,
        facebookId = account.facebookId,
        twitterId = account.twitterId,
        website = account.website,
        business = account.business,
        superUser = account.superUser,
        deactivated = account.deactivated?.toInstant()?.atOffset(ZoneOffset.UTC),
        created = account.created.toInstant().atOffset(ZoneOffset.UTC),
        updated = account.updated.toInstant().atOffset(ZoneOffset.UTC),
        phone = phoneService.toPhone(account.phone),
        category = account.category?.let { categoryService.toCategory(it, language) },
        city = account.city?.let { placeService.toPlace(it, language) },
        businessId = account.businessId,
        storeId = account.storeId,
        fundraisingId = account.fundraisingId,
        name = account.name?.value,
    )

    fun toAccountSummary(account: AccountEntity, language: String?) = AccountSummary(
        id = account.id ?: -1,
        displayName = account.displayName,
        pictureUrl = account.pictureUrl,
        country = account.country,
        language = account.language,
        status = account.status.name,
        business = account.business,
        superUser = account.superUser,
        created = account.created.toInstant().atOffset(ZoneOffset.UTC),
        categoryId = account.category?.id,
        cityId = account.city?.id,
        businessId = account.businessId,
        storeId = account.storeId,
        fundraisingId = account.fundraisingId,
        name = account.name?.value,
    )

    fun search(request: SearchAccountRequest): List<AccountEntity> {
        val query = em.createQuery(sql(request))
        parameters(request, query)
        return query
            .setFirstResult(request.offset)
            .setMaxResults(request.limit)
            .resultList as List<AccountEntity>
    }

    private fun sql(request: SearchAccountRequest): String {
        val select = select()
        val where = where(request)
        val order = order()
        return if (where.isNullOrEmpty()) {
            select
        } else {
            "$select WHERE $where $order"
        }
    }

    private fun order(): String =
        "ORDER BY a.displayName"

    private fun select(): String =
        "SELECT a FROM AccountEntity a"

    private fun where(request: SearchAccountRequest): String {
        val criteria = mutableListOf<String>()

        if (!request.phoneNumber.isNullOrEmpty()) {
            criteria.add("a.phone.number=:phone_number")
        }
        if (request.accountIds.isNotEmpty()) {
            criteria.add("a.id IN :ids")
        }
        if (request.business != null) {
            criteria.add("a.business=:business")
        }
        if (request.store == true) {
            criteria.add("a.storeId IS NOT NULL")
        } else if (request.store == false) {
            criteria.add("a.storeId IS NULL")
        }
        if (request.status != null) {
            criteria.add("a.status=:status")
        }
        return criteria.joinToString(separator = " AND ")
    }

    private fun parameters(request: SearchAccountRequest, query: Query) {
        if (!request.phoneNumber.isNullOrEmpty()) {
            query.setParameter("phone_number", normalizePhoneNumber(request.phoneNumber))
        }
        if (request.accountIds.isNotEmpty()) {
            query.setParameter("ids", request.accountIds)
        }
        if (request.business != null) {
            query.setParameter("business", request.business)
        }
        if (request.status != null) {
            query.setParameter("status", AccountStatus.valueOf(request.status.uppercase()))
        }
    }

    private fun activate(account: AccountEntity) {
        account.status = AccountStatus.ACTIVE
        account.deactivated = null
    }

    private fun suspend(account: AccountEntity) {
        account.status = AccountStatus.INACTIVE
        account.deactivated = Date()
        deleteName(account)
    }

    private fun deleteName(account: AccountEntity) {
        account.name?.let {
            nameService.delete(it)
            account.name = null
        }
    }

    private fun normalizePhoneNumber(phoneNumber: String?): String? {
        phoneNumber ?: return null

        val value = phoneNumber.trim()
        return if (value.startsWith("+")) {
            value
        } else {
            "+$value"
        }
    }

    private fun ensureNotAssigned(phone: PhoneEntity) {
        val account = dao.findByPhoneAndStatus(phone, AccountStatus.ACTIVE)
        if (account.isPresent) {
            throw ConflictException(
                error = Error(
                    code = ErrorURN.PHONE_NUMBER_ALREADY_ASSIGNED.urn,
                    parameter = Parameter(
                        name = "phoneNumber",
                        type = ParameterType.PARAMETER_TYPE_PAYLOAD,
                        value = phone.number,
                    ),
                ),
            )
        }
    }

    private fun toString(value: String?): String? =
        if (value.isNullOrEmpty()) {
            null
        } else {
            value
        }

    private fun toLong(value: String?): Long? =
        toString(value)?.toLong()

    private fun toBoolean(value: String?): Boolean? =
        toString(value)?.toBoolean()
}
