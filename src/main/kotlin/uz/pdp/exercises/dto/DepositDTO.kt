package uz.pdp.exercises.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

data class DepositDTO(
    @field:NotNull(message = "Account id can't be null")
    @Schema(description = "ID of account to deposit into", example = "1")
    val accountId: Long,

    @field:NotNull(message = "Amount can't be null")
    @field:DecimalMin(value = "0.01", message = "Amount must be greater than 0.01")
    @Schema(description = "Amount to deposit", example = "500.00")
    val amount: BigDecimal,

    @Schema(description = "Optional description", example = "Salary deposit")
    val description: String? = null
)
