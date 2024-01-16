package cloud.softwareag.log.annotation.constraint

import cloud.softwareag.log.validator.StringValidator
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
@Constraint(StringValidator::class)
annotation class StringConstraint(
    val min: Int = 0,
    val max: Int = 128,
    val message: String = "The provided string value is invalid",
)
