package zinc.doiche.database.`object`

data class Config(
    val host: String,
    val port: Int,
    val database: String,
    val username: String,
    val password: String,
    val showSQL: String,
    val ddl: String
) {

    fun getURL(): String = "jdbc:postgresql://$host:$port/$database"
}