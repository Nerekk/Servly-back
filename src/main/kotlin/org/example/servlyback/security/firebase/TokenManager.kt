package org.example.servlyback.security.firebase

import com.google.firebase.auth.FirebaseToken
import org.springframework.security.core.context.SecurityContextHolder

class TokenManager {
    companion object {
        fun getFirebaseToken(): FirebaseToken? {
            val authentication = SecurityContextHolder.getContext().authentication

            if (authentication == null || !authentication.isAuthenticated) {
                println("getFirebaseToken: invalid authentication")
                return null
            }

            return authentication.principal as FirebaseToken
        }
    }
}