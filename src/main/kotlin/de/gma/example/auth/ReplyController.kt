package de.gma.example.auth

import de.gma.example.config.SessionData
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val user: String
)



fun Route.replayRoutes() =
    authenticate {
        route("/api") {
            get("/user") {
                val user = call.principal<SessionData>()
                call.respond(UserResponse(if (user != null) "Logged in as: ${user.uid}" else "unauthorized"))
            }
        }
    }
