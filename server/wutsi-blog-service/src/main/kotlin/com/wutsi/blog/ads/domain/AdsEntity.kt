package com.wutsi.blog.ads.domain

import com.wutsi.blog.ads.dto.AdsCTAType
import com.wutsi.blog.ads.dto.AdsStatus
import com.wutsi.blog.ads.dto.AdsType
import com.wutsi.blog.ads.dto.Gender
import com.wutsi.blog.ads.dto.OS
import com.wutsi.blog.transaction.domain.TransactionEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
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
    var startDate: Date? = null,
    var endDate: Date? = null,
    var totalImpressions: Long = 0L,
    var totalClicks: Long = 0L,
    val creationDateTime: Date = Date(),
    var modificationDateTime: Date = Date(),
    var completedDateTime: Date? = null,
    var publishedDateTime: Date? = null,
    var budget: Long = 0L,
    val currency: String = "",
    var maxImpressions: Long = 0,
    var maxDailyImpressions: Long = 0,
    var todayImpressions: Long = 0,
    var country: String? = null,
    var language: String? = null,
    var gender: Gender? = null,
    var os: OS? = null,
    var email: Boolean? = null,

    @ManyToOne
    @JoinColumn(name = "transaction_fk")
    var transaction: TransactionEntity? = null,
)
