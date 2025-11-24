package uz.pdp.exercises.service

import org.springframework.stereotype.Service
import uz.pdp.exercises.base.BaseService
import uz.pdp.exercises.dto.DepositDTO
import uz.pdp.exercises.dto.TransactionResponseDTO
import uz.pdp.exercises.dto.TransferDTO
import uz.pdp.exercises.dto.WithdrawDTO
import uz.pdp.exercises.enums.TransactionStatus
import uz.pdp.exercises.enums.TransactionType
import uz.pdp.exercises.exceptions.InsufficientBalanceException
import uz.pdp.exercises.exceptions.InvalidOperationException
import uz.pdp.exercises.exceptions.NotFoundException
import uz.pdp.exercises.model.Account
import uz.pdp.exercises.model.Transaction
import java.math.BigDecimal
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

@Service
class TransactionService(
    private val accountService: AccountService,
    private val userService: UserService
) : BaseService<Transaction, Long> {

    private val storage = ConcurrentHashMap<Long, Transaction>()
    private val idGenerator = AtomicLong(1)
    private val transferCommission = BigDecimal("0.01")

    fun deposit(dto: DepositDTO): TransactionResponseDTO {
        val account = accountService.findById(dto.accountId)
        updateBalances(from = null, to = account, amount = dto.amount)
        return recordTransaction(
            type = TransactionType.DEPOSIT,
            fromAccountId = null,
            toAccountId = dto.accountId,
            amount = dto.amount,
            description = dto.description
        )
    }

    fun withdraw(dto: WithdrawDTO): TransactionResponseDTO {
        val account = accountService.findById(dto.accountId)
        checkEnoughBalance(account, dto.amount)
        updateBalances(from = account, to = null, amount = dto.amount)
        return recordTransaction(
            type = TransactionType.WITHDRAW,
            fromAccountId = dto.accountId,
            toAccountId = null,
            amount = dto.amount,
            description = dto.description
        )
    }

    fun transfer(dto: TransferDTO): TransactionResponseDTO {
        if (dto.fromAccountId == dto.toAccountId) {
            throw InvalidOperationException("Cannot transfer money to the same account")
        }

        val fromAccount = accountService.findById(dto.fromAccountId)
        val toAccount = accountService.findById(dto.toAccountId)

        val fromUser = userService.findById(fromAccount.userId)
        val toUser = userService.findById(toAccount.userId)

        val commission = if ((!fromUser.isCorporate && toUser.isCorporate) || (fromUser.isCorporate && toUser.isCorporate)) {
            dto.amount.multiply(transferCommission)
        } else BigDecimal.ZERO

        val totalAmount = dto.amount.add(commission)
        checkEnoughBalance(fromAccount, totalAmount)
        updateBalances(from = fromAccount, to = toAccount, amount = dto.amount, commission = commission)

        return recordTransaction(
            type = TransactionType.TRANSFER,
            fromAccountId = dto.fromAccountId,
            toAccountId = dto.toAccountId,
            amount = dto.amount,
            commission = commission,
            description = dto.description
        )
    }

    override fun create(entity: Transaction): Transaction {
        val id = idGenerator.getAndIncrement()
        val transaction = entity.copy(id = id)
        storage[id] = transaction
        return transaction
    }

    override fun findById(id: Long): Transaction =
        storage[id] ?: throw NotFoundException("Transaction ID: $id not found")

    override fun findAll(): List<Transaction> = storage.values.toList()

    override fun update(id: Long, entity: Transaction): Transaction {
        findById(id)
        val updated = entity.copy(id = id, updatedAt = java.time.LocalDateTime.now())
        storage[id] = updated
        return updated
    }

    override fun delete(id: Long) {
        storage.remove(id) ?: throw NotFoundException("Transaction ID: $id not found")
    }

    override fun exists(id: Long): Boolean = storage.containsKey(id)

    private fun checkEnoughBalance(account: Account, requiredAmount: BigDecimal) {
        if (account.balance < requiredAmount) {
            throw InsufficientBalanceException(
                "There is not enough money in the account. Available: ${account.balance}, Requirement: $requiredAmount"
            )
        }
    }

    private fun updateBalances(
        from: Account?,
        to: Account?,
        amount: BigDecimal,
        commission: BigDecimal = BigDecimal.ZERO
    ) {
        from?.let { accountService.updateBalance(
            it.id,
            it.balance.subtract(amount.add(commission))) }
        to?.let { accountService.updateBalance(
            it.id,
            it.balance.add(amount)) }
    }

    private fun recordTransaction(
        type: TransactionType,
        fromAccountId: Long?,
        toAccountId: Long?,
        amount: BigDecimal,
        commission: BigDecimal = BigDecimal.ZERO,
        description: String? = null
    ): TransactionResponseDTO {
        val transaction = Transaction(
            id = idGenerator.getAndIncrement(),
            type = type,
            fromAccountId = fromAccountId,
            toAccountId = toAccountId,
            amount = amount,
            commission = commission,
            status = TransactionStatus.SUCCESS,
            description = description
        )
        storage[transaction.id] = transaction
        return transaction.toDTO()
    }

    private fun Transaction.toDTO(): TransactionResponseDTO =
        TransactionResponseDTO(
            id = id,
            type = type.name,
            fromAccountId = fromAccountId,
            toAccountId = toAccountId,
            amount = amount,
            commission = commission,
            status = status.name,
            description = description
        )
}
