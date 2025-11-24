package uz.pdp.exercises.model

import uz.pdp.exercises.base.BaseModel
import java.math.BigDecimal
import java.time.LocalDateTime

data class Account(
    override val id: Long,
    val userId: Long,
    val balance: BigDecimal = BigDecimal.ZERO,
    override val createdAt: LocalDateTime = LocalDateTime.now(),
    override val updatedAt: LocalDateTime = LocalDateTime.now()
) : BaseModel(id, createdAt, updatedAt)