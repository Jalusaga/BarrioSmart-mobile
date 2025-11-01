package com.example.barriosmartfront.data.remote

import com.example.barriosmartfront.data.dto.report.Report
import com.example.barriosmartfront.data.dto.report.ReportCreate
import com.example.barriosmartfront.data.dto.report.ReportResponse
import com.example.barriosmartfront.data.dto.report.ReportUpdate
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ReportApi {

    // Obtener todos los reportes
    @GET("reports/")
    suspend fun listReports(): Response<List<ReportResponse>>

    // Obtener un reporte por ID
    @GET("reports/{id}")
    suspend fun getReportById(@Path("id") id: Int): Report

    // Crear un nuevo reporte
    @POST("reports/")
    suspend fun createReport(@Body report: Report): Report

    // Actualizar parcialmente un reporte
    @PATCH("reports/{id}")
    suspend fun updateReport(
        @Path("id") id: Int,
        @Body updates: ReportUpdate
    ): Report
}