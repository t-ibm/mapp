package cloud.softwareag.log

import org.apache.logging.log4j.message.StructuredDataMessage

interface AuditLogger {

    companion object {
        val NAME: String = AuditLogger::class.java.name
        const val MARKER = "Audit"
    }

    /**
     * Log the event.
     * @param message The audit message
     */
    fun logEvent(message: StructuredDataMessage)
}
