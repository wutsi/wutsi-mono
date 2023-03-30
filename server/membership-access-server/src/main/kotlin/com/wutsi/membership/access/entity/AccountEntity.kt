package com.wutsi.membership.access.entity

import com.wutsi.enums.AccountStatus
import java.util.Date
import javax.persistence.Entity
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.OneToOne
import javax.persistence.Table

@Entity
@Table(name = "T_ACCOUNT")
data class AccountEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @OneToOne
    @JoinColumn(name = "phone_fk")
    var phone: PhoneEntity = PhoneEntity(),

    @OneToOne
    @JoinColumn(name = "city_fk")
    var city: PlaceEntity? = null,

    @OneToOne
    @JoinColumn(name = "category_fk")
    var category: CategoryEntity? = null,

    @OneToOne
    @JoinColumn(name = "name_fk")
    var name: NameEntity? = null,

    @Enumerated
    var status: AccountStatus = AccountStatus.UNKNOWN,

    var displayName: String = "",
    var pictureUrl: String? = null,
    val created: Date = Date(),
    val updated: Date = Date(),
    var language: String = "",
    var country: String = "",
    val superUser: Boolean = false,
    var business: Boolean = false,
    var website: String? = null,
    var biography: String? = null,
    var whatsapp: Boolean = false,
    var street: String? = null,
    var timezoneId: String? = null,
    var email: String? = null,
    var facebookId: String? = null,
    var instagramId: String? = null,
    var twitterId: String? = null,
    var youtubeId: String? = null,
    var deactivated: Date? = null,
    var storeId: Long? = null,
    var fundraisingId: Long? = null,
    var businessId: Long? = null,
)
