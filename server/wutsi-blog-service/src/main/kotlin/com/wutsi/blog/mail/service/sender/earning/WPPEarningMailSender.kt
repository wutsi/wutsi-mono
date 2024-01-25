package com.wutsi.blog.mail.service.sender.earning

import com.wutsi.blog.country.dto.Country
import com.wutsi.blog.earning.entity.WPPUserEntity
import com.wutsi.blog.mail.service.sender.AbstractWutsiMailSender
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
) : AbstractWutsiMailSender() {
    @Transactional
    fun send(user: WPPUserEntity, recipient: UserEntity, date: LocalDate): Boolean {
        if (recipient.walletId.isNullOrEmpty()) {
            return false
        }

        val wallet = walletService.findById(recipient.walletId!!)
        val message = createEmailMessage(user, recipient, wallet, date)
        smtp.send(message)
        return true
    }

    private fun createEmailMessage(
        user: WPPUserEntity,
        recipient: UserEntity,
        wallet: WalletEntity,
        date: LocalDate,
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
            body = generateBody(user, recipient, wallet, date, language),
        )
    }

    private fun generateBody(
        user: WPPUserEntity,
        recipient: UserEntity,
        wallet: WalletEntity,
        date: LocalDate,
        language: String,
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
}
