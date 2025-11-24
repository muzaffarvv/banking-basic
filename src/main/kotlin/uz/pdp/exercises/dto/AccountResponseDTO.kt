package uz.pdp.exercises.dto

import java.math.BigDecimal

data class AccountResponseDTO(
    val id: Long,
    val userId: Long,
    val balance: BigDecimal,
    val username: String
)