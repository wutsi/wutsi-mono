package com.wutsi.regulation

class RuleSet(
    private val rules: List<Rule>,
) : Rule {
    companion object {
        val NONE = RuleSet(emptyList())
    }

    override fun check() {
        rules.forEach {
            it.check()
        }
    }
}
