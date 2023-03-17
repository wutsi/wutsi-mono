package com.wutsi.codegen.kotlin

import com.wutsi.codegen.Context
import com.wutsi.codegen.core.util.CaseUtil.toCamelCase
import com.wutsi.codegen.model.Api
import com.wutsi.codegen.model.Endpoint
import com.wutsi.codegen.model.EndpointParameter
import com.wutsi.codegen.model.EndpointSecurity
import com.wutsi.codegen.model.Field
import com.wutsi.codegen.model.ParameterType
import com.wutsi.codegen.model.Request
import com.wutsi.codegen.model.Security
import com.wutsi.codegen.model.SecurityLocation
import com.wutsi.codegen.model.SecurityType
import com.wutsi.codegen.model.Server
import com.wutsi.codegen.model.Type
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.media.ArraySchema
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.parameters.CookieParameter
import io.swagger.v3.oas.models.parameters.HeaderParameter
import io.swagger.v3.oas.models.parameters.Parameter
import io.swagger.v3.oas.models.parameters.PathParameter
import io.swagger.v3.oas.models.parameters.QueryParameter
import io.swagger.v3.oas.models.parameters.RequestBody
import io.swagger.v3.oas.models.responses.ApiResponses
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import java.time.LocalDate
import java.time.OffsetDateTime
import kotlin.reflect.KClass

class KotlinMapper(private val context: Context) {
    companion object {
        val OPENAPI_TYPE_TO_KOLTIN: Map<String, KClass<*>> = mapOf(
            "string" to String::class,
            "string:date" to LocalDate::class,
            "string:date-time" to OffsetDateTime::class,
            "string:binary" to ByteArray::class,

            "number" to Double::class,
            "number:int32" to Int::class,
            "number:int64" to Long::class,
            "number:float" to Float::class,
            "number:double" to Double::class,

            "integer" to Int::class,
            "integer:int32" to Int::class,
            "integer:int64" to Long::class,

            "boolean" to Boolean::class,
            "array" to List::class,
            "object" to Any::class,
        )
    }

    fun toAPI(openAPI: OpenAPI) = Api(
        packageName = context.basePackage,
        name = toCamelCase("${context.apiName}Api", true),
        endpoints = toEndpoints(openAPI),
        servers = toServers(openAPI),
        securities = toSecurities(openAPI),
    )

    fun toServers(openAPI: OpenAPI): List<Server> =
        openAPI.servers?.map { toServer(it) } ?: emptyList()

    fun toServer(server: io.swagger.v3.oas.models.servers.Server) = Server(
        url = server.url,
        description = server.description,
    )

    fun toSecurities(openAPI: OpenAPI): List<Security> =
        openAPI.components?.securitySchemes?.entries?.map { toSecurity(it.key, it.value) } ?: emptyList()

    fun toSecurity(name: String, scheme: SecurityScheme) = Security(
        name = name,
        location = toSecurityLocaltion(scheme.`in`),
        type = toSecurityType(scheme.type),
    )

    fun toSecurityLocaltion(location: SecurityScheme.In): SecurityLocation =
        if (location == SecurityScheme.In.COOKIE) {
            SecurityLocation.COOKIE
        } else if (location == SecurityScheme.In.HEADER) {
            SecurityLocation.HEADER
        } else if (location == SecurityScheme.In.QUERY) {
            SecurityLocation.QUERY
        } else {
            SecurityLocation.INVALID
        }

    fun toSecurityType(type: SecurityScheme.Type): SecurityType =
        if (type == SecurityScheme.Type.APIKEY) {
            SecurityType.API_KEY
        } else if (type == SecurityScheme.Type.HTTP) {
            SecurityType.HTTP
        } else if (type == SecurityScheme.Type.OAUTH2) {
            SecurityType.OAUTH2
        } else if (type == SecurityScheme.Type.OPENIDCONNECT) {
            SecurityType.OPENID
        } else {
            SecurityType.INVALID
        }

    fun toEndpoints(openAPI: OpenAPI): List<Endpoint> {
        val endpoints = mutableListOf<Endpoint>()
        openAPI.paths?.forEach { path, item ->
            item.get?.let { endpoints.add(toEndpoint(path, "GET", it)) }
            item.post?.let { endpoints.add(toEndpoint(path, "POST", it)) }
            item.delete?.let { endpoints.add(toEndpoint(path, "DELETE", it)) }
            item.put?.let { endpoints.add(toEndpoint(path, "PUT", it)) }
            item.trace?.let { endpoints.add(toEndpoint(path, "TRACE", it)) }
            item.patch?.let { endpoints.add(toEndpoint(path, "PATCH", it)) }
            item.head?.let { endpoints.add(toEndpoint(path, "HEAD", it)) }
        }
        return endpoints
    }

