package com.example.barriosmartfront.data.services

import com.example.barriosmartfront.data.dto.Report
import retrofit2.Response
import retrofit2.http.GET

interface ReportsService {
    @GET("reports/")
    suspend fun listReports(): Response<List<Report>>
}