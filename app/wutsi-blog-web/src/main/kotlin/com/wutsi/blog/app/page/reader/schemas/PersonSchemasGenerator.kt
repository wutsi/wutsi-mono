package com.wutsi.blog.app.page.reader.schemas

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.blog.app.model.UserModel
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class PersonSchemasGenerator(
    private val objectMapper: ObjectMapper,
    @Value("\${wutsi.application.server-url}") private val baseUrl: String,
) {
    fun generate(person: UserModel): String {
        val schemas = generateMap(person)
        return objectMapper.writeValueAsString(schemas)
    }

    fun generateMap(person: UserModel): Map<String, Any> {
        val schemas = mutableMapOf<String, Any>()

        schemas["@context"] = "https://schema.org/"
        schemas["@type"] = "Person"
        schemas["id"] = "$baseUrl/person/${person.id}"
        schemas["name"] = person.fullName

        if (person.pictureUrl != null) {
            schemas["image"] = person.pictureUrl
        }
        if (person.biography != null) {
            schemas["description"] = person.biography
        }

        schemas["url"] = "${baseUrl}${person.slug}"
        if (person.hasSocialLinks) {
            schemas["sameAs"] = arrayListOf(
                person.facebookUrl,
                person.linkedinUrl,
                person.youtubeUrl,
                person.twitterUrl,
            ).filter { it != null }
        }
        return schemas
    }
}
