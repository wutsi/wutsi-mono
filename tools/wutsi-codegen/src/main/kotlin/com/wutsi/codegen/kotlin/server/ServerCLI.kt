package com.wutsi.codegen.kotlin.server

import com.wutsi.codegen.Context
import com.wutsi.codegen.core.generator.AbstractCodeGeneratorCLI
import com.wutsi.codegen.core.generator.CodeGeneratorFactory
import com.wutsi.codegen.core.openapi.DefaultOpenAPILoader
import com.wutsi.codegen.core.openapi.OpenAPILoader
import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.Option
import org.apache.commons.cli.Options
import java.net.URL

class ServerCLI(
    codeGeneratorFactory: CodeGeneratorFactory = ServerCodeGeneratorFactory(),
    openAPILoader: OpenAPILoader = DefaultOpenAPILoader(),
) : AbstractCodeGeneratorCLI(codeGeneratorFactory, openAPILoader) {
    companion object {
        const val OPTION_HEROKU_APP = "heroku"
    }

    override fun name() = "server"

    override fun description() = "Generate the API Springboot/Kotlin Server code from an OpenAPIV3 specification"

    override fun addOptions(options: Options) {
        super.addOptions(options)

        options.addOption(
            Option.builder(OPTION_HEROKU_APP)
                .hasArg()
                .argName("heroku-app")
                .desc(
                    "Heroku application name. This will trigger the deployment when merging to `master` branch.\n" +
                        "                                IMPORTANT: The github secret HEROKU_API_KEY must be configured.",
                )
                .build(),
        )
        options.addOption(
            Option.builder(OPTION_SERVICE_CACHE)
                .hasArg(false)
                .desc("Attach a cache to the API")
                .build(),
        )
        options.addOption(
            Option.builder(OPTION_SERVICE_DATABASE)
                .hasArg(false)
                .desc("Attach a database to the API")
                .build(),
        )
        options.addOption(
            Option.builder(OPTION_SERVICE_LOGGER)
                .hasArg(false)
                .desc("Attach a logger to the API")
                .build(),
        )
        options.addOption(
            Option.builder(OPTION_SERVICE_MQUEUE)
                .hasArg(false)
                .desc("Attach a queue to the API")
                .build(),
        )
        options.addOption(
            Option.builder(OPTION_SERVICE_API_KEY)
                .hasArg(false)
                .desc("Attach the API key")
                .build(),
        )
        options.addOption(
            Option.builder(OPTION_SERVICE_SLACK)
                .hasArg(false)
                .desc("Use Slack for notification")
                .build(),
        )
        options.addOption(
            Option.builder(OPTION_SERVICE_AWS)
                .hasArg(false)
                .desc("Attach the AWS configuration")
                .build(),
        )
        options.addOption(
            Option.builder(OPTION_SERVICE_AWS_MYSQL)
                .hasArg(false)
                .desc("Attach the AWS Mysql database")
                .build(),
        )
        options.addOption(
            Option.builder(OPTION_SERVICE_AWS_POSTGRES)
                .hasArg(false)
                .desc("Attach the AWS Postgres database")
                .build(),
        )
        options.addOption(
            Option.builder(OPTION_SERVICE_MESSAGING)
                .hasArg(false)
                .desc("Attach the messaging service (email, SMS, WhatsApp)")
                .build(),
        )
        options.addOption(
            Option.builder(OPTION_SERVICE_SWAGGER)
                .hasArg(false)
                .desc("Generate swagger API?")
                .build(),
        )
    }

    override fun createContext(cmd: CommandLine): Context {
        val context = Context(
            apiName = cmd.getOptionValue(OPTION_API_NAME).trim(),
            basePackage = cmd.getOptionValue(OPTION_BASE_PACKAGE).trim(),
            outputDirectory = cmd.getOptionValue(OPTION_OUTPUT_DIR).trim(),
            jdkVersion = cmd.getOptionValue(OPTION_JDK_VERSION)?.trimIndent() ?: DEFAULT_JDK_VERSION,
            githubUser = cmd.getOptionValue(OPTION_GITHUB_USER)?.trim(),
            githubProject = cmd.getOptionValue(OPTION_GITHUB_PROJECT)?.trim(),
            herokuApp = cmd.getOptionValue(OPTION_HEROKU_APP)?.trim(),
            inputUrl = URL(cmd.getOptionValue(OPTION_INPUT_FILE).trim()),
        )
        if (cmd.hasOption(OPTION_SERVICE_CACHE)) {
            context.addService(Context.SERVICE_CACHE)
        }
        if (cmd.hasOption(OPTION_SERVICE_DATABASE)) {
            context.addService(Context.SERVICE_DATABASE)
        }
        if (cmd.hasOption(OPTION_SERVICE_MQUEUE)) {
            context.addService(Context.SERVICE_MQUEUE)
        }
        if (cmd.hasOption(OPTION_SERVICE_LOGGER)) {
            context.addService(Context.SERVICE_LOGGING)
        }
        if (cmd.hasOption(OPTION_SERVICE_AWS)) {
            context.addService(Context.SERVICE_AWS)
        }
        if (cmd.hasOption(OPTION_SERVICE_AWS_MYSQL)) {
            context.addService(Context.SERVICE_AWS_MYSQL)
        }
        if (cmd.hasOption(OPTION_SERVICE_AWS_POSTGRES)) {
            context.addService(Context.SERVICE_AWS_POSTGRES)
        }
        if (cmd.hasOption(OPTION_SERVICE_API_KEY)) {
            context.addService(Context.SERVICE_API_KEY)
        }
        if (cmd.hasOption(OPTION_SERVICE_SLACK)) {
            context.addService(Context.SERVICE_SLACK)
        }
        if (cmd.hasOption(OPTION_SERVICE_MESSAGING)) {
            context.addService(Context.SERVICE_MESSAGING)
        }
        if (cmd.hasOption(OPTION_SERVICE_SWAGGER)) {
            context.addService(Context.SERVICE_SWAGGER)
        }

        return context
    }
}
