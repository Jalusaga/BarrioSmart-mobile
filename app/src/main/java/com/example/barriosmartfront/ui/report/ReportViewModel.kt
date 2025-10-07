package com.example.barriosmartfront.ui.report

import com.example.barriosmartfront.ui.community.Community


data class Report(
    val title: String,
    val description: String,
    val reporter: String,
    val date: String,
    val status: ReportStatus,
    val community: Community
)
enum class ReportStatus { PENDIENTE, APROBADO, RECHAZADO }
