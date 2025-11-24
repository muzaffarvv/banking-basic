package uz.pdp.exercises.base

import java.time.LocalDateTime

abstract class BaseModel(
    open val id: Long,
    open val createdAt: LocalDateTime = LocalDateTime.now(),
    open val updatedAt: LocalDateTime = LocalDateTime.now()
)