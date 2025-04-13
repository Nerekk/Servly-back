package org.example.servlyback.security.firebase

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import org.example.servlyback.entities.JobRequest
import org.example.servlyback.entities.User

fun sendPushNotification(user: User, title: String, message: String) {
    if (user.fcmToken.isEmpty()) return

    val notification = Notification.builder()
        .setTitle(title)
        .setBody(message)
        .build()

    val messageToSend = Message.builder()
        .setToken(user.fcmToken)
        .setNotification(notification)
        .build()

    println("SENDING MESSAGE: $messageToSend")
    FirebaseMessaging.getInstance().send(messageToSend)
}

fun handleChatNotification(jobRequest: JobRequest, sender: User) {
    val customer = jobRequest.jobPosting.customer
    val provider = jobRequest.provider

    if (provider.user.uid == sender.uid) {
        sendPushNotification(customer.user, jobRequest.jobPosting.title, "${provider.name} wysłał(a) ci wiadomość")
    } else {
        sendPushNotification(provider.user, jobRequest.jobPosting.title, "${customer.name} wysłał(a) ci wiadomość")
    }
}

fun handleJobRequestNotification(targetUser: User, title: String, message: String) {
    sendPushNotification(targetUser, title, message)
}