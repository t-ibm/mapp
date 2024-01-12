package cloud.softwareag.log

import cloud.softwareag.log.annotation.AuditContext
import cloud.softwareag.log.annotation.AuditContextConstraints
import cloud.softwareag.log.annotation.EventName
import cloud.softwareag.log.annotation.MaxLength
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.LoggingException
import org.apache.logging.log4j.ThreadContext
import org.apache.logging.log4j.message.StructuredDataMessage
import org.apache.logging.log4j.plugins.util.AnnotationUtil
import org.apache.logging.log4j.plugins.validation.Constraint
import org.apache.logging.log4j.plugins.validation.ConstraintValidationException
import org.apache.logging.log4j.plugins.validation.ConstraintValidator
import org.apache.logging.log4j.plugins.validation.constraints.Required
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import kotlin.reflect.full.createInstance

interface AuditEvent {
    /**
     * Log the event.
     */
    fun logEvent()

    /**
     * Set the exception handler to use. The default exception handler will throw a `LoggingException` wrapping the
     * exception that occurred. If null is passed in then the exception will be ignored.
     * @param exceptionHandler The exception handler
     */
    fun setExceptionHandler(exceptionHandler: AuditExceptionHandler)

    /**
     * Added to the event after the operation has completed. Identifies whether it was successful or not.
     * @param status The completion status
     */
    fun setCompletionStatus(status: String)

    class Factory<T : AuditEvent> {
        private val methodNameLogEvent = "logEvent"
        private val methodNameSetExceptionHandler = "setExceptionHandler"
        private val methodNameSetCompletionStatus = "setCompletionStatus"
        private val maxLengthDefault = 32
        private val logger = LogManager.getLogger(Factory::class.java)
        private val auditLogger = AuditLoggerDefault()

        private val eventTypes: ConcurrentMap<Class<T>, List<Property>> = ConcurrentHashMap()

        private val exceptionHandlerNoop = AuditExceptionHandler { _, _ -> }
        private val exceptionHandlerDefault = AuditExceptionHandler { message, ex ->
            throw LoggingException("Error logging event " + message.id?.name, ex)
        }
        private var exceptionHandler = exceptionHandlerDefault
        private lateinit var message: AuditMessage

        fun setExceptionHandler(exceptionHandler: AuditExceptionHandler?) {
            this.exceptionHandler = exceptionHandler ?: exceptionHandlerNoop
        }

        fun resetExceptionHandler() {
            this.exceptionHandler = exceptionHandlerDefault
        }

        @Suppress("UNCHECKED_CAST")
        fun createEvent(eventType: Class<T>): T {
            message = buildAuditMessage(eventType)
            return Proxy.newProxyInstance(
                eventType.classLoader,
                arrayOf(eventType)
            ) { _: Any, method: Method, args: Array<Any>? ->
                when {
                    method.name == "toString" && method.parameterCount == 0 -> {
                        message.toString()
                    }
                    method.name == methodNameLogEvent -> {
                        runMessageAction { validateEvent(eventType) }
                        logEvent()
                    }
                    method.name == methodNameSetCompletionStatus -> {
                        require(args != null) { "Missing completion status" }
                        val name: String = method.toEventAttribute()
                        message.put(name, args[0].toString())
                    }
                    method.name == methodNameSetExceptionHandler -> {
                        exceptionHandler = if (args == null) {
                            exceptionHandlerNoop
                        } else if (args[0] is AuditExceptionHandler) {
                            args[0] as AuditExceptionHandler
                        } else {
                            throw IllegalArgumentException(
                                args[0].toString() + " is not an " + AuditExceptionHandler::class.java.name
                            )
                        }
                    }
                    method.name.startsWith("set") -> {
                        runMessageAction { setProperty(method, args) }
                    }
                    else -> {}
                }
            } as T
        }

        fun logEvent(eventType: Class<T>, properties: Map<String, String>) {
            message = buildAuditMessage(eventType)
            for ((key, value) in properties) {
                message.put(key, value)
            }
            validateEvent(eventType)
            logEvent()
        }

        private fun logEvent() {
            runMessageAction { auditLogger.logEvent(message) }
        }

        private fun runMessageAction(action: Runnable) {
            try {
                action.run()
            } catch (ex: Throwable) {
                exceptionHandler.handleException(message, ex)
            }
        }

