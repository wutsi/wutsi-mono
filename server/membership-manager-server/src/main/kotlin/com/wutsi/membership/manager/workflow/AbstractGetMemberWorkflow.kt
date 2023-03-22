package com.wutsi.membership.manager.workflow

import com.wutsi.enums.AccountStatus
import com.wutsi.error.ErrorURN
import com.wutsi.membership.access.MembershipAccessApi
import com.wutsi.membership.access.dto.Account
import com.wutsi.membership.manager.dto.Category
import com.wutsi.membership.manager.dto.GetMemberResponse
import com.wutsi.membership.manager.dto.Member
import com.wutsi.membership.manager.dto.Place
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.exception.NotFoundException
import com.wutsi.workflow.WorkflowContext
import com.wutsi.workflow.engine.Workflow
import com.wutsi.workflow.engine.WorkflowEngine
import feign.FeignException
import org.springframework.beans.factory.annotation.Autowired
import javax.annotation.PostConstruct

abstract class AbstractGetMemberWorkflow : Workflow {
    @Autowired
    protected lateinit var workflowEngine: WorkflowEngine

    @Autowired
    protected lateinit var membershipAccessApi: MembershipAccessApi

    protected abstract fun id(): String

    protected abstract fun findAccount(context: WorkflowContext): Account

    @PostConstruct
    fun init() {
        workflowEngine.register(id(), this)
    }

    override fun execute(context: WorkflowContext) {
        try {
            context.output = GetMemberResponse(
                member = toMember(findAccount(context)),
            )
        } catch (ex: FeignException.NotFound) {
            throw NotFoundException(
                error = Error(
                    code = ErrorURN.MEMBER_NOT_FOUND.urn,
                ),
            )
        }
    }

    private fun toMember(account: Account) = Member(
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
