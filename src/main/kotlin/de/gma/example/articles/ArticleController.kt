package de.gma.example.articles

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class ArticleDto(
    val name: String,
    val content: String
)

fun Route.articleController(db: ArticleRepository) {
    authenticate {
        route("/api/articles") {
            get {
                val articles = db.allArticles()
                call.respond(articles)
            }

            post {
                val newArticle = call.receive<ArticleDto>()
                db.newArticle(newArticle.name, newArticle.content)
                call.respond(db.allArticles())
            }
        }
    }
}
