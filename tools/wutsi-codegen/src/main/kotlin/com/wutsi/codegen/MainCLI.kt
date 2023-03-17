package com.wutsi.codegen

import com.wutsi.codegen.core.cli.AbstractCLI
import com.wutsi.codegen.core.generator.AbstractCodeGeneratorCLI
import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.Options

class MainCLI(private val commands: List<AbstractCodeGeneratorCLI>) : AbstractCLI() {
    override fun addOptions(options: Options) {
    }

    override fun getCommondLineSyntax(): String =
        "java wutsi-codegen-<version>.jar <command>"

    override fun run(args: Array<String>, cmd: CommandLine) {
    }

    override fun run(args: Array<String>) {
        if (args.isNotEmpty()) {
            val cli = commands.find { it.name() == args[0] }

            if (cli != null) {
                cli.run(args)
                return
            }
        }

        printHelp(
            footer = "COMMANDS\n" +
                commands.map { "  " + it.name() + " - " + it.description() }.joinToString("\n"),
        )
    }
}
