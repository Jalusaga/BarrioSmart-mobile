package com.example.barriosmartfront.data.dto.report

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class ReportType(
    val id: Int,
    val display_name: String
)

@RequiresApi(Build.VERSION_CODES.O)
data class Report(
    val id: Int? = null,                     // id bigint
    val community_id: Int = 0,         // community_id -> objeto Community
    val type_id: Int = 0,                   // type_id -> objeto ReportType
    val title: String = "",                // varchar(140)
    val description: String? ="",         // text, puede ser null
    val latitude: Double = 1.0,             // decimal(9,6)
    val longitude: Double = 1.0,            // decimal(9,6)
    val occurred_at: String = "",    // datetime
    val status: String = "",         // enum('pending','approved','rejected')
    val is_panic: Boolean = false,             // tinyint(1)
    val reported_by_user_id: Int? = 0,      // bigint, puede ser null
    val approved_by_user_id: Int? = 0,
)

@RequiresApi(Build.VERSION_CODES.O)
private fun defaultOccurredAt(): LocalDateTime {
    return LocalDateTime.now()
}