package com.wutsi.codegen.kotlin.sdk

import com.wutsi.codegen.core.generator.AbstractCodeGeneratorCLI
import com.wutsi.codegen.core.generator.CodeGeneratorFactory
import com.wutsi.codegen.core.openapi.DefaultOpenAPILoader
import com.wutsi.codegen.core.openapi.OpenAPILoader

class SdkCLI(
    codeGeneratorFactory: CodeGeneratorFactory = SdkCodeGeneratorFactory(),
    openAPILoader: OpenAPILoader = DefaultOpenAPILoader(),
) : AbstractCodeGeneratorCLI(codeGeneratorFactory, openAPILoader) {
    override fun name() = "sdk"

    override fun description() = "Generate the API Kotlin SDK from an OpenAPIV3 specification"
}