    fun toEndpoint(path: String, method: String, operation: Operation) = Endpoint(
        path = path,
        name = toCamelCase(operation.operationId?.let { it } ?: "${method.lowercase()}$path", false),
        method = method.uppercase(),
        request = operation.requestBody?.let { toRequest(it) },
        response = toResponse(operation.responses),
        parameters = operation.parameters?.map { toParameter(it) } ?: emptyList(),
        securities = operation.security?.map { toEndpointSecurity(it) } ?: emptyList(),
    )

    fun toEndpointSecurity(security: SecurityRequirement) = EndpointSecurity(
        name = security.keys.toList()[0],
        scopes = security[security.keys.toList()[0]]?.toList() ?: emptyList(),
    )

    fun toRequest(body: RequestBody): Request {
        val requests = mutableListOf<Request>()
        body.content.forEach { contentType, media ->
            requests.add(
                Request(
                    contentType = contentType,
                    type = context.getType(media.schema.`$ref`) ?: Type("Any", "kotlin"),
                    required = body.required?.let { it } ?: false,
                ),
            )
        }
        return requests[0]
    }

    fun toResponse(responses: ApiResponses?): Type? {
        if (responses == null || responses.isEmpty()) {
            return null
        }

        val response = responses.values.toList()[0]
        if (response.content == null || response.content.isEmpty()) {
            return null
        }

        val schema = response.content.values.toList()[0].schema
        val ref = schema.`$ref`
        if (ref == null) {
            val ktype = schema.type?.let { OPENAPI_TYPE_TO_KOLTIN[it] }
            return ktype?.let { Type(name = it.simpleName!!, packageName = "kotlin") }
        } else {
            return context.getType(ref)
        }
    }

    fun toParameter(parameter: Parameter) = EndpointParameter(
        type = when (parameter.javaClass) {
            QueryParameter::class.java -> ParameterType.QUERY
            PathParameter::class.java -> ParameterType.PATH
            HeaderParameter::class.java -> ParameterType.HEADER
            CookieParameter::class.java -> ParameterType.COOKIE
            else -> throw IllegalStateException("Unsupported parameters ${parameter.javaClass}")
        },
        field = toField(parameter.name, parameter.schema, null),
        name = parameter.name,
    )

    fun <T> toType(name: String, schema: Schema<T>) = Type(
        name = toCamelCase(name, true),
        packageName = toPackage(context.basePackage, "dto"),
        fields = schema.properties?.map {
            println("  $name.${it.key}")
            toField(it.key, it.value, schema as Schema<Any>)
        } ?: emptyList(),
    )

    fun toField(name: String, property: Schema<*>, schema: Schema<*>? = null, required: Boolean? = null) = Field(
        name = toCamelCase(name, false),
        type = toKClass(property),
        parametrizedType = toParametrizedType(property),
        default = property.default?.toString(),
        required = required ?: (schema?.required?.contains(name) == true),
        min = property.minimum,
        max = property.maximum,
        pattern = property.pattern,
        minLength = property.minLength,
        maxLength = property.maxLength,
        minItems = property.minItems,
        maxItems = property.maxItems,
        nullable = property.anyOf?.let { it.find { it.type == "null" } != null }
            ?: property.nullable?.let { it }
            ?: false,
    )

    fun <T> toKClass(property: Schema<T>): KClass<*> {
        val type = property.type
        val result: KClass<*>? = if (type == null) {
            Any::class
        } else if (type == "array") {
            List::class
        } else {
            val format = property.format
            OPENAPI_TYPE_TO_KOLTIN["$type:$format"] ?: OPENAPI_TYPE_TO_KOLTIN[type]
        }

        return result
            ?: throw IllegalStateException("Unable to resolve the type. ${property.name}:$type")
    }

    fun <T> toParametrizedType(property: Schema<T>): Type? {
        if (property.type == "array" || property.type == "object" || property.type == null) {
            val ref = if (property.type == "array") {
                val type = (property as ArraySchema).items?.type
                if (type != null) {
                    val format = (property as ArraySchema).items?.format
                    val ktype = OPENAPI_TYPE_TO_KOLTIN["$type:$format"] ?: OPENAPI_TYPE_TO_KOLTIN[type]
                    if (ktype != null) {
                        return Type(name = ktype.simpleName!!, packageName = "kotlin")
                    } else {
                        null
                    }
                }
                (property as ArraySchema).items?.`$ref`
            } else {
                property.`$ref`
                    ?: property.anyOf?.find { it.`$ref` != null }?.`$ref`
            }

            return ref?.let { context.getType(it) }
                ?: throw IllegalStateException("Unable to resolve the reference: $ref")
        } else {
            return null
        }
    }

    private fun toPackage(basePackage: String, suffix: String): String =
        if (basePackage.isNullOrEmpty()) suffix else "$basePackage.$suffix"
}
