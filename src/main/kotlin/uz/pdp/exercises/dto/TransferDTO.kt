package uz.pdp.exercises.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

data class TransferDTO(
    @field:NotNull(message = "From account ID can't be null")
    @Schema(description = "Account ID to transfer from", example = "1")
    val fromAccountId: Long,

    @field:NotNull(message = "To account ID can't be null")
    @Schema(description = "Account ID to transfer to", example = "2")
    val toAccountId: Long,

    @field:NotNull(message = "Amount can't be null")
    @field:DecimalMin(value = "0.01", message = "Amount must be greater than 0.01")
    @Schema(description = "Amount to transfer", example = "150.00")
    val amount: BigDecimal,

    @Schema(description = "Optional description", example = "Payment for services")
    val description: String? = null
)