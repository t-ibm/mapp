package cloud.softwareag.log

import cloud.softwareag.log.AuditEventTestFixture.ENTERPRISE_ID
import cloud.softwareag.log.AuditLogger.Companion.MARKER
import cloud.softwareag.log.annotation.event.Simple
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import java.util.function.Consumer

class SimpleTest: AuditEventTestBase() {
    private val auditEventFactory = AuditEvent.Factory<Simple>()

    @Test
    fun logEventFromAuditFactory() {
        auditEventFactory.logEvent(Simple::class.java, emptyMap())
        assertNotNull(app.messages)
        assertEquals(1, app.messages.size)
    }

    @Test
    fun logEventFromAuditClass() {
        val event: Simple = auditEventFactory.createEvent(Simple::class.java)
        event.logEvent()
        assertEquals("[${Simple::class.simpleName} ]", event.toString())
        event.setCompletionStatus(COMPLETION_STATUS_SUCCESS)
        assertEquals("[${Simple::class.simpleName} $EVENT_ATTRIBUTE_COMPLETION_STATUS=\"$COMPLETION_STATUS_SUCCESS\"]", event.toString())
        event.logEvent()
        val exceptionHandler = AuditExceptionHandler { _, _ -> }
        event.setExceptionHandler(exceptionHandler)
        event.logEvent()
        assertEquals(0, app.events.size)
        assertNotNull(app.messages)
        assertEquals(3, app.messages.size)
        app.messages.forEach(Consumer { x: String -> slf4jLogger.debug("Message content: $x") })
        assertEquals("[${Simple::class.simpleName}@$ENTERPRISE_ID]", app.messages[0].substringAfter("$MARKER "))
        assertEquals("[${Simple::class.simpleName}@$ENTERPRISE_ID $EVENT_ATTRIBUTE_COMPLETION_STATUS=\"$COMPLETION_STATUS_SUCCESS\"]", app.messages[1].substringAfter("$MARKER "))
        assertEquals(app.messages[1].substringAfter("$MARKER "), app.messages[2].substringAfter("$MARKER "))
    }
}
