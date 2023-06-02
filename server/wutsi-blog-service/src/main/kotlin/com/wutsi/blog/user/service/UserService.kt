package com.wutsi.blog.account.service

import com.wutsi.blog.account.dao.SearchUserQueryBuilder
import com.wutsi.blog.user.dao.UserEntityRepository
import com.wutsi.blog.user.domain.UserEntity
import com.wutsi.blog.client.user.AuthenticateRequest
import com.wutsi.blog.client.user.SearchUserRequest
import com.wutsi.blog.client.user.UpdateUserAttributeRequest
import com.wutsi.blog.util.Predicates
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.exception.ConflictException
import com.wutsi.platform.core.error.exception.NotFoundException
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.storage.StorageService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.net.URL
import java.time.Clock
import java.util.Date
import java.util.UUID
import javax.imageio.ImageIO
import javax.persistence.EntityManager

@Service
class UserServiceV0(
    private val dao: UserEntityRepository,
    private val clock: Clock,
    private val em: EntityManager,
    private val logger: KVLogger,
    private val storage: StorageService,

    @Value("\${wutsi.website.url}") private val websiteUrl: String,
) {
    fun findById(id: Long): UserEntity {
        val user = dao
            .findById(id)
            .orElseThrow { NotFoundException(Error("user_not_found")) }

        if (user.suspended) {
            throw NotFoundException(Error("session_expired"))
        }

        return user
    }

    fun search(request: SearchUserRequest): List<UserEntity> {
        val builder = SearchUserQueryBuilder()
        val sql = builder.query(request)
        val params = builder.parameters(request)
        val query = em.createNativeQuery(sql, UserEntity::class.java)
        Predicates.setParameters(query, params)
        return query.resultList as List<UserEntity>
    }

    fun count(request: SearchUserRequest): Number {
        val builder = SearchUserQueryBuilder()
        val sql = builder.count(request)
        val params = builder.parameters(request)
        val query = em.createNativeQuery(sql)
        Predicates.setParameters(query, params)
        return query.singleResult as Number
    }

    @Transactional
    fun downloadImage(id: Long) {
        val user = findById(id)
        if (user.pictureUrl.isNullOrBlank()) {
            return
        }

        val url = URL(user.pictureUrl)
        if (storage.contains(url)) {
            return
        }

        // Download
        val img = ImageIO.read(url)
        val out = ByteArrayOutputStream()
        ImageIO.write(img, "png", out)

        // Store
        val filename = "picture-" + UUID.randomUUID().toString() + ".png"
        val input = ByteArrayInputStream(out.toByteArray())
        val xurl = storage.store("user/$id/$filename", input)
        set(
            id,
            UpdateUserAttributeRequest(
                name = "picture_url",
                value = xurl.toString(),
            ),
        )
    }

    @Transactional
    fun set(id: Long, request: UpdateUserAttributeRequest): UserEntity {
        val user = findById(id)
        val name = request.name!!.lowercase()

        if ("name" == name) {
            rename(user, request.value!!)
        } else if ("email" == name) {
            updateEmail(user, request.value!!)
        } else if ("full_name" == name) {
            user.fullName = request.value!!
        } else if ("biography" == name) {
            user.biography = request.value!!
        } else if ("picture_url" == name) {
            user.pictureUrl = request.value
        } else if ("website_url" == name) {
            user.websiteUrl = request.value
        } else if ("language" == name) {
            user.language = request.value
        } else if ("read_all_languages" == name) {
            user.readAllLanguages = ("true" == request.value!!)
        } else if ("facebook_id" == name) {
            user.facebookId = request.value
        } else if ("twitter_id" == name) {
            user.twitterId = request.value
        } else if ("linkedin_id" == name) {
            user.linkedinId = request.value
        } else if ("youtube_id" == name) {
            user.youtubeId = request.value
        } else if ("blog" == name) {
            user.blog = request.value?.toBoolean() == true
        } else if ("whatsapp_id" == name) {
            user.whatsappId = request.value
        } else if ("telegram_id" == name) {
            user.telegramId = request.value
        } else {
            throw ConflictException(Error("invalid_attribute"))
        }

        user.lastLoginDateTime = Date(clock.millis())
        return save(user)
    }

    @Transactional
    fun createUser(request: AuthenticateRequest): UserEntity {
        val name = generateName(request)

        logger.add("CreateUser", true)
        logger.add("Username", name)

        val user = UserEntity(
            fullName = request.fullName!!,
            email = request.email?.lowercase(),
            pictureUrl = request.pictureUrl,
            name = name,
            language = request.language,
            siteId = request.siteId!!,
        )
        return save(user)
    }

    @Transactional
    fun sessionStarted(now: Date, user: UserEntity, request: AuthenticateRequest) {
        if (request.email != null && user.email != request.email) {
            user.email = request.email
        }
        if (user.pictureUrl.isNullOrEmpty()) {
            user.pictureUrl = request.pictureUrl
        }
        if (user.fullName.isEmpty()) {
            user.fullName = request.fullName!!
        }
        if (user.language.isNullOrEmpty()) {
            user.language = request.language
        }
        user.loginCount = user.loginCount + 1
        user.lastLoginDateTime = now
        save(user)
    }

    @Transactional
    fun save(user: UserEntity): UserEntity = dao.save(user)

    fun url(user: UserEntity): String =
        websiteUrl + "/@/${user.name}"

    fun findByName(name: String): UserEntity =
        dao.findByNameIgnoreCase(name)
            .orElseThrow { NotFoundException(Error("user_not_found")) }

    fun findByEmail(email: String): UserEntity =
        dao.findByEmailIgnoreCase(email)
            .orElseThrow { NotFoundException(Error("user_not_found")) }

    private fun rename(user: UserEntity, value: String) {
        checkNameUnique(user, value)
        user.name = value.lowercase()
    }

    private fun updateEmail(user: UserEntity, email: String) {
        checkEmailUnique(user, email)
        user.email = email
    }

    private fun checkNameUnique(user: UserEntity, name: String) {
        val dup = dao.findByNameIgnoreCase(name)
        if (dup.isPresent && dup.get().id != user.id) {
            throw ConflictException(Error("duplicate_name"))
        }
    }

    private fun checkEmailUnique(user: UserEntity, name: String) {
        val dup = dao.findByEmailIgnoreCase(name)
        if (dup.isPresent && dup.get().id != user.id) {
            throw ConflictException(Error("duplicate_email"))
        }
    }

    private fun generateName(request: AuthenticateRequest): String {
        var name = extractNameFromEmail(request)
        if (name == null) {
            name = extractNameFromProviderId(request)
        }
        return if (name == null) request.providerUserId + "-" + System.currentTimeMillis() else name
    }

    private fun extractNameFromEmail(request: AuthenticateRequest): String? {
        if (request.email != null) {
            val i = request.email!!.indexOf("@")
            val name = request.email!!.substring(0, i)

            val user = dao.findByNameIgnoreCase(name)
            return if (user.isPresent) null else name
        }
        return null
    }

    private fun extractNameFromProviderId(request: AuthenticateRequest): String? {
        val user = dao.findByNameIgnoreCase(request.providerUserId!!)
        return if (user.isPresent) null else request.providerUserId
    }
}
