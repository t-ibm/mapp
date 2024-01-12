package cloud.softwareag.log.validator

import cloud.softwareag.log.annotation.constraint.StringConstraint
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.apache.logging.log4j.plugins.validation.ConstraintValidator

class StringValidator : ConstraintValidator<StringConstraint?> {
    private val logger: Logger = LogManager.getLogger(StringValidator::class.java)
    private var annotation: StringConstraint? = null

    override fun initialize(annotation: StringConstraint?) {
        this.annotation = annotation
    }

    override fun isValid(name: String, value: Any): Boolean {
        if (value !is CharSequence) {
            logger.error(annotation!!.message)
            return false
        }
        val range = annotation!!.min..annotation!!.max
        when (value.length) {
            !in range -> {
                logger.error(annotation!!.message)
                return false
            }
        }
        return true
    }
}
