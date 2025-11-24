package uz.pdp.exercises.service

import org.springframework.stereotype.Service
import uz.pdp.exercises.base.BaseService
import uz.pdp.exercises.exceptions.AccountLimitException
import uz.pdp.exercises.exceptions.NotFoundException
import uz.pdp.exercises.model.Account
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

@Service
class AccountService(
    private val userService: UserService
) : BaseService<Account, Long> {

    private val storage = ConcurrentHashMap<Long, Account>()
    private val idGenerator = AtomicLong(1)
    private val userAccountIndex = ConcurrentHashMap<Long, MutableList<Long>>()

    private val corporateAccountLimit = 10
    private val regularAccountLimit = 5

    fun createAccount(userId: Long): Account {
        val user = userService.findById(userId)
        val accounts = userAccountIndex.getOrPut(userId) { mutableListOf() }
        val limit = if (user.isCorporate) corporateAccountLimit else regularAccountLimit

        if (accounts.size >= limit) {
            val userType = if (user.isCorporate) "Corporate" else "Regular"
            throw AccountLimitException(
                "$userType user can open a maximum of $limit accounts. You already have ${accounts.size} accounts"
            )
        }

        val account = Account(
            id = idGenerator.getAndIncrement(),
            userId = userId,
            balance = BigDecimal.ZERO
        )
        storage[account.id] = account
        accounts.add(account.id)

        return account
    }

    override fun create(entity: Account): Account {
        val id = idGenerator.getAndIncrement()
        val account = entity.copy(id = id)
        storage[id] = account
        userAccountIndex.getOrPut(account.userId) { mutableListOf() }.add(id)
        return account
    }

    override fun findById(id: Long): Account =
        storage[id] ?: throw NotFoundException("Account ID: $id not found")

    override fun findAll(): List<Account> = storage.values.toList()

    fun findByUserId(userId: Long): List<Account> {
        val accountIds = userAccountIndex[userId] ?: emptyList()
        return accountIds.mapNotNull { storage[it] }
    }

    override fun update(id: Long, entity: Account): Account {
        findById(id)
        val updated = entity.copy(id = id, updatedAt = LocalDateTime.now())
        storage[id] = updated
        return updated
    }

    override fun delete(id: Long) {
        val account = findById(id)
        storage.remove(id)
        userAccountIndex[account.userId]?.remove(id)
    }

    override fun exists(id: Long): Boolean = storage.containsKey(id)

    fun updateBalance(accountId: Long, newBalance: BigDecimal): Account {
        val account = findById(accountId)
        val updated = account.copy(balance = newBalance, updatedAt = LocalDateTime.now())
        storage[accountId] = updated
        return updated
    }
}
