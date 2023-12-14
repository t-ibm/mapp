package cloud.softwareag

import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Produces
import org.apache.logging.log4j.LogManager
import org.slf4j.LoggerFactory

@Controller("/hello")
class HelloController {
    private val slf4jLogger = LoggerFactory.getLogger(HelloController::class.java)
    private val log4jLogger = LogManager.getLogger()
    @Get
    @Produces(MediaType.TEXT_PLAIN)
    fun index(): String {
        slf4jLogger.debug("Hello World from SLF4J.")
        log4jLogger.debug("Hello World from Log4J.")
        return "Hello World."
    }
}
