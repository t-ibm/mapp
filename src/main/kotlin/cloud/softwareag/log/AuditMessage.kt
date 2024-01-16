package cloud.softwareag.log

import org.apache.logging.log4j.message.StructuredDataId
import org.apache.logging.log4j.message.StructuredDataMessage

class AuditMessage(eventName: String, maxLength: Int) : StructuredDataMessage(Id(eventName, maxLength), null, AuditLogger.MARKER, maxLength) {
    private val extraContent: MutableMap<String, StructuredDataMessage> = HashMap()

    fun addContent(name: String, message: StructuredDataMessage) {
        extraContent[name] = message
    }

    private class Id(eventName: String, maxLength: Int) : StructuredDataId(eventName, null, null, maxLength)
}
