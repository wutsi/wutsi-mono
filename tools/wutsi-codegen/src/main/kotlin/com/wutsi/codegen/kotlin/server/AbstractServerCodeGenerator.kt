package com.wutsi.codegen.kotlin.server

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName
import com.wutsi.codegen.Context
import com.wutsi.codegen.kotlin.AbstractKotlinCodeGenerator
import com.wutsi.codegen.kotlin.KotlinMapper
import com.wutsi.codegen.model.Endpoint
import com.wutsi.codegen.model.EndpointParameter
import com.wutsi.codegen.model.Request
import io.swagger.v3.oas.models.OpenAPI
import java.io.File

abstract class AbstractServerCodeGenerator(protected val mapper: KotlinMapper) : AbstractKotlinCodeGenerator() {
    companion object {
        const val REQUEST_VARIABLE = "request"
        const val INVOKE_FUNCTION = "invoke"
    }

    abstract fun className(endpoint: Endpoint): String

    abstract fun packageName(endpoint: Endpoint, context: Context): String

    protected abstract fun classAnnotations(endpoint: Endpoint): List<AnnotationSpec>

    protected abstract fun functionAnnotations(endpoint: Endpoint): List<AnnotationSpec>

    protected abstract fun requestBodyAnnotations(requestBody: Request?): List<AnnotationSpec>

    protected abstract fun parameterAnnotations(parameter: EndpointParameter, getter: Boolean): List<AnnotationSpec>

    protected abstract fun constructorSpec(endpoint: Endpoint, context: Context): FunSpec

    protected abstract fun funCodeBloc(endpoint: Endpoint): CodeBlock

    protected abstract fun canGenerate(directory: File, packageName: String, className: String): Boolean

    override fun generate(openAPI: OpenAPI, context: Context) {
        val api = mapper.toAPI(openAPI)
        api.endpoints.forEach {
            generateClass(it, context)
            generateTest(it, context)
        }
    }

    fun generateClass(endpoint: Endpoint, context: Context): Boolean {
        val file = getSourceDirectory(context)
        val packageName = packageName(endpoint, context)
        val classname = className(endpoint)
        if (!canGenerate(file, packageName, classname)) {
            return false
        }

        System.out.println("Generating $packageName.$classname to $file")
        FileSpec.builder(packageName, classname)
            .addType(toTypeSpec(endpoint, context))
            .build()
            .writeTo(file)
        return true
    }

    open fun generateTest(endpoint: Endpoint, context: Context) {
    }

    fun toTypeSpec(endpoint: Endpoint, context: Context): TypeSpec {
        val constructor = constructorSpec(endpoint, context)

        val spec = TypeSpec.classBuilder(className(endpoint))
            .primaryConstructor(constructor)
            .addAnnotations(classAnnotations(endpoint))
            .addFunction(toFunSpec(endpoint))

        if (constructor.parameters.isNotEmpty()) {
            spec.addProperties(
                constructor.parameters.map {
                    PropertySpec.builder(it.name, it.type.copy(), it.modifiers)
                        .initializer(it.name)
                        .build()
                },
            )
        }

        return spec.build()
    }

    fun toFunSpec(endpoint: Endpoint): FunSpec {
        val builder = FunSpec.builder(INVOKE_FUNCTION)
            .addAnnotations(functionAnnotations(endpoint))
            .addParameters(endpoint.parameters.map { toParameterSpec(it, false) })
            .addCode(funCodeBloc(endpoint))

        if (endpoint.request != null) {
            val type = endpoint.request.type
            builder.addParameter(
                ParameterSpec
                    .builder(REQUEST_VARIABLE, ClassName(type.packageName, type.name))
                    .addAnnotations(requestBodyAnnotations(endpoint.request))
                    .build(),
            )
        }

        if (endpoint.response != null) {
            builder.returns(ClassName(endpoint.response.packageName, endpoint.response.name))
        }

        return builder.build()
    }

    fun toParameterSpec(parameter: EndpointParameter, getter: Boolean): ParameterSpec {
        val builder = ParameterSpec.builder(parameter.field.name, parameter.field.type.asTypeName().copy(parameter.field.nullable))
            .addAnnotations(parameterAnnotations(parameter, getter))

        val default = defaultValue(parameter.field)
        if (default != null) {
            builder.defaultValue(default)
        }

        return builder.build()
    }

    protected fun toPackage(basePackage: String, suffix: String): String =
        if (basePackage.isNullOrEmpty()) suffix else "$basePackage.$suffix"
}
