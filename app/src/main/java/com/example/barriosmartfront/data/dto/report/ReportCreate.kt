package com.example.barriosmartfront.data.dto.report

data class ReportCreate(
    val community_id: Int,
    val type_id: Int,
    val title: String,
    val description: String?,
    val latitude: Double,
    val longitude: Double,
    val occurred_at: String,
    val is_panic: Boolean
)