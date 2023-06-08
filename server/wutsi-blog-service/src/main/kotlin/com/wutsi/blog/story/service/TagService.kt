package com.wutsi.blog.story.service

import com.wutsi.blog.story.dao.StoryRepository
import com.wutsi.blog.story.dao.TagRepository
import com.wutsi.blog.story.domain.StoryEntity
import com.wutsi.blog.story.domain.TagEntity
import com.wutsi.blog.util.SlugGenerator
import org.apache.commons.text.WordUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.text.Normalizer
import java.time.Clock
import java.util.Date
import java.util.regex.Pattern
import javax.persistence.EntityManager

@Service
class TagService(
    private val clock: Clock,
    private val dao: TagRepository,
    private val storyDao: StoryRepository,
    private val em: EntityManager,
) {
    fun find(names: List<String>): List<TagEntity> {
        if (names.isEmpty()) {
            return emptyList()
        }
        return dao.findByNameIn(names.map { toName(it) })
    }

    @Transactional
    fun findOrCreate(names: List<String>): List<TagEntity> {
        if (names.isEmpty()) {
            return mutableListOf()
        }

        val tags = find(names)
        if (tags.size == names.size) {
            return tags
        }

        val created = createNewTags(names, tags)

        val joined = mutableListOf<TagEntity>()
        joined.addAll(tags)
        joined.addAll(created)
        return joined
    }

    fun search(query: String): List<TagEntity> =
        dao.findByNameStartsWithOrderByTotalStoriesDesc(toName(query))

    @Transactional
    fun onStoryPublished(story: StoryEntity) =
        updateTotalStories(story)

    @Transactional
    fun onStoryDeleted(story: StoryEntity) =
        updateTotalStories(story)

    private fun updateTotalStories(story: StoryEntity) {
        story.tags.forEach {
            val sql = """
                update T_TAG T set total_stories=(
                    SELECT COUNT(*)
                        FROM T_STORY_TAG ST JOIN T_STORY S on ST.story_fk=S.id
                        WHERE ST.tag_fk=${it.id} AND S.deleted=false
                ) where T.id=${it.id}
            """.trimIndent()
            em.createNativeQuery(sql).executeUpdate()
        }
    }

    fun toName(name: String): String {
        val slug = SlugGenerator.generate("", unaccent(name.lowercase()))
        return slug.substring(1)
    }

    private fun createNewTags(names: List<String>, tags: List<TagEntity>): Iterable<TagEntity> {
        val now = Date(clock.millis())
        val map: Map<String, TagEntity> = tags.map { it.name to it }.toMap()
        val created = names
            .filter { map[toName(it)] == null }
            .map { createTag(it, now) }
            .associateBy { it.name } // To prevent name duplication

        return dao.saveAll(created.values)
    }

    private fun createTag(name: String, now: Date) = TagEntity(
        name = toName(name),
        displayName = toDisplayName(name),
        creationDateTime = now,
    )

    private fun unaccent(s: String): String {
        val temp = Normalizer.normalize(s, Normalizer.Form.NFD)
        val pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
        return pattern.matcher(temp).replaceAll("")
    }

    private fun toDisplayName(name: String) = WordUtils.capitalize(name)
}
