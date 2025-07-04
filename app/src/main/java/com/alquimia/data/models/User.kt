package com.alquimia.data.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String = "",
    val email: String = "",
    val name: String = "",
    val age: Int = 0,
    val city: String = "",
    val gender: String = "",
    val profile_picture: String? = null,
    val last_seen: String? = null,
    val created_at: String? = null,
    val interests: List<String>? = null // NOVO: Campo para interesses
)

@Serializable
data class Conversation(
    val id: String = "",
    val user1_id: String = "",
    val user2_id: String = "",
    val started_at: String? = null,
    val total_chat_time: Int = 0,
    val blur_status: Int = 100,
    val created_at: String? = null,
    val updated_at: String? = null
)

@Serializable
data class Message(
    val id: String = "",
    val conversation_id: String = "",
    val sender_id: String = "",
    val receiver_id: String = "",
    val content: String = "",
    val timestamp: String? = null
)

@Serializable
data class UserPreferences(
    val id: String = "",
    val user_id: String = "",
    val gender_preference: String = "",
    val min_age: Int = 18,
    val max_age: Int = 50,
    val location_radius: Int = 50,
    val city: String = ""
)
