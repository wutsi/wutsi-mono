package com.wutsi.security.manager.dao

import com.wutsi.security.manager.entity.OtpEntity
import org.springframework.cache.Cache
import org.springframework.stereotype.Service
import java.util.Optional

@Service
public class OtpRepository(private val cache: Cache) {
    fun save(otp: OtpEntity): OtpEntity {
        cache.put(otp.token, otp)
        return otp
    }

    fun findById(token: String): Optional<OtpEntity> =
        cache.get(token, OtpEntity::class.java)?.let { Optional.of(it) } ?: Optional.empty()
}
