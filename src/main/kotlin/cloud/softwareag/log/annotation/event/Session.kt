package cloud.softwareag.log.annotation.event

import cloud.softwareag.log.AuditEvent
import cloud.softwareag.log.annotation.AuditContext
import cloud.softwareag.log.annotation.MaxLength
import cloud.softwareag.log.annotation.constraint.IntConstraint
import cloud.softwareag.log.annotation.constraint.StringConstraint
import org.apache.logging.log4j.plugins.validation.constraints.Required
import java.math.BigDecimal

@MaxLength(32)
@AuditContext("rootContextId", true)
@AuditContext("parentContextId")
@AuditContext("contextId", true)
@AuditContext("messageId", true)
@AuditContext("serverId", true)
@AuditContext("auditTimestamp", true)
@AuditContext("timestamp", true)
interface Session : AuditEvent {

    @Required
    fun setAge(age: BigDecimal)

    @Required
    fun setRpcs(rpcs: Int)

    @StringConstraint
    fun setClientApplication(clientApplication: String)

    @Required @StringConstraint(0, 64)
    fun setSessionId(sessionId: String)

    @Required @StringConstraint
    fun setSessionName(sessionName: String)

    @Required @IntConstraint(1)
    fun setSessionState(sessionState: Int)

    @Required @StringConstraint(0, 64)
    fun setUserId(userId: String)
}
