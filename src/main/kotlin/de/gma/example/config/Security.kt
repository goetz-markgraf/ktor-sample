package de.gma.example.config

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*

fun Application.configureSecurity() {
    authentication {
        session<SessionData> {
            validate { sessionData ->
                if (sessionData.uid.isEmpty()) {
                    sessions.clear<SessionData>()
                    null
                } else {
                    sessionData
                }
            }
            challenge {
                call.respond(HttpStatusCode.Forbidden, "not authorized")
            }
        }
    }
}
