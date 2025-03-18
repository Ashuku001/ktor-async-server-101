package dao

import com.example.plugins.model.UserRow
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import io.ktor.server.engine.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction


object DatabaseFactory {
    fun init() {
        Database.connect(createHikariDataSource())
        transaction {
            SchemaUtils.create(UserRow)
        }
    }

    // connect to db
    private fun createHikariDataSource(): HikariDataSource {
        val driverClass = "org.postgresql.Driver"
//        val dbPassword = applicationEnvironment().config.property("postgres.password").getString()
//        val dbUser = applicationEnvironment().config.property("postgres.user").getString()
//        val dbHost = applicationEnvironment().config.property("postgres.host").getString()
//        val dbName = applicationEnvironment().config.property("postgres.name").getString()
//        val dbPort = applicationEnvironment().config.property("postgres.port").getString()
        val jdbcUrl = "jdbc:postgresql://${"localhost"}:${"5432"}/${"socialapp"}"


        val hikariConfig = HikariConfig().apply {
            driverClassName = driverClass
            setJdbcUrl(jdbcUrl)
            password = "admin"
            username = "postgres"
            maximumPoolSize = 3
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }

        return HikariDataSource(hikariConfig)
    }

    // for running all non-blocking TX pass a suspend lambda block as a parameter and execute
    // run the query inside a suspend
    suspend fun <T> dbQuery(block: suspend () -> T)
    = newSuspendedTransaction(Dispatchers.IO) {block()} // no thread blocking execution
}