package cloud.softwareag.log

import cloud.softwareag.log.appender.ListAppender
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.ThreadContext
import org.apache.logging.log4j.core.LoggerContext
import org.apache.logging.log4j.core.config.Configuration
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.slf4j.LoggerFactory

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
open class AuditEventTestBase {
    internal val slf4jLogger = LoggerFactory.getLogger(AuditEventTestBase::class.java)
    internal lateinit var ctx: LoggerContext
    internal lateinit var app: ListAppender

    @BeforeAll
    fun setupClass() {
        ctx = LogManager.getContext(false) as LoggerContext
        val config: Configuration = ctx.configuration
        for ((key, value) in config.appenders.entries) {
            slf4jLogger.debug("Found plugin named '{}' of type '{}'.", key, value.javaClass.name)
            if (key == ListAppender::class.qualifiedName) {
                app = value as ListAppender
                break
            }
        }
        Assertions.assertNotNull(app)
    }

    @BeforeEach
    fun setup() {
        app.clear()
        ThreadContext.clearMap()
    }
}
