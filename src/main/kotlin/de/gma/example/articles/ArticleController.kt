package de.gma.example.articles

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject

@Serializable
data class ArticleDto(
    val name: String,
    val content: String
)

fun Route.articleController() {
    authenticate {
        val articles by inject<ArticleRepository>()

        route("/api/articles") {
            get {
                val allArticles = articles.allArticles()
                call.respond(allArticles)
            }

            post {
                val newArticle = call.receive<ArticleDto>()
                articles.newArticle(newArticle.name, newArticle.content)
                call.respond(articles.allArticles())
            }
        }
    }
}
