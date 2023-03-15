package com.wutsi.platform.core.util

import com.vladmihalcea.hibernate.naming.CamelCaseToSnakeCaseNamingStrategy
import org.hibernate.boot.model.naming.Identifier
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment

class CustomPhysicalNamingStrategy : CamelCaseToSnakeCaseNamingStrategy() {
    override fun toPhysicalTableName(name: Identifier, context: JdbcEnvironment): Identifier {
        return Identifier.toIdentifier(name.text, name.isQuoted())
    }
}
