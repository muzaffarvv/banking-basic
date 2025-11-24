package uz.pdp.exercises.dto

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

data class TransferDTO(

    @field:NotNull(message = "From account ID can't be null")
    val fromAccountId: Long,

    @field:NotNull(message = "To account ID can't be null")
    val toAccountId: Long,

    @field:NotNull(message = "Amount can't be null")
    @field:DecimalMin(value = "0.01", message = "Amount must be greater than 0.01")
    val amount: BigDecimal,

    val description: String? = null
)