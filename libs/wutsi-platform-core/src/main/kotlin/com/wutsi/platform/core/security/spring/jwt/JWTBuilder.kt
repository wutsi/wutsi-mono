package com.wutsi.platform.core.security.spring.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.RSAKeyProvider
import com.wutsi.platform.core.security.SubjectType
import java.util.Date

class JWTBuilder(
    private val subject: String,
    private val subjectType: SubjectType,
    private val tenantId: Long? = null,
    private val name: String? = null,
    private val keyProvider: RSAKeyProvider,
    private val admin: Boolean = false,
    private val ttl: Long = 84600,
    private val scope: List<String> = emptyList(),
    private val phoneNumber: String? = null,
    private val email: String? = null,
) {
    companion object {
        const val ISSUER = "wutsi.com"
        const val CLAIM_SUBJECT_TYPE = "sub_type"
        const val CLAIM_NAME = "name"
        const val CLAIM_PHONE_NUMBER = "phone_number"
        const val CLAIM_EMAIL = "email"
        const val CLAIM_SCOPE = "scope"
        const val CLAIM_ADMIN = "admin"
        const val CLAIM_TENANT_ID = "tenant_id"
    }

    fun build(): String {
        val builder = JWT.create()
            .withIssuer(ISSUER)
            .withIssuedAt(Date(System.currentTimeMillis()))
            .withExpiresAt(Date(System.currentTimeMillis() + ttl))
            .withJWTId(keyProvider.privateKeyId)
            .withSubject(subject)
            .withClaim(CLAIM_SUBJECT_TYPE, subjectType.name)
            .withClaim(CLAIM_SCOPE, scope)
            .withClaim(CLAIM_ADMIN, admin)

        if (tenantId != null) {
            builder.withClaim(CLAIM_TENANT_ID, tenantId)
        }

        if (name != null) {
            builder.withClaim(CLAIM_NAME, name)
        }

        if (email != null) {
            builder.withClaim(CLAIM_EMAIL, email)
        }

        if (phoneNumber != null) {
            builder.withClaim(CLAIM_PHONE_NUMBER, phoneNumber)
        }

        return builder.sign(Algorithm.RSA256(keyProvider))
    }
}
