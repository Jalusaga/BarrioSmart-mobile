package com.example.barriosmartfront.data.dto.report

class ReportResponse(
    val id: Int,
    val community_id: Int,
    val type_id: Int,
    val title: String,
    val description: String?,
    val latitude: Double,
    val longitude: Double,
    val occurred_at: String, // o Instant/LocalDateTime si parseas
    val status: String,
    val is_panic: Boolean,
    val reported_by_user_id: Int?,
    val approved_by_user_id: Int?,
    val created_at: String, // o Instant/LocalDateTime
    val updated_at: String  // o Instant/LocalDateTime
)