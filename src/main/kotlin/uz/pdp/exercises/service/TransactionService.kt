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
import uz.pdp.exercises.model.Transaction
import java.math.BigDecimal
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

@Service
class TransactionService(
    private val accountService: AccountService,
    private val userService: UserService
): BaseService<Transaction, Long> {

    private val storage = ConcurrentHashMap<Long, Transaction>()
    private val idGenerator = AtomicLong(1)
    private val transferCommission = BigDecimal(0.01)

    fun deposit(dto: DepositDTO): TransactionResponseDTO {
        val account = accountService.findById(dto.accountId)

        val newBalance = account.balance.add(dto.amount)
        accountService.updateBalance(dto.accountId, newBalance)

        val transaction = Transaction( // ###
            id = idGenerator.andIncrement,
            type = TransactionType.DEPOSIT,
            fromAccountId = null,
            toAccountId = dto.accountId,
            amount = dto.amount,
            status = TransactionStatus.SUCCESS,
            description = dto.description
        )

        storage[transaction.id] = transaction
        return toDTO(transaction)
    }

    fun withdraw(dto: WithdrawDTO): TransactionResponseDTO {
        val account = accountService.findById(dto.accountId)
        if (account.balance < dto.amount) {
            throw InsufficientBalanceException(
                "There is not enough money in the account. Available: ${account.balance}, Requirement: ${dto.amount}")
        }
        
        val newBalance = account.balance.subtract(dto.amount)
        accountService.updateBalance(dto.accountId, newBalance)
        
        val transaction = Transaction(
            id = idGenerator.andIncrement,
            type = TransactionType.WITHDRAW,
            fromAccountId = dto.accountId,
            toAccountId = null,
            amount = dto.amount,
            commission = BigDecimal.ZERO,
            status = TransactionStatus.SUCCESS,
            description = dto.description
        )
        
        storage[transaction.id] = transaction
        return toDTO(transaction)
    }
    
    fun transfer(dto: TransferDTO): TransactionResponseDTO {
        if (dto.fromAccountId == dto.toAccountId) {
            throw InvalidOperationException("Can't transfer money from the same account")
        }
        val fromAccount = accountService.findById(dto.fromAccountId)
        val toAccount = accountService.findById(dto.toAccountId)
        
        val fromUser = userService.findById(fromAccount.userId)
        val toUser = userService.findById(toAccount.userId)

        val commission = if (!fromUser.isCorporate && toUser.isCorporate) {
            dto.amount.multiply(transferCommission)
        } else if (fromUser.isCorporate && toUser.isCorporate) {
            dto.amount.multiply(transferCommission)
        } else {
            BigDecimal.ZERO
        }

        val totalAmount = dto.amount.add(commission)
        
        if (fromAccount.balance < totalAmount) {
            throw InsufficientBalanceException(
                "There is not enough money in the account. Available: ${fromAccount.balance}, Requirement: $totalAmount")
        }
        
        accountService.updateBalance(dto.fromAccountId, fromAccount.balance.subtract(totalAmount))
        accountService.updateBalance(dto.toAccountId, toAccount.balance.add(totalAmount))
        
        val transaction = Transaction(
            id = idGenerator.andIncrement,
            type = TransactionType.TRANSFER,
            fromAccountId = dto.fromAccountId,
            toAccountId = dto.toAccountId,
            amount = dto.amount,
            commission = commission,
            status = TransactionStatus.SUCCESS,
            description = dto.description
        )
        storage[transaction.id] = transaction
        return toDTO(transaction)
    }

    override fun create(entity: Transaction): Transaction {
        val id = idGenerator.andIncrement
        val transaction = entity.copy(id = id)
        storage[id] = transaction
        return transaction
    }

    override fun findById(id: Long): Transaction {
        return storage[id] ?: throw NotFoundException("Transaction ID: $id not found")
    }

    override fun findAll(): List<Transaction> = storage.values.toList()

    override fun update(
        id: Long,
        entity: Transaction
    ): Transaction {
      val existing = findById(id)
        val updated = entity.copy(id = id, updatedAt = java.time.LocalDateTime.now())
        storage[id] = updated
        return updated
    }

    override fun delete(id: Long) {
        storage.remove(id) ?: throw NotFoundException("Transaction ID: $id not found")
    }

    override fun exists(id: Long): Boolean = storage.containsKey(id)

    private fun toDTO(transaction: Transaction): TransactionResponseDTO {
        return TransactionResponseDTO(
            id = transaction.id,
            type = transaction.type.name,
            fromAccountId = transaction.fromAccountId,
            toAccountId = transaction.toAccountId,
            amount = transaction.amount,
            commission = transaction.commission,
            status = transaction.status.name,
            description = transaction.description
        )
    }

}