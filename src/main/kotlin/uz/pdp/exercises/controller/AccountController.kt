package uz.pdp.exercises.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import uz.pdp.exercises.dto.AccountResponseDTO
import uz.pdp.exercises.model.Account
import uz.pdp.exercises.model.User
import uz.pdp.exercises.service.AccountService
import uz.pdp.exercises.service.UserService


@RestController
@RequestMapping("/accounts")
@Tag(name = "Account Management", description = "Account management API")
class AccountController(
    private val accountService: AccountService,
    private val userService: UserService
) {

    @PostMapping
    @Operation(summary = "Create a new account")
    fun createAccount(@RequestParam userId: Long): ResponseEntity<AccountResponseDTO> {
        val account = accountService.createAccount(userId)
        val user = userService.findById(account.userId)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(buildResponse(account, user))
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get account by ID")
    fun getAccountBalance(@PathVariable id: Long): ResponseEntity<AccountResponseDTO> {
        val account = accountService.findById(id)
        val user = userService.findById(account.userId)
        return ResponseEntity.ok(buildResponse(account, user))
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete account by ID")
    fun deleteAccount(@PathVariable id: Long): ResponseEntity<String> {
        accountService.delete(id)
        return ResponseEntity.ok("Account deleted")
    }

    private fun buildResponse(account: Account, user: User) = AccountResponseDTO(
        id = account.id,
        userId = account.userId,
        balance = account.balance,
        username = user.username
    )

}