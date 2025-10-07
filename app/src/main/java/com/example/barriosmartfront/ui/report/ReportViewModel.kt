package com.example.barriosmartfront.ui.report


data class Report(
    val title: String,
    val description: String,
    val reporter: String,
    val date: String,
    val status: ReportStatus
)
enum class ReportStatus { PENDIENTE, APROBADO, RECHAZADO }
