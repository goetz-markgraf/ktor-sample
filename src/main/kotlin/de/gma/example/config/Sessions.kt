package de.gma.example.config

import de.gma.example.config.SessionDataItems.expires
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.sessions.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.less
import java.util.*

fun Application.configureSessions() {
    install(Sessions) {
        cookie<SessionData>("SESSION", SessionStorageDatabase()) {
            cookie.path = "/"
            cookie.extensions["SameSite"] = "lax"
        }
    }
}

data class SessionData(val uid: String = "") : Principal

object SessionDataItems : Table("session") {
    val id = varchar("id", 200)
    val value = varchar("value", 1000)
    val expires = long("expires")

    override val primaryKey = PrimaryKey(id)
}

const val SESSION_TIMEOUT = 60 * 1000

class SessionStorageDatabase : SessionStorage {
    override suspend fun invalidate(id: String) {
        dbQuery {
            clearExpiredSessions()
            SessionDataItems.deleteWhere { SessionDataItems.id eq id }
        }
    }

    override suspend fun read(id: String): String = dbQuery {
        clearExpiredSessions()
        val time = Date().time
        val query = SessionDataItems.select {
            (SessionDataItems.id eq id) and (expires greater time)
        }.map { it[SessionDataItems.value] }
        if (query.isEmpty() || query.size > 1)
            throw NoSuchElementException("Session $id not found")
        else
            query.first()
    }

    override suspend fun write(id: String, value: String) {
        val newExpires = Date().time + SESSION_TIMEOUT
        dbQuery {
            clearExpiredSessions()
            if (SessionDataItems.update({ SessionDataItems.id eq id }) {
                    it[expires] = newExpires
                } == 0
            )
                SessionDataItems.insert {
                    it[SessionDataItems.id] = id
                    it[SessionDataItems.value] = value
                    it[expires] = newExpires
                }

        }
    }

    private fun clearExpiredSessions() {
        val time = Date().time
        SessionDataItems.deleteWhere { expires less time }
    }
}
