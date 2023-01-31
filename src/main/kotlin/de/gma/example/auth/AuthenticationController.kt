package de.gma.example.auth

import de.gma.example.config.SessionData
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import kotlinx.serialization.Serializable

@Serializable
data class LoginDTO(
    val username: String,
    val password: String
)

fun Route.authenticationRoutes() =
    route("/public") {
        post("/login") {
            try {
                val data =
                    call.receive<LoginDTO>()
                if (data.username.isNotEmpty() && data.username == data.password) {
                    call.sessions.set(SessionData(data.username))
                    call.respond(true)
                } else
                    call.respond(HttpStatusCode.Unauthorized, "username or password incorrect")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "malformed body")
            }
        }

        post("/invalidate-session") {
            call.sessions.clear<SessionData>()
            call.respond(true)
        }
    }
