package uz.pdp.exercises.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import uz.pdp.exercises.dto.*
import uz.pdp.exercises.service.TransactionService

@RestController
@RequestMapping("/transactions")
@Tag(name = "Transaction Management", description = "Transaction management API")
class TransactionController(
    private val transactionService: TransactionService
) {

    @PostMapping("/deposit")
    @Operation(summary = "Create a new deposit transaction")
    fun deposit(@Valid @RequestBody dto: DepositDTO) = buildResponse(transactionService.deposit(dto))

    @PostMapping("/withdraw")
    @Operation(summary = "Create a new withdraw transaction")
    fun withdraw(@Valid @RequestBody dto: WithdrawDTO) = buildResponse(transactionService.withdraw(dto))

    @PostMapping("/transfer")
    @Operation(summary = "Create a new transfer transaction")
    fun transfer(@Valid @RequestBody dto: TransferDTO) = buildResponse(transactionService.transfer(dto))

    private fun buildResponse(body: TransactionResponseDTO) =
        ResponseEntity.status(HttpStatus.CREATED).body(body)
}