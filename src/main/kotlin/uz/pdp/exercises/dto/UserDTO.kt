package uz.pdp.exercises.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class UserDTO(
    @field:NotBlank(message = "Username can't be blank")
    @field:Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    @Schema(description = "Username of the user", example = "muzaffarvv", required = true)
    val username: String?,

    @field:NotBlank(message = "Email can't be blank")
    @field:Email(message = "Email must be valid")
    @Schema(description = "Email of the user", example = "youremail@example.com", required = true)
    val email: String?,

    @Schema(description = "Is the user corporate", example = "false", required = false)
    val isCorporate: Boolean = false
)
