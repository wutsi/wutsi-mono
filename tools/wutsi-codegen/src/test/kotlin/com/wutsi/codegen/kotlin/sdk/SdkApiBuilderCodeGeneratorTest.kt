package com.wutsi.codegen.kotlin.sdk

import com.wutsi.codegen.Context
import com.wutsi.codegen.kotlin.KotlinMapper
import io.swagger.v3.oas.models.OpenAPI
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertTrue

internal class SdkApiBuilderCodeGeneratorTest {
    val context = Context(
        apiName = "Test",
        outputDirectory = "./target/wutsi/codegen/sdk",
        basePackage = "com.wutsi.test",
    )

    val codegen = SdkApiBuilderCodeGenerator(KotlinMapper(context))

    @Test
    fun `generate`() {
        codegen.generate(
            openAPI = OpenAPI(),
            context = context,
        )

        val file = File("${context.outputDirectory}/src/main/kotlin/com/wutsi/test/TestApiBuilder.kt")
        assertTrue(file.exists())

        val text = file.readText()
        kotlin.test.assertEquals(
            """
                package com.wutsi.test

                import com.fasterxml.jackson.databind.ObjectMapper
                import feign.RequestInterceptor
                import feign.codec.ErrorDecoder
                import kotlin.Boolean
                import kotlin.Int
                import kotlin.Long
                import kotlin.collections.List

                public class TestApiBuilder {
                  public fun build(
                    env: Environment,
                    mapper: ObjectMapper,
                    interceptors: List<RequestInterceptor> = emptyList(),
                    errorDecoder: ErrorDecoder = ErrorDecoder.Default(),
                    retryPeriodMillis: Long = 100L,
                    retryMaxPeriodSeconds: Long = 3,
                    retryMaxAttempts: Int = 5,
                    connectTimeoutMillis: Long = 15000,
                    readTimeoutMillis: Long = 15000,
                    followRedirects: Boolean = true,
                  ) = feign.Feign.builder()
                    .client(feign.okhttp.OkHttpClient())
                    .encoder(feign.jackson.JacksonEncoder(mapper))
                    .decoder(feign.jackson.JacksonDecoder(mapper))
                    .logger(feign.slf4j.Slf4jLogger(TestApi::class.java))
                    .logLevel(feign.Logger.Level.BASIC)
                    .requestInterceptors(interceptors)
                    .errorDecoder(errorDecoder)
                    .retryer(feign.Retryer.Default(retryPeriodMillis,
                      java.util.concurrent.TimeUnit.SECONDS.toMillis(retryMaxPeriodSeconds), retryMaxAttempts))
                    .options(feign.Request.Options(connectTimeoutMillis, java.util.concurrent.TimeUnit.MILLISECONDS,
                      readTimeoutMillis, java.util.concurrent.TimeUnit.MILLISECONDS, followRedirects))
                    .target(TestApi::class.java, env.url)
                }
            """.trimIndent(),
            text.trimIndent(),
        )
    }
}
