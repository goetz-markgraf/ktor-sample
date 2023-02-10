package de.gma.example

import de.gma.example.auth.LoginDTO
import de.gma.example.auth.UserResponse
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {

    @Test
    fun `should login and show username`() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
            install(HttpCookies)
        }

        val responseLogin = client.post("/public/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginDTO("admin", "admin"))
        }
        assertEquals(HttpStatusCode.OK, responseLogin.status)
        assertEquals(true, responseLogin.body())

        val responseReply = client.get("/api/user")
        assertEquals(HttpStatusCode.OK, responseReply.status)
        assertEquals("Logged in as: admin", responseReply.body<UserResponse>().user)
    }
}
