package uz.pdp.exercises.model

import uz.pdp.exercises.base.BaseModel
import uz.pdp.exercises.enums.TransactionStatus
import uz.pdp.exercises.enums.TransactionType
import java.math.BigDecimal

data class Transaction(
    override val id: Long,
    val type: TransactionType,
    val fromAccountId: Long?,
    val toAccountId: Long?,
    val amount: BigDecimal,
    val commission: BigDecimal = BigDecimal.ZERO,
    val status: TransactionStatus,
    val description: String?,
    override val createdAt: java.time.LocalDateTime = java.time.LocalDateTime.now(),
    override val updatedAt: java.time.LocalDateTime = java.time.LocalDateTime.now()
) : BaseModel(id, createdAt, updatedAt)