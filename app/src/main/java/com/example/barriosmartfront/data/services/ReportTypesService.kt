package com.example.barriosmartfront.data.services

import com.example.barriosmartfront.data.dto.report.ReportType
import retrofit2.Response
import retrofit2.http.GET

interface ReportTypesService {
    @GET("report-types/")
    suspend fun listReportTypes(): Response<List<ReportType>>
}