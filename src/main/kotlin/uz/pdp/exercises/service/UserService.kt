package uz.pdp.exercises.service

import org.springframework.stereotype.Service
import uz.pdp.exercises.base.BaseService
import uz.pdp.exercises.dto.UserDTO
import uz.pdp.exercises.exceptions.DuplicateElementException
import uz.pdp.exercises.exceptions.NotFoundException
import uz.pdp.exercises.model.User
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

@Service
class UserService : BaseService<User, Long> {

    private val storage = ConcurrentHashMap<Long, User>()
    private val idGenerator = AtomicLong(1)
    private val usernameIndex = ConcurrentHashMap<String, Long>()
    private val emailIndex = ConcurrentHashMap<String, Long>()

    fun createUser(dto: UserDTO): User {
        if (usernameIndex.containsKey(dto.username)) {
            throw DuplicateElementException("Username '${dto.username}' already exists")
        }
        if (emailIndex.containsKey(dto.email)) {
            throw DuplicateElementException("Email '${dto.email}' already exists")
        }

        val user = User(
            id = idGenerator.getAndIncrement(),
            username = dto.username,
            email = dto.email,
            isCorporate = dto.isCorporate
        )

        storage[user.id] = user
        usernameIndex[user.username] = user.id
        emailIndex[user.email] = user.id

        return user
    }

    override fun create(entity: User): User {
        val id = idGenerator.getAndIncrement()
        val user = entity.copy(id = id)
        storage[id] = user
        usernameIndex[user.username] = id
        emailIndex[user.email] = id
        return user
    }

    override fun findById(id: Long): User =
        storage[id] ?: throw NotFoundException("User ID: $id not found")

    override fun findAll(): List<User> = storage.values.toList()

    override fun update(id: Long, entity: User): User {
        findById(id)
        val updated = entity.copy(id = id, updatedAt = LocalDateTime.now())
        storage[id] = updated
        return updated
    }

    override fun delete(id: Long) {
        val user = findById(id)
        storage.remove(id)
        usernameIndex.remove(user.username)
        emailIndex.remove(user.email)
    }

    override fun exists(id: Long): Boolean = storage.containsKey(id)
}