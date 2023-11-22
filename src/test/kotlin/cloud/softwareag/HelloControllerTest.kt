package cloud.softwareag

import io.micronaut.http.HttpRequest
import io.micronaut.http.MediaType
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

@MicronautTest
class HelloControllerTest {
    @Inject
    @Client("/")
    var client: HttpClient? = null
    @Test
    fun testHello() {
        val request: HttpRequest<*> = HttpRequest.GET<Any>("/hello").accept(MediaType.TEXT_PLAIN)
        val body = client!!.toBlocking().retrieve(request)
        assertNotNull(body)
        assertEquals("Hello World", body)
    }
}
