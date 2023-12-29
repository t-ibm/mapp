package cloud.softwareag.log.annotation.constraint

import cloud.softwareag.log.validator.IntValidator
import org.apache.logging.log4j.plugins.validation.Constraint

@Retention(AnnotationRetention.RUNTIME)
@Target(
    AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.FIELD,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
@Constraint(IntValidator::class)
annotation class IntConstraint(
    val min: Int = Int.MIN_VALUE,
    val max: Int = Int.MAX_VALUE,
    val message: String = "The provided integer value is invalid",
)
