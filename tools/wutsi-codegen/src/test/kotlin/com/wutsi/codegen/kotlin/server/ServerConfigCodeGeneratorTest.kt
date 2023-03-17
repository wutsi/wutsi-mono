package com.wutsi.codegen.kotlin.server

import com.wutsi.codegen.Context
import com.wutsi.codegen.helpers.AbstractMustacheCodeGeneratorTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

internal class ServerConfigCodeGeneratorTest : AbstractMustacheCodeGeneratorTest() {
    override fun createContext() = Context(
        apiName = "Test",
        outputDirectory = "./target/wutsi/codegen/server",
        basePackage = "com.wutsi.test",
        jdkVersion = "1.8",
    )

    override fun getCodeGenerator(context: Context) = ServerConfigCodeGenerator()

    @Test
    fun `generate`() {
        val openAPI = createOpenAPI()
        val context = createContext()
        getCodeGenerator(context).generate(openAPI, context)

        assertContent("/kotlin/server/application.yml", "${context.outputDirectory}/src/main/resources/application.yml")
        assertContent(
            "/kotlin/server/application-test.yml",
            "${context.outputDirectory}/src/main/resources/application-test.yml",
        )
        assertContent(
            "/kotlin/server/application-prod.yml",
            "${context.outputDirectory}/src/main/resources/application-prod.yml",
        )
        assertContent(
            "/kotlin/server/logback.xml",
            "${context.outputDirectory}/src/main/resources/logback.xml",
        )
    }

    @Test
    fun `generate with database`() {
        val openAPI = createOpenAPI()
        val context = createContext()
        context.addService(Context.SERVICE_DATABASE)
        getCodeGenerator(context).generate(openAPI, context)

        assertContent(
            "/kotlin/server/database/application.yml",
            "${context.outputDirectory}/src/main/resources/application.yml",
        )
        assertContent(
            "/kotlin/server/database/application-test.yml",
            "${context.outputDirectory}/src/main/resources/application-test.yml",
        )
        assertContent(
            "/kotlin/server/database/application-prod.yml",
            "${context.outputDirectory}/src/main/resources/application-prod.yml",
        )
        assertContent(
            "/kotlin/server/database/logback.xml",
            "${context.outputDirectory}/src/main/resources/logback.xml",
        )
    }

    @Test
    fun `generate with cache configuration `() {
        val openAPI = createOpenAPI()
        val context = createContext()
        context.addService(Context.SERVICE_CACHE)
        getCodeGenerator(context).generate(openAPI, context)

        assertContent(
            "/kotlin/server/cache/application.yml",
            "${context.outputDirectory}/src/main/resources/application.yml",
        )
        assertContent(
            "/kotlin/server/cache/application-test.yml",
            "${context.outputDirectory}/src/main/resources/application-test.yml",
        )
        assertContent(
            "/kotlin/server/cache/application-prod.yml",
            "${context.outputDirectory}/src/main/resources/application-prod.yml",
        )
        assertContent(
            "/kotlin/server/cache/logback.xml",
            "${context.outputDirectory}/src/main/resources/logback.xml",
        )
    }

    @Test
    fun `generate with messaging configuration `() {
        val openAPI = createOpenAPI()
        val context = createContext()
        context.addService(Context.SERVICE_MESSAGING)
        getCodeGenerator(context).generate(openAPI, context)

        assertContent(
            "/kotlin/server/messaging/application.yml",
            "${context.outputDirectory}/src/main/resources/application.yml",
        )
        assertContent(
            "/kotlin/server/messaging/application-test.yml",
            "${context.outputDirectory}/src/main/resources/application-test.yml",
        )
        assertContent(
            "/kotlin/server/messaging/application-prod.yml",
            "${context.outputDirectory}/src/main/resources/application-prod.yml",
        )
    }

    @Test
    fun `generate with AWS Postgres`() {
        val openAPI = createOpenAPI()
        val context = createContext()
        context.addService(Context.SERVICE_AWS_POSTGRES)
        getCodeGenerator(context).generate(openAPI, context)

        assertContent(
            "/kotlin/server/aws-postgres/application.yml",
            "${context.outputDirectory}/src/main/resources/application.yml",
        )
        assertContent(
            "/kotlin/server/aws-postgres/application-test.yml",
            "${context.outputDirectory}/src/main/resources/application-test.yml",
        )
        assertContent(
            "/kotlin/server/aws-postgres/application-prod.yml",
            "${context.outputDirectory}/src/main/resources/application-prod.yml",
        )
    }

