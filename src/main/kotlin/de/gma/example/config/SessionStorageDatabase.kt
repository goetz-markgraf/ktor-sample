package de.gma.example.config

import de.gma.example.config.SessionDataTable.expires
import io.ktor.server.sessions.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.less
import java.util.*


object SessionDataTable : Table("session") {
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
            SessionDataTable.deleteWhere { SessionDataTable.id eq id }
        }
    }


    override suspend fun read(id: String): String = dbQuery {
        clearExpiredSessions()
        val time = Date().time
        val uid = SessionDataTable.select {
            (SessionDataTable.id eq id) and (expires greater time)
        }.map { it[SessionDataTable.value] }
            .singleOrNull()

        uid ?: throw NoSuchElementException("Session $id not found")
    }


    override suspend fun write(id: String, value: String) {
        val newExpires = Date().time + SESSION_TIMEOUT
        dbQuery {
            clearExpiredSessions()
            if (SessionDataTable.update({ SessionDataTable.id eq id }) {
                    it[expires] = newExpires
                } == 0
            )
                SessionDataTable.insert {
                    it[SessionDataTable.id] = id
                    it[SessionDataTable.value] = value
                    it[expires] = newExpires
                }
        }
    }


    private fun clearExpiredSessions() {
        val time = Date().time
        SessionDataTable.deleteWhere { expires less time }
    }
}
