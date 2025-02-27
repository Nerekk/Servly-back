package org.example.servlyback.dto

import org.example.servlyback.entities.custom_fields.Role

data class ChatUserInfo(
    val uid: String,
    val role: Role,
)
