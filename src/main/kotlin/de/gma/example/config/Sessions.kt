package de.gma.example.config

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.sessions.*

fun Application.configureSessions() {
    install(Sessions) {
        cookie<SessionData>("SESSION", SessionStorageDatabase()) {
            cookie.path = "/"
            cookie.extensions["SameSite"] = "lax"
        }
    }
}



data class SessionData(val uid: String = "") : Principal
