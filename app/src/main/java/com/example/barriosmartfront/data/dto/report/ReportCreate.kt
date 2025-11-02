package com.example.barriosmartfront.data.dto.report

data class ReportCreate(
    val title: String = "",
    val description: String = "",
    val isAnonymous: Boolean = false,
    val date: String = "",
    val time: String = "",
    val type_id: Int = 0,
    val community_id: Int = 0,
    val location: String = ""
)