package de.gma.example

import de.gma.example.config.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    initDatabase()

    initDependencies()

    configureSessions()
    configureSecurity()

    configureSerialization()
    configureRouting()
}
