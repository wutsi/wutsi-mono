package com.wutsi.blog.ads.domain

import com.wutsi.blog.ads.dto.AdsCTAType
import com.wutsi.blog.ads.dto.AdsStatus
import com.wutsi.blog.ads.dto.AdsType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name = "T_ADS")
data class AdsEntity(
    @Id
    val id: String? = null,

    @Column(name = "user_fk")
    val userId: Long = -1,

    var title: String = "",
    var imageUrl: String? = null,
    var url: String? = null,
    var type: AdsType = AdsType.UNKNOWN,
    var ctaType: AdsCTAType = AdsCTAType.UNKNOWN,
    var status: AdsStatus = AdsStatus.DRAFT,
    var durationDays: Int = 0,
    var startDate: Date = Date(),
    var endDate: Date? = null,
    var totalImpressions: Long = 0L,
    var totalClicks: Long = 0L,
    val creationDateTime: Date = Date(),
    var modificationDateTime: Date = Date(),
    var completedDateTime: Date? = null,
    var budget: Long = 0L,
    val currency: String = "",
    var maxImpressions: Long = 0,
    var maxDailyImpressions: Long = 0,
    var todayImpressions: Long = 0,
)
