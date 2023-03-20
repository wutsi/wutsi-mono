package com.wutsi.codegen.kotlin.server

import com.wutsi.codegen.Context
import com.wutsi.codegen.core.util.DatabaseUtil
import com.wutsi.codegen.github.AbstractGithubActionsCodeGenerator
import com.wutsi.codegen.kotlin.KotlinMapper
import io.swagger.v3.oas.models.OpenAPI

class ServerGithubActionsCodeGenerator : AbstractGithubActionsCodeGenerator() {
    override fun getInputFilePath(filename: String): String =
        "/kotlin/server/.github/workflows/$filename.mustache"

    override fun toMustacheScope(openAPI: OpenAPI, context: Context) = mapOf(
        "apiName" to context.apiName.lowercase(),
        "artifactId" to ServerMavenCodeGenerator(KotlinMapper(context)).artifactId(context),
        "version" to openAPI.info?.version,
        "jdkVersion" to context.jdkVersion,
        "secrets.GITHUB_TOKEN" to "{{secrets.GITHUB_TOKEN}}",

        "secrets.SLACK_WEBHOOK_URL" to "{{secrets.SLACK_WEBHOOK_URL}}",

        "secrets.HEROKU_API_KEY_TEST" to "{{secrets.HEROKU_API_KEY_TEST}}",
        "secrets.CLOUDAMQP_URL_TEST" to "{{secrets.CLOUDAMQP_URL_TEST}}",
        "secrets.AWS_ACCESS_KEY_TEST" to "{{secrets.AWS_ACCESS_KEY_TEST}}",
        "secrets.AWS_SECRET_KEY_TEST" to "{{secrets.AWS_SECRET_KEY_TEST}}",
        "secrets.API_KEY_TEST" to "{{secrets.API_KEY_TEST}}",
        "secrets.LOG4J_SLACK_WEBHOOK_URL_TEST" to "{{secrets.LOG4J_SLACK_WEBHOOK_URL_TEST}}",
        "secrets.SMTP_USER_TEST" to "{{secrets.SMTP_USER_TEST}}",
        "secrets.SMTP_PASSWORD_TEST" to "{{secrets.SMTP_PASSWORD_TEST}}",
        "secrets.SMTP_HOST_TEST" to "{{secrets.SMTP_HOST_TEST}}",
        "secrets.SMTP_PORT_TEST" to "{{secrets.SMTP_PORT_TEST}}",
        "secrets.BITLY_ACCESS_TOKEN_TEST" to "{{secrets.BITLY_ACCESS_TOKEN_TEST}}",
        "secrets.WHATSAPP_ACCESS_TOKEN_TEST" to "{{secrets.WHATSAPP_ACCESS_TOKEN_TEST}}",
        "secrets.WHATSAPP_PHONE_ID_TEST" to "{{secrets.WHATSAPP_PHONE_ID_TEST}}",
        "secrets.FIREBASE_CREDENTIALS_TEST" to "{{secrets.FIREBASE_CREDENTIALS_TEST}}",
        "secrets.MYSQL_USERNAME_TEST" to "{{secrets.MYSQL_USERNAME_TEST}}",
        "secrets.MYSQL_PASSWORD_TEST" to "{{secrets.MYSQL_PASSWORD_TEST}}",
        "secrets.MYSQL_URL_TEST" to "{{secrets.MYSQL_URL_TEST}}",
        "secrets.POSTGRES_USERNAME_TEST" to "{{secrets.POSTGRES_USERNAME_TEST}}",
        "secrets.POSTGRES_PASSWORD_TEST" to "{{secrets.POSTGRES_PASSWORD_TEST}}",
        "secrets.POSTGRES_URL_TEST" to "{{secrets.POSTGRES_URL_TEST}}",

        "secrets.HEROKU_API_KEY_PROD" to "{{secrets.HEROKU_API_KEY_PROD}}",
        "secrets.CLOUDAMQP_URL_PROD" to "{{secrets.CLOUDAMQP_URL_PROD}}",
        "secrets.AWS_ACCESS_KEY_PROD" to "{{secrets.AWS_ACCESS_KEY_PROD}}",
        "secrets.AWS_SECRET_KEY_PROD" to "{{secrets.AWS_SECRET_KEY_PROD}}",
        "secrets.API_KEY_PROD" to "{{secrets.API_KEY_PROD}}",
        "secrets.LOG4J_SLACK_WEBHOOK_URL_PROD" to "{{secrets.LOG4J_SLACK_WEBHOOK_URL_PROD}}",
        "secrets.SMTP_USER_PROD" to "{{secrets.SMTP_USER_PROD}}",
        "secrets.SMTP_PASSWORD_PROD" to "{{secrets.SMTP_PASSWORD_PROD}}",
        "secrets.SMTP_HOST_PROD" to "{{secrets.SMTP_HOST_PROD}}",
        "secrets.SMTP_PORT_PROD" to "{{secrets.SMTP_PORT_PROD}}",
        "secrets.BITLY_ACCESS_TOKEN_PROD" to "{{secrets.BITLY_ACCESS_TOKEN_PROD}}",
        "secrets.WHATSAPP_ACCESS_TOKEN_PROD" to "{{secrets.WHATSAPP_ACCESS_TOKEN_PROD}}",
        "secrets.WHATSAPP_PHONE_ID_PROD" to "{{secrets.WHATSAPP_PHONE_ID_PROD}}",
        "secrets.FIREBASE_CREDENTIALS_PROD" to "{{secrets.FIREBASE_CREDENTIALS_PROD}}",
        "secrets.MYSQL_USERNAME_PROD" to "{{secrets.MYSQL_USERNAME_PROD}}",
        "secrets.MYSQL_PASSWORD_PROD" to "{{secrets.MYSQL_PASSWORD_PROD}}",
        "secrets.MYSQL_URL_PROD" to "{{secrets.MYSQL_URL_PROD}}",
        "secrets.POSTGRES_USERNAME_PROD" to "{{secrets.POSTGRES_USERNAME_PROD}}",
        "secrets.POSTGRES_PASSWORD_PROD" to "{{secrets.POSTGRES_PASSWORD_PROD}}",
        "secrets.POSTGRES_URL_PROD" to "{{secrets.POSTGRES_URL_PROD}}",
        "always()" to "{{always()}}",
        "job.status" to "{{job.status}}",
        "herokuApp" to context.herokuApp,
        "herokuAddons" to toAddOns(context),
        "services" to toServices(context),
    )

