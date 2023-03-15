package com.wutsi.platform.core.security.spring.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
import com.wutsi.platform.core.security.SubjectType
import com.wutsi.platform.core.security.SubjectType.UNKNOWN
import com.wutsi.platform.core.security.WutsiPrincipal
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.security.Principal

class JWTAuthentication(private val decodedJWT: DecodedJWT) : Authentication {
    private var authenticated: Boolean = false
    private val principal: Principal
    private val authorities: MutableCollection<SimpleGrantedAuthority> = decodedJWT.claims[JWTBuilder.CLAIM_SCOPE]
        ?.asList(String::class.java)
        ?.map { SimpleGrantedAuthority(it) }
        ?.toMutableList()
        ?: mutableListOf()

    companion object {
        fun of(jwt: String): JWTAuthentication =
            JWTAuthentication(JWT.decode(jwt))
    }

    init {
        principal = WutsiPrincipal(
            id = decodedJWT.subject,
            type = toSubjectType(decodedJWT),
            _name = decodedJWT.getClaim(JWTBuilder.CLAIM_NAME).asString() ?: "",
            admin = decodedJWT.getClaim(JWTBuilder.CLAIM_ADMIN).asBoolean() ?: false,
            tenantId = decodedJWT.getClaim(JWTBuilder.CLAIM_TENANT_ID).asLong(),
        )
    }

    private fun toSubjectType(decodedJWT: DecodedJWT): SubjectType =
        try {
            SubjectType.valueOf(decodedJWT.getClaim(JWTBuilder.CLAIM_SUBJECT_TYPE).asString())
        } catch (ex: Exception) {
            UNKNOWN
        }

    override fun getName(): String =
        decodedJWT.subject

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> =
        authorities

    override fun getCredentials(): Any =
        ""

    override fun getDetails(): Any =
        decodedJWT

    override fun getPrincipal(): Any =
        principal

    override fun isAuthenticated(): Boolean =
        authenticated

    override fun setAuthenticated(value: Boolean) {
        this.authenticated = value
    }

    override fun toString(): String =
        "JWTAuthentication{${decodedJWT.token}}"
}
