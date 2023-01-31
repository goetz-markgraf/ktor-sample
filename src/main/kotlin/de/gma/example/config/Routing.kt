package de.gma.example.config

import de.gma.example.articles.ArticleRepositoryImpl
import de.gma.example.articles.articleController
import de.gma.example.auth.authenticationRoutes
import de.gma.example.auth.replayRoutes
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        authenticationRoutes()
        replayRoutes()

        articleController(ArticleRepositoryImpl())
    }
}
