package zinc.doiche.database.`object`

data class Pageable<E>(
    val content: List<E>,
    val total: Long,
    val page: Int
) {
    val maxPage: Int
        get() = (total / content.size).toInt() + 1
}