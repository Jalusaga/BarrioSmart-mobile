package com.example.barriosmartfront.data.repositories

import com.example.barriosmartfront.data.dto.Report
import retrofit2.Response
import com.example.barriosmartfront.data.services.ReportsService


class ReportsRepository(private val service: ReportsService) {
    suspend fun getReports(): Response<List<Report>> {
        return service.listReports()
    }
}