package uz.pdp.exercises.base

interface BaseService<T, ID> {
    fun create(entity: T): T
    fun findById(id: ID): T
    fun findAll(): List<T>
    fun update(id: ID, entity: T): T
    fun delete(id: ID)
    fun exists(id: ID): Boolean
}