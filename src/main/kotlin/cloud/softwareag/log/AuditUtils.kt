package cloud.softwareag.log

import java.lang.reflect.Method
import java.util.Locale

const val EVENT_ATTRIBUTE_COMPLETION_STATUS = "completionStatus"
const val COMPLETION_STATUS_SUCCESS = "Success"

fun Method.toEventAttribute(): String {
    val name = this.name.replaceFirst("(get|set|is|has)".toRegex(), replacement = "")
    return name[0].toString().lowercase(Locale.getDefault()) + name.substring(1)
}
