package com.spec

import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import io.ktor.server.testing.TestApplicationBuilder
import io.ktor.server.application.application
import kotlin.test.*

import com.spec.api.configureSerialization
import com.spec.api.configureRouting

class ServerTest {

    private fun TestApplicationBuilder.configure() {
        application {
            configureSerialization()
            configureRouting()
        }
    }

    @Test
    fun `test root endpoint`() = testApplication {
        // loads default configuration
        configure()
        // verify server root returns 200
        assertEquals(HttpStatusCode.OK, client.get("/").status)
    }

}
