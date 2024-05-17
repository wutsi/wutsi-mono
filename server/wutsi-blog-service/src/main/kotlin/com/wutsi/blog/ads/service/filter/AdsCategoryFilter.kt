package com.wutsi.blog.ads.service.filter

import com.wutsi.blog.ads.domain.AdsEntity
import com.wutsi.blog.ads.dto.SearchAdsRequest
import com.wutsi.blog.ads.service.AdsFilter
import com.wutsi.blog.backend.IpApiBackend
import com.wutsi.blog.user.domain.UserEntity

class AdsCategoryFilter(private val ipApi: IpApiBackend) : AdsFilter {
    override fun filter(request: SearchAdsRequest, ads: List<AdsEntity>, user: UserEntity?): List<AdsEntity> {
        return ads.filter { item ->
            item.category != null || item.country.equals(getCountry(request, user), true)
        }
    }

    private fun getCountry(request: SearchAdsRequest, user: UserEntity?): String? =
        user?.country ?: getCountry(request)

    private fun getCountry(request: SearchAdsRequest): String? =
        request.impressionContext?.ip?.let { ip ->
            try {
                ipApi.resolve(ip).countryCode
            } catch (ex: Exception) {
                null
            }
        }
}