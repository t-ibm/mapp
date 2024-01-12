package cloud.softwareag.log

import org.apache.logging.log4j.message.StructuredDataMessage

fun interface AuditExceptionHandler {

    /**
     * Handles Exceptions that occur while audit logging. If a RuntimeException is thrown it will percolate back to the application.
     * @param message The message being logged
     * @param t The Throwable
     */
    fun handleException(message: StructuredDataMessage, t: Throwable)
}