        private fun validateEvent(eventType: Class<T>) {
            validateContextConstraints(eventType)
            val props: List<Property> = getProperties(eventType)
            val propertyMap: MutableMap<String, Property> = HashMap()
            for (property in props) {
                propertyMap[property.name] = property
                if (property.isRequired && !message.containsKey(property.name)) {
                    logger.error("Validation failed for '${property.name}' (source: $eventType) and value '${Required::class.java.simpleName}'.")
                    throw ConstraintValidationException(eventType, property.name, Required::class.java.simpleName)
                }
            }
        }

        private fun validateConstraint(method: Method, objects: Array<Any>?) {
            val name: String = method.toEventAttribute()
            require(objects != null) { "Missing value for $name" }
            val annotations = AnnotationUtil.findAnnotatedAnnotations(method, Constraint::class.java)
            annotations.forEach {
                val validator = it.metaAnnotation.value.createInstance() as ConstraintValidator<Annotation>
                validator.initialize(it.annotation)
                val result = validator.isValid(name, objects[0])
                if (!result) {
                    logger.error("Validation failed for '$name' (source: $method) and value '${objects[0]}'.")
                    throw ConstraintValidationException(method, name, objects[0])
                }
            }
        }

        private fun validateContextConstraints(eventType: Class<T>) {
            val reqCtxConstraints = eventType.getAnnotation(AuditContextConstraints::class.java)
            if (reqCtxConstraints != null) {
                for (ctx in reqCtxConstraints.value) {
                    validateContextConstraint(eventType, ctx)
                }
            } else {
                val ctx = eventType.getAnnotation(AuditContext::class.java)
                validateContextConstraint(eventType, ctx)
            }
        }

        private fun validateContextConstraint(eventType: Class<T>, constraint: AuditContext?) {
            if (constraint == null) {
                // the request context is not mandatory
                return
            }
            val value = ThreadContext.get(constraint.key)
            if (value == null && constraint.required) {
                logger.error("Validation failed for '${constraint.key}' (source: $eventType) and value '${Required::class.java.simpleName}'.")
                throw ConstraintValidationException(eventType, constraint.key, Required::class.java.simpleName)
            }
        }

        private fun getProperties(eventType: Class<T>): List<Property> {
            var props: List<Property>? = eventTypes[eventType]
            if (props != null) {
                return props
            }
            props = ArrayList()
            val methods = eventType.methods
            var completionStatus = false
            for (method in methods) {
                if (method.name.startsWith("set") && method.name != methodNameSetExceptionHandler) {
                    if (method.name == methodNameSetCompletionStatus) {
                        completionStatus = true
                    }
                    val name: String = method.toEventAttribute()
                    val annotations = method.declaredAnnotations
                    val constraints: MutableList<Constraint> = ArrayList()
                    var isRequired = false
                    for (annotation in annotations) {
                        if (annotation is Constraint) {
                            constraints.add(annotation)
                        }
                        if (annotation is Required) {
                            isRequired = true
                        }
                    }
                    props.add(Property(name, isRequired, constraints))
                }
            }
            if (!completionStatus) {
                props.add(
                    Property(
                        EVENT_ATTRIBUTE_COMPLETION_STATUS,
                        false,
                        ArrayList()
                    )
                )
            }
            eventTypes.putIfAbsent(eventType, props)
            return eventTypes[eventType]!!
        }

        @Suppress("UNCHECKED_CAST")
        private fun setProperty(method: Method, args: Array<Any>?) {
            val name: String = method.toEventAttribute()
            require(args != null) { "Missing value for $name" }
            validateConstraint(method, args)
            val result: String = if (args[0] is List<*>) {
                args.joinToString(", ")
            } else if (args[0] is Map<*, *>) {
                val extra = StructuredDataMessage(name, null, null)
                extra.putAll(args[0] as Map<String, String>)
                message.addContent(name, extra)
                return
            } else {
                args[0].toString()
            }
            message.put(name, result)
        }

        private fun buildAuditMessage(eventType: Class<T>): AuditMessage {
            val eventName: String = getEventName(eventType)
            val msgLength: Int = getMaxLength(eventType)
            return AuditMessage(eventName, msgLength)
        }

        private fun getEventName(eventType: Class<T>): String {
            val eventName = eventType.getAnnotation(EventName::class.java)
            return eventName?.value ?: eventType.simpleName
        }

        private fun <T> getMaxLength(eventType: Class<T>): Int {
            val maxLength: MaxLength? = eventType.getAnnotation(MaxLength::class.java)
            return maxLength?.value ?: maxLengthDefault
        }

        private class Property(val name: String, val isRequired: Boolean, constraints: List<Constraint>) {
            private val constraints: Array<Constraint>

            init {
                this.constraints = constraints.toTypedArray()
            }
        }
    }
}
