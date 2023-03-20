package com.wutsi.codegen.kotlin.server

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.codegen.Context
import com.wutsi.codegen.core.generator.CodeGenerator
import com.wutsi.codegen.kotlin.KotlinMapper
import io.swagger.v3.oas.models.OpenAPI
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class ServerCodeGeneratorTest {
    val context = mock<Context>()

    @Test
    fun `run all generators`() {
        val gen1 = mock<CodeGenerator>()
        val gen2 = mock<CodeGenerator>()
        val gen3 = mock<CodeGenerator>()
        val codegen = ServerCodeGenerator(
            mapper = KotlinMapper(context),
            generators = listOf(gen1, gen2, gen3),
        )

        val openAPI = OpenAPI()
        codegen.generate(openAPI, context)

        verify(gen1).generate(openAPI, context)
        verify(gen2).generate(openAPI, context)
        verify(gen3).generate(openAPI, context)
    }

    @Test
    fun `generators`() {
        val codegen = ServerCodeGenerator(mapper = KotlinMapper(context))

        assertEquals(10, codegen.generators.size)
        assertTrue(codegen.generators[0] is ServerDtoCodeGenerator)
        assertTrue(codegen.generators[1] is ServerDelegateCodeGenerator)
        assertTrue(codegen.generators[2] is ServerControllerCodeGenerator)
        assertTrue(codegen.generators[3] is ServerMavenCodeGenerator)
        assertTrue(codegen.generators[4] is ServerLauncherCodeGenerator)
        assertTrue(codegen.generators[5] is ServerConfigCodeGenerator)
//        assertTrue(codegen.generators[6] is ServerHerokuCodeGenerator)
//        assertTrue(codegen.generators[7] is EditorConfigCodeGenerator)
        assertTrue(codegen.generators[6] is ServerGithubActionsCodeGenerator)
//        assertTrue(codegen.generators[9] is GitCodeGenerator)
        assertTrue(codegen.generators[7] is SwaggerCodeGenerator)
        assertTrue(codegen.generators[8] is ServerReadmeCodeGenerator)
        assertTrue(codegen.generators[9] is DatabaseCodeGenerator)
//        assertTrue(codegen.generators[13] is RenovateCodeGenerator)
    }
}
