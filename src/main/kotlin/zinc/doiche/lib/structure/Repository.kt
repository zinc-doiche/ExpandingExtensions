package zinc.doiche.lib.structure

interface Repository<E: Any> {
    fun save(entity: E)
    fun findById(id: Long): E?
    fun delete(entity: E)
}