package com.wutsi.membership.access.service

import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.wutsi.membership.access.dao.PhoneRepository
import com.wutsi.membership.access.dto.Phone
import com.wutsi.membership.access.entity.PhoneEntity
import org.springframework.stereotype.Service
import java.time.ZoneOffset

@Service
class PhoneService(private val dao: PhoneRepository) {
    fun findOrCreate(phoneNumber: String): PhoneEntity {
        val util = PhoneNumberUtil.getInstance()
        val phone = util.parse(phoneNumber, "")
        val number = util.format(phone, PhoneNumberUtil.PhoneNumberFormat.E164)

        return dao.findByNumber(number).orElseGet {
            dao.save(
                PhoneEntity(
                    number = number,
                    country = util.getRegionCodeForCountryCode(phone.countryCode),
                ),
            )
        }
    }

    fun toPhone(phone: PhoneEntity) = Phone(
        id = phone.id ?: -1,
        number = phone.number,
        country = phone.country,
        created = phone.created.toInstant().atOffset(ZoneOffset.UTC),
    )
}
