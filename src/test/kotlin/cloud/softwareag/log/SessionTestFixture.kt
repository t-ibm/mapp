package cloud.softwareag.log

import cloud.softwareag.log.AuditEventTestFixture.ENTERPRISE_ID
import cloud.softwareag.log.annotation.AuditContext
import cloud.softwareag.log.annotation.event.Session
import java.time.Instant.now
import java.time.format.DateTimeFormatter

object SessionTestFixture {
    private const val CONTEXT_ID = "00112233-4455-6677-8899-aabbccddeeff"
    private const val MESSAGE_ID = "123e4567-e89b-12d3-a456-426614174000"
    private const val SERVER_ID = "MyHostname:5555"
    private val timestamp = DateTimeFormatter.ISO_INSTANT.format(now())

    private val age = 0.toBigDecimal()
    private const val RPCS = 0
    private const val SESSION_ID = "851e21fe990e40dd8db67c5c8eeece63"
    private const val SESSION_NAME = "healthCheck"
    private const val SESSION_STATE = 1
    private const val USER_ID = "Administrator"

    val expectedMessage = """
            [${Session::class.simpleName}@$ENTERPRISE_ID
            age="$age"
            rpcs="$RPCS"
            sessionId="$SESSION_ID"
            sessionName="$SESSION_NAME"
            sessionState="$SESSION_STATE"
            userId="$USER_ID"][${AuditContext::class.simpleName}@$ENTERPRISE_ID
            auditTimestamp="$timestamp"
            contextId="$CONTEXT_ID"
            messageId="$MESSAGE_ID"
            rootContextId="$CONTEXT_ID"
            serverId="$SERVER_ID"
            timestamp="$timestamp"]
        """.trimIndent().lines().joinToString(" ")

    val contextAttributesRequired = mapOf(
        "rootContextId" to  CONTEXT_ID,
        "contextId" to CONTEXT_ID,
        "messageId" to MESSAGE_ID,
        "serverId" to SERVER_ID,
        "auditTimestamp" to timestamp,
        "timestamp" to timestamp,
    )

    val eventAttributesRequired = mapOf(
        "age" to age.toString(),
        "rpcs" to RPCS.toString(),
        "sessionId" to SESSION_ID,
        "sessionName" to SESSION_NAME,
        "sessionState" to SESSION_STATE.toString(),
        "userId" to USER_ID,
    )

    val auditEventFactory = AuditEvent.Factory<Session>()

    fun setupEventRequired(): Session {
        val session: Session = auditEventFactory.createEvent(Session::class.java)
        session.setAge(age)
        session.setRpcs(RPCS)
        session.setSessionId(SESSION_ID)
        session.setSessionName(SESSION_NAME)
        session.setSessionState(SESSION_STATE)
        session.setUserId(USER_ID)
        return session
    }

    fun setupEventIncomplete(): Session {
        val session: Session = auditEventFactory.createEvent(Session::class.java)
        session.setAge(age)
        session.setClientApplication("")
        session.setRpcs(RPCS)
        session.setSessionName(SESSION_NAME)
        session.setSessionState(SESSION_STATE)
        session.setUserId(USER_ID)
        return session
    }
}
