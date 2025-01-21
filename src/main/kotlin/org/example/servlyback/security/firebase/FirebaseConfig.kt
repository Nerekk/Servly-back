package org.example.servlyback.security.firebase

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FirebaseConfig {
    @Bean
    fun firebaseInit(): FirebaseApp {
        return if (FirebaseApp.getApps().isEmpty()) {
            val serviceAccount = javaClass.getResourceAsStream("/serviceAccountKey.json")

            val options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build()

            FirebaseApp.initializeApp(options)
        } else {
            FirebaseApp.getInstance()
        }
    }
}