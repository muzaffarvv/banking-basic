package uz.pdp.exercises.model

import uz.pdp.exercises.base.BaseModel
import java.time.LocalDateTime

data class User(
    override val id: Long,
    val username: String,
    val email: String,
    val isCorporate: Boolean = false,
    override val createdAt: LocalDateTime = LocalDateTime.now(),
    override val updatedAt: LocalDateTime = LocalDateTime.now()
) : BaseModel(id, createdAt, updatedAt)