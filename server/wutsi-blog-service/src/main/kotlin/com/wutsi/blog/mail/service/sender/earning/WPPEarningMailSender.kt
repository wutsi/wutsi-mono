package com.wutsi.blog.mail.service.sender.earning

import com.wutsi.blog.country.dto.Country
import com.wutsi.blog.earning.entity.WPPStoryEntity
import com.wutsi.blog.earning.entity.WPPUserEntity
import com.wutsi.blog.mail.service.model.StoryEarningModel
import com.wutsi.blog.mail.service.sender.AbstractWutsiMailSender
import com.wutsi.blog.story.dto.SearchStoryRequest
import com.wutsi.blog.story.service.StoryService
import com.wutsi.blog.transaction.domain.WalletEntity
import com.wutsi.blog.transaction.service.WalletService
import com.wutsi.blog.user.domain.UserEntity
import com.wutsi.platform.core.messaging.Message
import com.wutsi.platform.core.messaging.Party
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.thymeleaf.context.Context
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Service
class WPPEarningMailSender(
    private val walletService: WalletService,
    private val storyService: StoryService,
) : AbstractWutsiMailSender() {
    @Transactional
    fun send(user: WPPUserEntity, recipient: UserEntity, date: LocalDate, stories: List<WPPStoryEntity>): Boolean {
        if (recipient.walletId.isNullOrEmpty()) {
            return false
        }

        val wallet = walletService.findById(recipient.walletId!!)
        val message = createEmailMessage(user, recipient, wallet, date, stories)
        smtp.send(message)
        return true
    }

    private fun createEmailMessage(
        user: WPPUserEntity,
        recipient: UserEntity,
        wallet: WalletEntity,
        date: LocalDate,
        stories: List<WPPStoryEntity>,
    ): Message {
        val language = getLanguage(recipient)
        return Message(
            sender = Party(
                displayName = "Wutsi Partner Program",
            ),
            recipient = Party(
                email = recipient.email ?: "",
                displayName = recipient.fullName,
            ),
            language = language,
            mimeType = "text/html;charset=UTF-8",
            data = mapOf(),
            subject = messages.getMessage("wpp_earning.subject", emptyArray(), Locale(language)),
            body = generateBody(user, recipient, wallet, date, language, stories),
        )
    }

    private fun generateBody(
        user: WPPUserEntity,
        recipient: UserEntity,
        wallet: WalletEntity,
        date: LocalDate,
        language: String,
        wstories: List<WPPStoryEntity>,
    ): String {
        val country = Country.all.find { wallet.country.equals(it.code, true) }!!
        val fmt = DecimalFormat(country.monetaryFormat)

        val thymleafContext = Context(Locale(language))
        thymleafContext.setVariable("period", date.format(DateTimeFormatter.ofPattern("MMM yyyy", Locale(language))))
        thymleafContext.setVariable("recipientName", recipient.fullName)
        thymleafContext.setVariable("earnings", fmt.format(user.earnings))
        thymleafContext.setVariable("bonus", fmt.format(user.bonus))
        thymleafContext.setVariable("total", fmt.format(user.total))
        thymleafContext.setVariable("threshold", fmt.format(country.wppEarningThreshold))
        thymleafContext.setVariable("stories", findStories(wstories, user, wallet))

        if (user.total >= country.wppEarningThreshold) {
            thymleafContext.setVariable("toReceive", fmt.format(user.total))
        } else {
            thymleafContext.setVariable("toReceive", fmt.format(0L))
            thymleafContext.setVariable("belowThreshold", true)
        }

        val body = templateEngine.process("mail/wpp-earning.html", thymleafContext)
        return mailFilterSet.filter(
            body = body,
            context = createMailContext("Wutsi", language),
        )
    }

    private fun findStories(
        wstories: List<WPPStoryEntity>,
        user: WPPUserEntity,
        wallet: WalletEntity,
    ): List<StoryEarningModel> {
        val storyMap = storyService.searchStories(
            SearchStoryRequest(
                storyIds = wstories.map { it.id },
                userIds = listOf(user.userId),
                limit = wstories.size
            )
        ).associateBy { it.id }

        val country = Country.all.find { country -> country.code.equals(wallet.country, true) }
        val fmt = country?.createMoneyFormat() ?: DecimalFormat()

        return wstories.mapNotNull { wstory ->
            storyMap[wstory.id]?.let { story ->
                StoryEarningModel(
                    id = wstory.id,
                    title = story.title ?: "",
                    wppScore = "${story.wppScore}%",
                    earnings = fmt.format(wstory.earnings),
                    bonus = fmt.format(wstory.bonus),
                    total = fmt.format(wstory.total)
                )
            }
        }
    }
}
