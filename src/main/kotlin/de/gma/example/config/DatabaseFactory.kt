package de.gma.example.config

import io.ktor.server.application.*
import kotlinx.coroutines.Dispatchers
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction


fun Application.initDatabase() {

    val host = environment.config.property("database.host").getString()
    val port = environment.config.property("database.port").getString()
    val flavour = environment.config.property("database.flavour").getString()
    val databaseName = environment.config.property("database.database").getString()
    val username = environment.config.property("database.username").getString()
    val password = environment.config.property("database.password").getString()

    val dbUrl = "jdbc:$flavour://$host:$port/$databaseName"

    // TODO introduce Connection Pool (Hikari)

    Database.connect(
        url = dbUrl,
        user = username,
        password = password
    )

    Flyway.configure()
        .createSchemas(true)
        .schemas("public")
        .dataSource(dbUrl, username, password)
        .load()
        .migrate()
}



suspend fun <T> dbQuery(block: suspend () -> T): T =
    newSuspendedTransaction(Dispatchers.IO) { block() }
