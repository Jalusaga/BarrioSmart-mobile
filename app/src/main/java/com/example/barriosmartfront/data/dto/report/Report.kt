package com.example.barriosmartfront.data.dto.report

import kotlinx.serialization.Serializable

enum class ReportStatus {
    PENDING,
    APPROVED,
    REJECTED
}

@Serializable
data class Report(
    val id: Long,
    val title: String,
    val type: String,           // Ej: "Robo", "Ruido", etc.
    val reportedBy: String,     // Nombre del usuario que reportó
    val occurredAt: String,     // Fecha/hora del incidente
    val status: ReportStatus,
    val isPanic: Boolean = false
)