package com.example.barriosmartfront.data.dto.community

import com.example.barriosmartfront.data.dto.member.Member
import com.example.barriosmartfront.data.dto.report.Report

data class CommunityResponse(
    val id: Int,
    val name: String,
    val description: String? = null,
    val isActive: Boolean,
    val isJoined: Boolean,
    val members: List<Member> = emptyList(),
    val reports: List<Report> = emptyList()
)
