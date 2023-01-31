package de.gma.example.articles

import io.ktor.http.*
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
        val articlesRepository by inject<ArticleRepository>()

        route("/api/articles") {
            get {
                val allArticles = articlesRepository.allArticles()
                call.respond(allArticles)
            }

            post {
                val articleDto = call.receive<ArticleDto>()
                val newArticle = articlesRepository.newArticle(articleDto.name, articleDto.content)
                if (newArticle != null)
                    call.respond(newArticle)
                else
                    call.respond(HttpStatusCode.InternalServerError, "Error saving new article")
            }
        }
    }
}