    private fun toAddOns(context: Context): List<Map<String, String>> {
        val addons = mutableListOf<Map<String, String>>()
        if (context.hasService(Context.SERVICE_LOGGING)) {
            addons.add(mapOf("addonName" to "papertrail"))
        }
        if (context.hasService(Context.SERVICE_CACHE)) {
            addons.add(mapOf("addonName" to "memcachier"))
        }
        if (context.hasService(Context.SERVICE_DATABASE)) {
            addons.add(mapOf("addonName" to "heroku-postgresql"))
        }
//        Never add MQUEUE.. Queues are shared
//        if (context.hasService(Context.SERVICE_MQUEUE))
//            addons.add(mapOf("addonName" to "cloudamqp"))
        return addons
    }

    private fun toServices(context: Context): Map<String, Any?> {
        val result = mutableMapOf<String, Any?>()
        if (context.hasService(Context.SERVICE_DATABASE)) {
            result["database"] = true
            result["databaseName"] = DatabaseUtil.toDatabaseName(context.apiName)
        }
        if (context.hasService(Context.SERVICE_CACHE)) {
            result["cache"] = true
        }
        if (context.hasService(Context.SERVICE_MQUEUE)) {
            result["mqueue"] = true
        }
        if (context.hasService(Context.SERVICE_AWS)) {
            result["aws"] = true
        }
        if (context.hasService(Context.SERVICE_AWS_MYSQL)) {
            result["aws_mysql"] = true
            result["databaseName"] = DatabaseUtil.toDatabaseName(context.apiName)
        }
        if (context.hasService(Context.SERVICE_AWS_POSTGRES)) {
            result["aws_postgres"] = true
            result["databaseName"] = DatabaseUtil.toDatabaseName(context.apiName)
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
}
