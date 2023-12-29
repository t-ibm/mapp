package cloud.softwareag.log

import org.apache.logging.log4j.EventLogger
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.MarkerManager
import org.apache.logging.log4j.message.StructuredDataMessage

class AuditLoggerDefault : AuditLogger {
    private val marker = MarkerManager.getMarker(AuditLogger.MARKER).addParents(EventLogger.EVENT_MARKER)
    private val logger = LogManager.getContext(false).getLogger(AuditLogger.NAME)

    override fun logEvent(message: StructuredDataMessage) {
        logger.logIfEnabled(AuditLoggerDefault::class.java.name, Level.OFF, marker, message, null)
    }
}
