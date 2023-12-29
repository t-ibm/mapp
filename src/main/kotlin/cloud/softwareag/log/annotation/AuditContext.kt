package cloud.softwareag.log.annotation

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@JvmRepeatable(AuditContextConstraints::class)
annotation class AuditContext(
    val key: String, val required: Boolean = false,
)
