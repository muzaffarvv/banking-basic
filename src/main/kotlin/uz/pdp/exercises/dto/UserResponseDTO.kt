package uz.pdp.exercises.dto

data class UserResponseDTO(
    val id: Long,
    val username: String,
    val email: String,
    val isCorporate: Boolean = false,
    val accountCount: Int = 0
)