package org.example.servlyback.security.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseToken
import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken
import org.springframework.stereotype.Component

@Component
class FirebaseAuthenticationFilter : Filter {

    override fun doFilter(
        request: ServletRequest,
        response: ServletResponse,
        filterChain: FilterChain
    ) {
        request as HttpServletRequest
        response as HttpServletResponse

        val authHeader = request.getHeader("Authorization")
        if (authHeader?.startsWith("Bearer ") == true) {
            val token = authHeader.substring(7)
            try {
                val decodedToken: FirebaseToken = FirebaseAuth.getInstance().verifyIdToken(token)
                println("Token valid: $token, UID: ${decodedToken.uid}")
                val authentication = PreAuthenticatedAuthenticationToken(decodedToken, null)
                authentication.isAuthenticated = true
                SecurityContextHolder.getContext().authentication = authentication
            } catch (e: Exception) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Firebase token")
                return
            }
        }
        filterChain.doFilter(request, response)
    }
}