    @Test
    fun `generate with AWS MySQL`() {
        val openAPI = createOpenAPI()
        val context = createContext()
        context.addService(Context.SERVICE_AWS_MYSQL)
        getCodeGenerator(context).generate(openAPI, context)

        assertContent(
            "/kotlin/server/aws-mysql/application.yml",
            "${context.outputDirectory}/src/main/resources/application.yml",
        )
        assertContent(
            "/kotlin/server/aws-mysql/application-test.yml",
            "${context.outputDirectory}/src/main/resources/application-test.yml",
        )
        assertContent(
            "/kotlin/server/aws-mysql/application-prod.yml",
            "${context.outputDirectory}/src/main/resources/application-prod.yml",
        )
    }

    @Test
    fun `generate with mqueue configuration `() {
        val openAPI = createOpenAPI()
        val context = createContext()
        context.addService(Context.SERVICE_MQUEUE)
        getCodeGenerator(context).generate(openAPI, context)

        assertContent(
            "/kotlin/server/mqueue/application.yml",
            "${context.outputDirectory}/src/main/resources/application.yml",
        )
        assertContent(
            "/kotlin/server/mqueue/application-test.yml",
            "${context.outputDirectory}/src/main/resources/application-test.yml",
        )
        assertContent(
            "/kotlin/server/mqueue/application-prod.yml",
            "${context.outputDirectory}/src/main/resources/application-prod.yml",
        )
        assertContent(
            "/kotlin/server/mqueue/logback.xml",
            "${context.outputDirectory}/src/main/resources/logback.xml",
        )
    }

    @Test
    fun `generate with api-key configuration `() {
        val openAPI = createOpenAPI()
        val context = createContext()
        context.addService(Context.SERVICE_API_KEY)
        getCodeGenerator(context).generate(openAPI, context)

        assertContent(
            "/kotlin/server/api-key/application.yml",
            "${context.outputDirectory}/src/main/resources/application.yml",
        )
        assertContent(
            "/kotlin/server/api-key/application-test.yml",
            "${context.outputDirectory}/src/main/resources/application-test.yml",
        )
        assertContent(
            "/kotlin/server/api-key/application-prod.yml",
            "${context.outputDirectory}/src/main/resources/application-prod.yml",
        )
        assertContent(
            "/kotlin/server/api-key/logback.xml",
            "${context.outputDirectory}/src/main/resources/logback.xml",
        )
    }

    @Test
    fun `generate with security`() {
        val openAPI = createOpenAPI(true)
        val context = createContext()
        getCodeGenerator(context).generate(openAPI, context)

        assertContent(
            "/kotlin/server/security/application.yml",
            "${context.outputDirectory}/src/main/resources/application.yml",
        )
        assertContent(
            "/kotlin/server/security/application-test.yml",
            "${context.outputDirectory}/src/main/resources/application-test.yml",
        )
        assertContent(
            "/kotlin/server/security/application-prod.yml",
            "${context.outputDirectory}/src/main/resources/application-prod.yml",
        )
        assertContent(
            "/kotlin/server/security/logback.xml",
            "${context.outputDirectory}/src/main/resources/logback.xml",
        )
    }

    @Test
    fun `generate with slack`() {
        val openAPI = createOpenAPI()
        val context = createContext()
        context.addService(Context.SERVICE_SLACK)
        getCodeGenerator(context).generate(openAPI, context)

        assertContent(
            "/kotlin/server/slack/logback.xml",
            "${context.outputDirectory}/src/main/resources/logback.xml",
        )
    }

    @ParameterizedTest
    @ValueSource(strings = ["application.yml", "application-test.yml", "application-prod.yml"])
    fun `generate - do not overwrite`(name: String) {
        val openAPI = createOpenAPI()
        val context = createContext()

        val path = "${context.outputDirectory}/kotlin/server/$name"
        createFileAndWait(path)

        getCodeGenerator(context).generate(openAPI, context)

        assertFileNotOverwritten(path)
    }
}
