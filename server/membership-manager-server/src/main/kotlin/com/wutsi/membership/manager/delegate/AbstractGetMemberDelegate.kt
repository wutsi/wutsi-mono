package com.wutsi.membership.manager.delegate

import com.wutsi.enums.AccountStatus
import com.wutsi.membership.access.dto.Account
import com.wutsi.membership.manager.dto.Category
import com.wutsi.membership.manager.dto.Member
import com.wutsi.membership.manager.dto.Place

public abstract class AbstractGetMemberDelegate {
    protected fun toMember(account: Account) = Member(
        id = account.id,
        name = account.name,
        displayName = account.displayName,
        pictureUrl = account.pictureUrl,
        business = account.business,
        country = account.country,
        language = account.language,
        active = account.status == AccountStatus.ACTIVE.name,
        superUser = account.superUser,
        facebookId = account.facebookId,
        twitterId = account.twitterId,
        youtubeId = account.youtubeId,
        instagramId = account.instagramId,
        whatsapp = account.whatsapp,
        phoneNumber = account.phone.number,
        timezoneId = account.timezoneId,
        storeId = account.storeId,
        businessId = account.businessId,
        biography = account.biography,
        street = account.street,
        website = account.website,
        email = account.email,
        category = account.category
            ?.let {
                Category(
                    id = it.id,
                    title = it.title,
                )
            },
        city = account.city
            ?.let {
                Place(
                    id = it.id,
                    name = it.name,
                    longName = it.longName,
                    longitude = it.longitude,
                    latitude = it.latitude,
                    type = it.type,
                    timezoneId = it.timezoneId,
                )
            },
    )
}
