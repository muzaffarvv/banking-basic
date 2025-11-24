package uz.pdp.exercises.controller

import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import uz.pdp.exercises.dto.UserDTO
import uz.pdp.exercises.dto.UserResponseDTO
import uz.pdp.exercises.model.Account
import uz.pdp.exercises.model.User
import uz.pdp.exercises.service.AccountService
import uz.pdp.exercises.service.UserService


@RestController
@RequestMapping("/users")
@Tag(name = "User Management", description = "User management API")
class UserController(
    private val userService: UserService,
    private val accountService: AccountService
) {
    @PostMapping()
    @Operation(summary = "Create a new user")
    fun createUser(@Valid @RequestBody dto: UserDTO): ResponseEntity<UserResponseDTO> {
        val user = userService.createUser(dto)
        val accounts = accountService.findByUserId(user.id)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(buildResponse(user,accounts))
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    fun getUserById(@PathVariable id: Long): ResponseEntity<UserResponseDTO> {
        val user = userService.findById(id)
        val accounts = accountService.findByUserId(user.id)
        return ResponseEntity.ok(buildResponse(user,accounts))
    }

    @GetMapping()
    @Operation(summary = "Get all users")
    fun getUsers(): ResponseEntity<List<UserResponseDTO>> {
        val users = userService.findAll()

        val response = users.map { user ->
            val accounts = accountService.findByUserId(user.id)
            buildResponse(user,accounts)
        }
        return ResponseEntity.ok(response)
    }

    private fun buildResponse(user: User, accounts: List<Account> ) = UserResponseDTO(
        id = user.id,
        username = user.username,
        email = user.email,
        isCorporate = user.isCorporate,
        accountCount = accounts.size
    )

}