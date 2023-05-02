package com.wutsi.blog.story.service

import com.wutsi.blog.story.dao.TagRepository
import com.wutsi.blog.story.domain.Tag
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
    private val em: EntityManager,
) {
    fun find(names: List<String>): List<Tag> {
        if (names.isEmpty()) {
            return emptyList()
        }

        return dao.findByNameIn(names.map { toName(it) })
    }

    @Transactional
    fun findOrCreate(names: List<String>): List<Tag> {
        if (names.isEmpty()) {
            return mutableListOf()
        }

        val tags = find(names)
        if (tags.size == names.size) {
            return tags
        }

        val created = createNewTags(names, tags)

        val joined = mutableListOf<Tag>()
        joined.addAll(tags)
        joined.addAll(created)
        return joined
    }

    fun search(query: String): List<Tag> =
        dao.findByNameStartsWithOrderByTotalStoriesDesc(toName(query))

    fun toName(name: String): String {
        val slug = SlugGenerator.generate("", unaccent(name.lowercase()))
        return slug.substring(1)
    }

    @Transactional
    fun updateTotalStories(): Int {
        val sql = "update T_TAG set total_stories=(SELECT COUNT(*) FROM T_STORY_TAG WHERE tag_fk=id);"
        return em.createNativeQuery(sql).executeUpdate()
    }

    private fun createNewTags(names: List<String>, tags: List<Tag>): Iterable<Tag> {
        val now = Date(clock.millis())
        val map: Map<String, Tag> = tags.map { it.name to it }.toMap()
        val created = names
            .filter { map[toName(it)] == null }
            .map { createTag(it, now) }
            .associateBy { it.name } // To prevent name duplication

        return dao.saveAll(created.values)
    }

    private fun createTag(name: String, now: Date) = Tag(
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
