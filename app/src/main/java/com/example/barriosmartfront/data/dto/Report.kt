package com.example.barriosmartfront.data.dto

import com.example.barriosmartfront.ui.community.Community

enum class ReportStatus {
    Pendiente,
    Aprobado,
    Rechazado
}

// Tipo de reporte (para type_id)
data class Type(
    val id: Short,
    val name: String
)

data class Report(
    val id: Long,                     // id bigint
    val community: Community,         // community_id -> objeto Community
    val type: Type,                   // type_id -> objeto Type
    val title: String,                // varchar(140)
    val description: String?,         // text, puede ser null
    val latitude: Double,             // decimal(9,6)
    val longitude: Double,            // decimal(9,6)
    val occurredAt: String,    // datetime
    val status: ReportStatus,         // enum('pending','approved','rejected')
    val isPanic: Boolean,             // tinyint(1)
    val reportedByUserId: Long?,      // bigint, puede ser null
    val approvedByUserId: Long?,
)