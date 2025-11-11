package com.example.barriosmartfront.data.dto.community

import kotlinx.serialization.Serializable

@Serializable
data class CommunityMemberResponse(
    val user_id: Long,
    val community_id: Int,
    val joined_at: String
)