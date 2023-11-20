package com.wutsi.blog.mail.service

import com.wutsi.blog.backend.PersonalizeBackend
import com.wutsi.blog.mail.service.model.LinkModel
import com.wutsi.blog.story.domain.StoryEntity
import com.wutsi.blog.story.mapper.StoryMapper
import com.wutsi.blog.story.service.StoryService
import com.wutsi.blog.subscription.dto.SearchSubscriptionRequest
import com.wutsi.blog.subscription.service.SubscriptionService
import com.wutsi.blog.user.domain.UserEntity
import com.wutsi.ml.personalize.dto.SortStoryRequest
import com.wutsi.ml.personalize.dto.Story
import com.wutsi.platform.core.messaging.Message
import com.wutsi.platform.core.messaging.Party
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import java.util.Locale
import javax.annotation.PostConstruct

@Service
class WeeklyMailSender(
	private val smtp: SMTPSender,
	private val personalizeBackend: PersonalizeBackend,
	private val subscriptionService: SubscriptionService,
	private val templateEngine: TemplateEngine,
	private val mailFilterSet: MailFilterSet,
	private val mapper: StoryMapper,
	private val storyService: StoryService,

	@Value("\${wutsi.application.asset-url}") private val assetUrl: String,
	@Value("\${wutsi.application.website-url}") private val webappUrl: String,
	@Value("\${wutsi.application.mail.weekly-digest.whitelist.email}") private val emailWhitelist: String,
	@Value("\${wutsi.application.mail.weekly-digest.whitelist.country}") private val countryWhitelist: String,
) {
	companion object {
		private val LOGGER = LoggerFactory.getLogger(WeeklyMailSender::class.java)
	}

	@PostConstruct
	fun init() {
		LOGGER.info(">>> Email Whitelist: $emailWhitelist")
		LOGGER.info(">>> Country Whitelist: $countryWhitelist")
	}

	fun send(stories: List<StoryEntity>, users: List<UserEntity>, recipient: UserEntity): Boolean {
		if (!isWhitelisted(recipient)) {
			return false
		}

		// Sort stories
		val sorted: List<Story> = try {
			personalizeBackend.sort(
				SortStoryRequest(
					storyIds = stories.mapNotNull { it.id },
					userId = recipient.id!!,
				)
			).stories
		} catch (ex: Exception) {
			LOGGER.warn("Unable to sort stories for User#${recipient.id}", ex)
			emptyList()
		}

		// Remove stories that I'm subscribed to
		val xstories = dedupByUser(
			filterOutStoriesFromSubscriptions(
				stories = sort(
					stories.filter { it.language == recipient.language && it.userId != recipient.id },
					sorted
				),
				recipient = recipient
			)
		).take(10) // Top 10
		if (xstories.isEmpty()) {
			return false
		}

		val scores = sorted.associate { it.id to it.score }
		val message = createEmailMessage(xstories, users, scores, recipient)
		return smtp.send(message) != null
	}

	private fun dedupByUser(stories: List<StoryEntity>): List<StoryEntity> {
		val userIds = mutableSetOf<Long>()
		return stories.filter { userIds.add(it.userId) }
	}

	private fun filterOutStoriesFromSubscriptions(
		stories: List<StoryEntity>,
		recipient: UserEntity
	): List<StoryEntity> {
		val userIds = subscriptionService.search(
			SearchSubscriptionRequest(
				subscriberId = recipient.id,
				limit = 100
			)
		).map { it.userId }
		return stories.filter { !userIds.contains(it.id) }
	}

	private fun sort(stories: List<StoryEntity>, sorted: List<Story>): List<StoryEntity> {
		val map = stories.associateBy { it.id }
		return storyService.bubbleDown( // Push read stories at the bottom of the list
			stories = sorted.mapNotNull { map[it.id] }
		)
	}

	private fun createEmailMessage(
		stories: List<StoryEntity>,
		users: List<UserEntity>,
		scores: Map<Long, Double>,
		recipient: UserEntity,
	) = Message(
		sender = Party(
			displayName = "Wutsi Weekly Digest",
		),
		recipient = Party(
			email = recipient.email ?: "",
			displayName = recipient.fullName,
		),
		language = recipient.language,
		mimeType = "text/html;charset=UTF-8",
		data = mapOf(),
		subject = stories[0].title,
		body = generateBody(stories, users, scores, recipient, createMailContext(recipient)),
	)

	private fun generateBody(
		stories: List<StoryEntity>,
		users: List<UserEntity>,
		scores: Map<Long, Double>,
		recipient: UserEntity,
		mailContext: MailContext,
	): String {
		val thymleafContext = Context(Locale(recipient.language ?: "en"))
		thymleafContext.setVariable("recipientName", recipient.fullName.ifEmpty { null })
		thymleafContext.setVariable("stories", toLinkModel(stories, users, scores, mailContext))
		thymleafContext.setVariable("context", mailContext)

		val body = templateEngine.process("mail/weekly-digest.html", thymleafContext)
		return mailFilterSet.filter(
			body = body,
			context = mailContext,
		)
	}

	private fun toLinkModel(
		stories: List<StoryEntity>,
		users: List<UserEntity>,
		scores: Map<Long, Double>,
		mailContext: MailContext
	): List<LinkModel> {
		val userMap = users.associateBy { it.id }
		return stories.map { story ->
			LinkModel(
				title = story.title ?: "",
				url = mailContext.websiteUrl + mapper.slug(story) + "?referer=weekly-digest",
				summary = story.summary,
				thumbnailUrl = story.thumbnailUrl,
				author = userMap[story.userId]?.fullName,
				authorPictureUrl = userMap[story.userId]?.pictureUrl,
				authorUrl = userMap[story.userId]?.let { "$webappUrl/@/${it.name}" },
				score = scores[story.id] ?: 0.0
			)
		}
	}

	private fun createMailContext(recipient: UserEntity): MailContext {
		return MailContext(
			assetUrl = assetUrl,
			websiteUrl = webappUrl,
			template = "wutsi",
			storyId = null,
			blog = Blog(
				name = null,
				fullName = "Wutsi",
				language = recipient.language ?: "en",
				logoUrl = "$assetUrl/assets/wutsi/img/logo/logo_512x512.png",
			),
		)
	}

	private fun isWhitelisted(recipient: UserEntity): Boolean {
		val email = recipient.email
		val country = recipient.country
		return !email.isNullOrEmpty() &&
				(emailWhitelist == "*" || emailWhitelist.contains(email)) &&
				!country.isNullOrEmpty() &&
				(countryWhitelist == "*" || countryWhitelist.contains(country))
	}
}
