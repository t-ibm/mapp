package cloud.softwareag.log.annotation

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class MaxLength(val value: Int)
