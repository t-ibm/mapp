package cloud.softwareag

import cloud.softwareag.log.AuditEvent
import cloud.softwareag.log.COMPLETION_STATUS_SUCCESS
import cloud.softwareag.log.annotation.event.Session
import cloud.softwareag.log.annotation.event.Simple
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Produces
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.ThreadContext
import org.slf4j.LoggerFactory
import java.math.BigDecimal
import java.time.Instant
import java.time.format.DateTimeFormatter

@Controller("/hello")
class HelloController {
    private val slf4jLogger = LoggerFactory.getLogger(HelloController::class.java)
    private val log4jLogger = LogManager.getLogger()

    @Get
    @Produces(MediaType.TEXT_PLAIN)
    fun index(): String {
        val instant = DateTimeFormatter.ISO_INSTANT.format(Instant.now())
        slf4jLogger.debug("Hello World from SLF4J.")
        log4jLogger.debug("Hello World from Log4J.")
        ThreadContext.put("rootContextId", "00112233-4455-6677-8899-aabbccddeeff")
        ThreadContext.put("parentContextId", "")
        ThreadContext.put("contextId", "00112233-4455-6677-8899-aabbccddeeff")
        ThreadContext.put("messageId", "123e4567-e89b-12d3-a456-426614174000")
        ThreadContext.put("serverId", "MyHostname:5555")
        ThreadContext.put("auditTimestamp", instant)
        ThreadContext.put("timestamp", instant)
        val session: Session = AuditEvent.Factory<Session>().createEvent(Session::class.java)
        session.setAge(BigDecimal(0))
        session.setClientApplication("")
        session.setRpcs(0)
        session.setSessionId("851e21fe990e40dd8db67c5c8eeece63")
        session.setSessionName("healthCheck")
        session.setSessionState(1)
        session.setUserId("Administrator")
        session.logEvent()
        val simple: Simple = AuditEvent.Factory<Simple>().createEvent(Simple::class.java)
        simple.setCompletionStatus(COMPLETION_STATUS_SUCCESS)
        simple.logEvent()
        return "Hello World."
    }
}
