package com.example.barriosmartfront.data.dto


enum class ReportStatus {
    pending,
    approved,
    rejected
}

data class Report(
    val id: Long,                     // id bigint
    val community: Community,         // community_id -> objeto Community
    val reportType: ReportType,                   // type_id -> objeto ReportType
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