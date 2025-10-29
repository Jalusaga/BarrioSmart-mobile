package com.example.barriosmartfront.ui.report

import com.example.barriosmartfront.data.dto.community.CommunityResponse


data class Report(
    val title: String,
    val description: String,
    val reporter: String,
    val date: String,
    val status: ReportStatus,
    val community: CommunityResponse
)
enum class ReportStatus { PENDIENTE, APROBADO, RECHAZADO }
