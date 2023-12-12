package com.wutsi.blog.app.mapper

import com.wutsi.blog.app.model.StoreModel
import com.wutsi.blog.app.model.UserModel
import com.wutsi.blog.country.dto.Country
import com.wutsi.blog.product.dto.Store
import org.springframework.stereotype.Service

@Service
class StoreMapper(private val countryMapper: CountryMapper) {
    fun toStoreMapper(store: Store, user: UserModel) = StoreModel(
        id = store.id,
        currency = store.currency,
        country = countryMapper.toCountryModel(toCountry(user.country)),
        totalSales = store.totalSales,
        orderCount = store.orderCount,
        productCount = store.productCount,
        publishProductCount = 0,
    )

    private fun toCountry(country: String?): Country =
        Country.all.find { it.code.equals(country, true) }!!
}
