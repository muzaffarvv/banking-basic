package uz.pdp.exercises.dto

import java.math.BigDecimal

data class TransactionResponseDTO(
    val id: Long,
    val type: String,
    val fromAccountId: Long?,
    val toAccountId: Long?,
    val amount: BigDecimal,
    val commission: BigDecimal,
    val status: String,
    val description: String?
)