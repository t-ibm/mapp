package cloud.softwareag.log.appender

import org.apache.logging.log4j.LoggingException
import org.apache.logging.log4j.core.LogEvent
import org.apache.logging.log4j.core.appender.AbstractAppender
import org.apache.logging.log4j.plugins.Plugin
import org.apache.logging.log4j.plugins.PluginAttribute
import org.apache.logging.log4j.plugins.PluginFactory
import org.apache.logging.log4j.plugins.validation.constraints.Required

@Plugin("AlwaysFail")
class AlwaysFailAppender private constructor(name: String) : AbstractAppender(name, null, null, false, null) {
    override fun append(event: LogEvent) {
        throw LoggingException("Always fail")
    }

    companion object {
        @PluginFactory
        fun createAppender(@Required(message = "A name for the Appender must be specified") name: @PluginAttribute String): AlwaysFailAppender {
            return AlwaysFailAppender(name)
        }
    }
}
