package de.gma.example.config

import de.gma.example.articles.articleModule
import io.ktor.server.application.*
import org.koin.ktor.plugin.Koin

fun Application.initDependencies() {
    install(Koin) {
        modules(articleModule)
    }
}
