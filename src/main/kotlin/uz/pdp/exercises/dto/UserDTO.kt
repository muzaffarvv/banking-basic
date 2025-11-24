package uz.pdp.exercises.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class UserDTO(
    @field:NotBlank (message = "Username can't be blank")
    @field:Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    val username: String,

    @field:NotBlank (message = "Email can't be blank")
    @field:Email(message = "Email must be valid")
    val email: String,

    val isCorporate: Boolean = false
)