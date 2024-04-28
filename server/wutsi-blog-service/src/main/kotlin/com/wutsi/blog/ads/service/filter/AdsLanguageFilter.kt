package com.wutsi.blog.ads.service.filter

import com.wutsi.blog.ads.domain.AdsEntity
import com.wutsi.blog.ads.dto.SearchAdsRequest
import com.wutsi.blog.ads.service.AdsFilter
import com.wutsi.blog.country.dto.Country
import com.wutsi.blog.user.domain.UserEntity

class AdsLanguageFilter : AdsFilter {
    override fun filter(request: SearchAdsRequest, ads: List<AdsEntity>, user: UserEntity?): List<AdsEntity> {
        return ads.filter { item ->
            item.language.isNullOrEmpty() || item.language.equals(getLanguage(user), true)
        }
    }

    private fun getLanguage(user: UserEntity?): String? =
        user?.language
            ?: user?.country?.let { code ->
                Country.fromCode(code)?.languages?.get(0)
            }
}