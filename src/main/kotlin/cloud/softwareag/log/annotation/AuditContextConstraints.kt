package cloud.softwareag.log.annotation

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class AuditContextConstraints(vararg val value: AuditContext)
