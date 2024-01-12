package cloud.softwareag.log.validator

import cloud.softwareag.log.annotation.constraint.IntConstraint
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.apache.logging.log4j.plugins.convert.TypeConverter
import org.apache.logging.log4j.plugins.convert.TypeConverterFactory
import org.apache.logging.log4j.plugins.validation.ConstraintValidator

class IntValidator : ConstraintValidator<IntConstraint?> {
    private val logger: Logger = LogManager.getLogger(IntValidator::class.java)
    private val converter: TypeConverter<Int> = TypeConverterFactory(emptyList()).getTypeConverter(Int::class.java)
    private var annotation: IntConstraint? = null

    override fun initialize(annotation: IntConstraint?) {
        this.annotation = annotation
    }

    override fun isValid(name: String, value: Any): Boolean {
        if (value is CharSequence) {
            return isValid(name, converter.convert(value.toString(), -1))
        }
        if (value !is Int) {
            logger.error(annotation!!.message)
            return false
        }
        val range = annotation!!.min..annotation!!.max
        when (value) {
            !in range -> {
                logger.error(annotation!!.message)
                return false
            }
        }
        return true
    }
}
