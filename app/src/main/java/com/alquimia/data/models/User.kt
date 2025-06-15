package com.alquimia.data.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String = "",
    val email: String = "",
    val name: String = "",
    val age: Int = 0,
    val bio: String = "",
    val location: String = "",
    val profile_pictures: List<String> = emptyList(),
    val interests: List<String> = emptyList(),
    val description: String = "",
    val created_at: String? = null,
    val updated_at: String? = null
)

@Serializable
data class Conversation(
    val id: String = "",
    val user1_id: String = "",
    val user2_id: String = "",
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
    val timestamp: String? = null,
    val is_read: Boolean = false
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
