package org.example.servlyback.user

import org.example.servlyback.entities.User
import org.example.servlyback.entities.custom_fields.Role
import org.example.servlyback.util.ControllerMappings
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/" + ControllerMappings.USER)
class UserController(private val userService: UserService) {

    @GetMapping
    fun getUserRoles(): ResponseEntity<Role> {
        return userService.getUserRoles()
    }
}