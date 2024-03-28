package pt.isel.ps

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {
    @Test
    fun testRoot() =
        testApplication {
            // testApplication by default loads all modules and properties specified in the configuration file automatically (/resources/application.conf)
            // This environment configuration is used to force testApplication to use a specific test configuration file
            // TODO MapApplicationConfig
            environment {
                config = MapApplicationConfig("ktor.environment" to "test")
            }

            application {
                module()
            }

            client.get("/").apply {
                assertEquals(HttpStatusCode.OK, status)
                assertEquals("Hello, world!", this.bodyAsText())
            }
        }
}
