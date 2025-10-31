package com.example.barriosmartfront.data.dto.report

import com.example.barriosmartfront.data.dto.community.Community


data class ReportType(
    val id: Short,
    val name: String
)
enum class ReportStatus {
    pending,
    approved,
    rejected
}

data class Report(
    val id: Long,                     // id bigint
    val community_id: Community,         // community_id -> objeto Community
    val type_id: ReportType,                   // type_id -> objeto ReportType
    val title: String,                // varchar(140)
    val description: String?,         // text, puede ser null
    val latitude: Double,             // decimal(9,6)
    val longitude: Double,            // decimal(9,6)
    val occurred_at: String,    // datetime
    val status: ReportStatus,         // enum('pending','approved','rejected')
    val is_panic: Boolean,             // tinyint(1)
    val reported_by_user_id: Long?,      // bigint, puede ser null
    val approved_by_user_id: Long?,
)