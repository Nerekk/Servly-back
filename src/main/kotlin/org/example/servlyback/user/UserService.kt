package org.example.servlyback.user

import com.google.firebase.auth.FirebaseAuth
import org.example.servlyback.entities.User
import org.example.servlyback.entities.custom_fields.Role
import org.example.servlyback.security.firebase.TokenManager
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class UserService(private val userRepository: UserRepository) {

    fun getUserRoles(fcmToken: String): ResponseEntity<Role> {
        val firebaseToken = TokenManager.getFirebaseToken()
        val uid = firebaseToken.uid
        val user = userRepository.findByUid(uid)

        if (user != null) {
            if (fcmToken.isNotEmpty()) {
                user.fcmToken = fcmToken
                userRepository.save(user)
            }
            return ResponseEntity(user.role, HttpStatus.OK)
        } else {
            val newUser = User(
                uid = uid,
                email = getEmailByUid(uid),
                fcmToken = fcmToken
            )
            userRepository.save(newUser)

            return ResponseEntity(newUser.role, HttpStatus.OK)
        }
    }

    fun addUserRole(role: Role) {
        val firebaseToken = TokenManager.getFirebaseToken()
        val uid = firebaseToken.uid
        val user = userRepository.findByUid(uid)

        if (user != null) {
            when (role) {
                Role.CUSTOMER -> {
                    when (user.role) {
                        Role.NONE -> user.role = role
                        Role.PROVIDER -> user.role = Role.BOTH
                        else -> {}
                    }
                }
                Role.PROVIDER -> {
                    when (user.role) {
                        Role.NONE -> user.role = role
                        Role.CUSTOMER -> user.role = Role.BOTH
                        else -> {}
                    }
                }
                else -> {}
            }

            userRepository.save(user)
        }
    }

    fun getEmailByUid(uid: String): String {
        val userRecord = FirebaseAuth.getInstance().getUser(uid)
        return userRecord.email
    }


}