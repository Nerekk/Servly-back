package org.example.servlyback.dto

data class ChatInfo(
    val jobRequestId: Long,
    val myUid: String,
    val secondUid: String,
    val secondName: String
)
