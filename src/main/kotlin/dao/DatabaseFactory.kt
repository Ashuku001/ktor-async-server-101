package dao

import com.example.plugins.dao.follows.FollowsTable
import com.example.plugins.dao.post.PostTable
import com.example.plugins.dao.post_comment.PostCommentTable
import com.example.plugins.dao.post_likes.PostLikeTable
import com.example.plugins.dao.user.UserTable
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction


object DatabaseFactory {
    fun init() {
        Database.connect(createHikariDataSource())
        transaction {
            SchemaUtils.create(
                UserTable,
                FollowsTable,
                PostTable,
                PostLikeTable,
                PostCommentTable
            )
        }
    }

    // connect to db
    private fun createHikariDataSource(): HikariDataSource {
        val driverClass = "org.postgresql.Driver"
        val dbPassword = System.getenv("postgres.password")
        val dbUser = System.getenv("postgres.user")
        val dbHost = System.getenv("postgres.host")
        val dbName = System.getenv("postgres.name")
        val dbPort = System.getenv("postgres.port")
        val jdbcUrl = "jdbc:postgresql://$dbHost:$dbPort/$dbName"


        val hikariConfig = HikariConfig().apply {
            driverClassName = driverClass
            setJdbcUrl(jdbcUrl)
            password = dbPassword
            username = dbUser
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