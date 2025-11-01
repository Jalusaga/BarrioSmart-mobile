package com.example.barriosmartfront.data.dto.report

import java.time.LocalDateTime


data class ReportType(
    val id: Int,
    val display_name: String
)
enum class ReportStatus {
    pending,
    approved,
    rejected
}

class Report(
    val id: Int,                     // id bigint
    val community_id: Int,         // community_id -> objeto Community
    val type_id: Int,                   // type_id -> objeto ReportType
    val title: String,                // varchar(140)
    val description: String?,         // text, puede ser null
    val latitude: Double,             // decimal(9,6)
    val longitude: Double,            // decimal(9,6)
    val occurred_at: LocalDateTime,    // datetime
    val status: ReportStatus,         // enum('pending','approved','rejected')
    val is_panic: Boolean,             // tinyint(1)
    val reported_by_user_id: Int?,      // bigint, puede ser null
    val approved_by_user_id: Int?,
)