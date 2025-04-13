package org.example.servlyback.user

import org.example.servlyback.entities.custom_fields.Role
import org.example.servlyback.util.ControllerMappings
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/" + ControllerMappings.USER)
class UserController(private val userService: UserService) {

    @GetMapping
    fun getUserRoles(@RequestParam("fcmToken") fcmToken: String): ResponseEntity<Role> {
        return userService.getUserRoles(fcmToken)
    }
}