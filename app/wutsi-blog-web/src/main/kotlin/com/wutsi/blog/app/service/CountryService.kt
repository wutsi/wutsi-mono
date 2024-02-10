package com.wutsi.blog.app.service

import com.wutsi.blog.app.mapper.CountryMapper
import com.wutsi.blog.app.model.CountryModel
import com.wutsi.blog.app.model.PaymentProviderTypeModel
import com.wutsi.blog.country.dto.Country
import org.springframework.stereotype.Service

@Service
class CountryService(
    private val mapper: CountryMapper,
) {
    final val all: List<CountryModel> = Country.all.map { country -> mapper.toCountryModel(country) }
    final val paymentProviderTypes: List<PaymentProviderTypeModel> =
        all.flatMap { country -> country.paymentProviderTypes }
            .toSet()
            .toList()
}
