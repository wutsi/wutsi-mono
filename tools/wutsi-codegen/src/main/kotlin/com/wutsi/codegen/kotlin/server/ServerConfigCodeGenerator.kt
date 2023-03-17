package com.wutsi.codegen.kotlin.server

import com.wutsi.codegen.Context
import com.wutsi.codegen.core.generator.AbstractMustacheCodeGenerator
import com.wutsi.codegen.core.util.DatabaseUtil
import com.wutsi.codegen.kotlin.KotlinMapper
import io.swagger.v3.oas.models.OpenAPI
import java.io.File

class ServerConfigCodeGenerator : AbstractMustacheCodeGenerator() {
    override fun toMustacheScope(openAPI: OpenAPI, context: Context): Map<String, Any?> {
        val api = KotlinMapper(context).toAPI(openAPI)
        return mapOf(
            "services" to toServices(context),
            "security" to api.isSecured(),
            "basePackage" to context.basePackage,
            "name" to context.apiName.lowercase(),
        )
    }

    override fun canGenerate(file: File) = !file.exists()

    private fun toServices(context: Context): Map<String, Any?> {
        val result = mutableMapOf<String, Any?>()
        if (context.hasService(Context.SERVICE_DATABASE)) {
            result["database"] = true
            result["databaseName"] = DatabaseUtil.toDatabaseName(context.apiName)
        }
        if (context.hasService(Context.SERVICE_AWS_POSTGRES)) {
            result["aws_postgres"] = true
            result["databaseName"] = DatabaseUtil.toDatabaseName(context.apiName)
        }
        if (context.hasService(Context.SERVICE_AWS_MYSQL)) {
            result["aws_mysql"] = true
            result["databaseName"] = DatabaseUtil.toDatabaseName(context.apiName)
        }
        if (context.hasService(Context.SERVICE_CACHE)) {
            result["cache"] = true
        }
        if (context.hasService(Context.SERVICE_MQUEUE)) {
            result["mqueue"] = true
        }
        if (context.hasService(Context.SERVICE_API_KEY)) {
            result["apiKey"] = true
        }
        if (context.hasService(Context.SERVICE_SLACK)) {
            result["slack"] = true
        }
        if (context.hasService(Context.SERVICE_MESSAGING)) {
            result["messaging"] = true
        }
        return result
    }

    override fun generate(openAPI: OpenAPI, context: Context) {
        generate("application.yml", openAPI, context)
        generate("application-test.yml", openAPI, context)
        generate("application-prod.yml", openAPI, context)
        generate("logback.xml", openAPI, context)
    }

    private fun generate(filename: String, spec: OpenAPI, context: Context) {
        generate(
            inputPath = "/kotlin/server/$filename.mustache",
            outputFile = File("${context.outputDirectory}/src/main/resources/$filename"),
            openAPI = spec,
            context = context,
        )
    }
}
