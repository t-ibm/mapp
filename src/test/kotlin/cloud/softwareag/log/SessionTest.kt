package cloud.softwareag.log

import cloud.softwareag.log.SessionTestFixture.auditEventFactory
import cloud.softwareag.log.SessionTestFixture.contextAttributesRequired
import cloud.softwareag.log.SessionTestFixture.eventAttributesRequired
import cloud.softwareag.log.SessionTestFixture.setupEventIncomplete
import cloud.softwareag.log.SessionTestFixture.setupEventRequired
import cloud.softwareag.log.annotation.event.Session
import cloud.softwareag.log.appender.AlwaysFailAppender
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.LoggingException
import org.apache.logging.log4j.ThreadContext
import org.apache.logging.log4j.core.Appender
import org.apache.logging.log4j.core.Logger
import org.apache.logging.log4j.core.config.AbstractConfiguration
import org.apache.logging.log4j.plugins.validation.ConstraintValidationException
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test


class SessionTest : AuditEventTestBase() {
    private val failingAppenderName = "failingAppenderName"

    @AfterEach
    fun cleanup() {
        auditEventFactory.resetExceptionHandler()
    }

    @Test
    fun logEventFromAuditFactory() {
        ThreadContext.putAll(contextAttributesRequired)
        auditEventFactory.logEvent(Session::class.java, eventAttributesRequired)
        assertNotNull(app.messages)
        assertEquals(1, app.messages.size)
        verifyEventRequired(app.messages[0])
    }

    @Test
    fun logEventFromAuditFactoryWithMissingContextAttribute() {
        contextAttributesRequired.entries.toList().take(2).forEach {
            ThreadContext.put(it.key, it.value)
        }
        assertThrows(ConstraintValidationException::class.java) {
            auditEventFactory.logEvent(Session::class.java, eventAttributesRequired)
        }
    }

    @Test
    fun logEventFromAuditFactoryWithMissingEventAttribute() {
        ThreadContext.putAll(contextAttributesRequired)
        val eventAttributes: MutableMap<String,String> = mutableMapOf()
        eventAttributesRequired.entries.toList().take(2).forEach {
            eventAttributes[it.key] = it.value
        }
        assertThrows(ConstraintValidationException::class.java) {
            auditEventFactory.logEvent(Session::class.java, eventAttributes)
        }
    }

    @Test
    fun logEventFromAuditClass() {
        ThreadContext.putAll(contextAttributesRequired)
        val session: Session = setupEventRequired()
        session.logEvent()
        session.setCompletionStatus(COMPLETION_STATUS_SUCCESS)
        session.logEvent()
        assertNotNull(app.messages)
        assertEquals(2, app.messages.size)
        verifyEventRequired(app.messages[0])
    }

    @Test
    fun logEventFromAuditClassForMissingContextAttribute() {
        var exceptionHandled = false
        auditEventFactory.setExceptionHandler { _, ex ->
            assertInstanceOf(ConstraintValidationException::class.java, ex)
            exceptionHandled = true
        }
        contextAttributesRequired.entries.toList().take(2).forEach {
            ThreadContext.put(it.key, it.value)
        }
        val session: Session = setupEventRequired()
        session.logEvent()
        assertTrue(exceptionHandled)
    }

    @Test
    fun logEventFromAuditClassForMissingEventAttribute() {
        var exceptionHandled = false
        auditEventFactory.setExceptionHandler { _, ex ->
            assertInstanceOf(ConstraintValidationException::class.java, ex)
            exceptionHandled = true
        }
        ThreadContext.putAll(contextAttributesRequired)
        val session = setupEventIncomplete()
        session.logEvent()
        assertTrue(exceptionHandled)
    }

    @Test
    fun logEventFromAuditClassForInvalidEventAttribute() {
        var exceptionHandled = false
        auditEventFactory.setExceptionHandler { _, _ ->
            exceptionHandled = true
        }
        val session: Session = setupEventRequired()
        session.setSessionState(-1)
        assertTrue(exceptionHandled)
    }

    @Test
    fun logEventFromAuditClassAndInvokeDefaultExceptionHandler() {
        val config = setUpFailingAppender()
        ThreadContext.putAll(contextAttributesRequired)
        val session: Session = setupEventRequired()
        try {
            val exception = assertThrows(LoggingException::class.java) {
                session.logEvent()
            }
            assertInstanceOf(LoggingException::class.java, exception.cause)
            assertEquals("Error logging event Session", exception.message)
        } finally {
            config.removeAppender(failingAppenderName)
        }
    }

    @Test
    fun logEventFromAuditClassAndInvokeCustomExceptionHandler() {
        val config = setUpFailingAppender()
        var exceptionHandled = false
        auditEventFactory.setExceptionHandler { _, ex ->
            assertInstanceOf(LoggingException::class.java, ex)
            exceptionHandled = true
        }
        ThreadContext.putAll(contextAttributesRequired)
        val session: Session = setupEventRequired()
        session.logEvent()
        assertTrue(exceptionHandled)
        config.removeAppender(failingAppenderName)
    }

    private fun setUpFailingAppender(): AbstractConfiguration {
        val auditLogger = LogManager.getContext(false).getLogger(AuditLogger.NAME) as Logger
        val config = ctx.configuration as AbstractConfiguration
        val appender: Appender = AlwaysFailAppender.createAppender(failingAppenderName)
        appender.start()
        config.addLoggerAppender(auditLogger, appender)
        return config
    }

    private fun verifyEventRequired(message: String) {
        assertEquals(SessionTestFixture.expectedMessage, message.substringAfter("${AuditLogger.MARKER} "))
    }
}
