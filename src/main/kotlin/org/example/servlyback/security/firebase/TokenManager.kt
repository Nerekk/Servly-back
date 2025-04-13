package org.example.servlyback.security.firebase

import com.google.firebase.auth.FirebaseToken
import org.example.servlyback.entities.JobRequest
import org.springframework.security.core.context.SecurityContextHolder

class TokenManager {
    companion object {
        fun getFirebaseToken(): FirebaseToken {
            val authentication = SecurityContextHolder.getContext().authentication

            if (authentication == null || !authentication.isAuthenticated) {
                println("getFirebaseToken: invalid authentication")
            }

            return authentication.principal as FirebaseToken
        }

        fun verifyCustomer(jobRequest: JobRequest): Boolean {
            return verifyJobUser(jobRequest.jobPosting.customer.user.uid)
        }

        fun verifyProvider(jobRequest: JobRequest): Boolean {
            return verifyJobUser(jobRequest.provider.user.uid)
        }

        fun verifyJobUser(uidToCheck: String): Boolean {
            val firebaseToken = getFirebaseToken() ?: return false
            val uid = firebaseToken.uid
            return uid == uidToCheck
        }
    }
}