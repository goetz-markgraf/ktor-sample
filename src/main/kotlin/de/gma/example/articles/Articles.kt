package de.gma.example.articles

import de.gma.example.articles.ArticlesTable.published
import de.gma.example.config.dbQuery
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.koin.dsl.module

@Serializable
data class Article(
    val id: Long,
    val published: Boolean,
    val name: String,
    val content: String
)

object ArticlesTable : Table() {
    val id = long("id").autoIncrement()
    val published = bool("published")
    val name = varchar("name", 128)
    val content = varchar("content", 1000)

    override val primaryKey = PrimaryKey(id)
}

interface ArticleRepository {
    suspend fun allArticles(): List<Article>
    suspend fun article(id: Long): Article?
    suspend fun newArticle(name: String, content: String): Article?
    suspend fun publishArticle(id: Long): Boolean
    suspend fun deleteArticle(id: Long): Boolean
}

val articleModule = module {
    single<ArticleRepository> { ArticleRepositoryImpl() }
}


class ArticleRepositoryImpl : ArticleRepository {
    override suspend fun allArticles(): List<Article> = dbQuery {
        ArticlesTable.selectAll().map(::toArticle)
    }

    override suspend fun article(id: Long): Article? = dbQuery {
        ArticlesTable
            .select { ArticlesTable.id eq id }
            .map(::toArticle)
            .singleOrNull()
    }

    override suspend fun newArticle(name: String, content: String): Article? = dbQuery {
        val insertStatement = ArticlesTable.insert {
            it[ArticlesTable.name] = name
            it[ArticlesTable.content] = content
            it[published] = false
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::toArticle)
    }

    override suspend fun publishArticle(id: Long): Boolean = dbQuery {
        ArticlesTable.update({ ArticlesTable.id eq id }) {
            it[published] = true
        } > 0
    }

    override suspend fun deleteArticle(id: Long): Boolean = dbQuery {
        ArticlesTable.deleteWhere { ArticlesTable.id eq id } > 0
    }

    private fun toArticle(row: ResultRow) = Article(
        id = row[ArticlesTable.id],
        published = row[published],
        name = row[ArticlesTable.name],
        content = row[ArticlesTable.content]
    )
}
