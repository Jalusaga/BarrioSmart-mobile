package com.example.barriosmartfront.data.dto.community

import com.example.barriosmartfront.data.dto.member.Member
import com.example.barriosmartfront.data.dto.report.Report
import com.example.barriosmartfront.data.dto.report.ReportResponse

data class CommunityResponse(
    val id: Int,
    val name: String,
    val description: String? = null,
    val is_active: Boolean,
    val members: List<Member> = emptyList(),
    val reports: List<ReportResponse> = emptyList()
)
