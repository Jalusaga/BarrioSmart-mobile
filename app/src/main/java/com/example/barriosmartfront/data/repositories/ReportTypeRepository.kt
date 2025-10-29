package com.example.barriosmartfront.data.repositories

import com.example.barriosmartfront.data.dto.ReportType
import com.example.barriosmartfront.data.services.ReportTypesService
import retrofit2.Response


class ReportTypeRepository(private val service: ReportTypesService) {
    suspend fun getReportTypes(): Response<List<ReportType>> {
        return service.listReportTypes()
    }
}
