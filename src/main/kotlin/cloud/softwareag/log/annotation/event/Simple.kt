package cloud.softwareag.log.annotation.event

import cloud.softwareag.log.AuditEvent
import cloud.softwareag.log.annotation.EventName

@EventName("Simple")
interface Simple : AuditEvent
