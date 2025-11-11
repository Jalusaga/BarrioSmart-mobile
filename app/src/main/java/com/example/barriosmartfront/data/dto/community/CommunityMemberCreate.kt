package com.example.barriosmartfront.data.dto.community

import kotlinx.serialization.Serializable

@Serializable
data class CommunityMemberCreate(
    val user_id: Long,
    val community_id: Int
)
