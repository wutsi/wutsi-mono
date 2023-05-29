package com.wutsi.blog.app.reader.schemas

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.blog.app.service.RequestContext
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class WutsiSchemasGenerator(
    private val objectMapper: ObjectMapper,
    private val requestContext: RequestContext,

    @Value("\${wutsi.application.server-url}") private val baseUrl: String,
    @Value("\${wutsi.application.asset-url}") private val assetUrl: String,
) {

    fun generate(): String {
        val schemas = generateMap()
        return objectMapper.writeValueAsString(schemas)
    }

    fun generateMap(): Map<String, Any> {
        val schemas = mutableMapOf<String, Any>()
        schemas["@context"] = "https://schema.org/"
        schemas["@type"] = "Organization"
        schemas["name"] = "Wutsi"
        schemas["description"] = requestContext.getMessage("wutsi.description", "")
        schemas["url"] = baseUrl
        schemas["logo"] = logo()
        return schemas
    }

    private fun logo(): Map<String, String> {
        val schemas = mutableMapOf<String, String>()
        schemas["@type"] = "ImageObject"
        schemas["url"] = "$assetUrl//assets/wutsi/img/logo/logo-512x512.png"
        schemas["width"] = "512"
        schemas["height"] = "512"
        return schemas
    }
}
