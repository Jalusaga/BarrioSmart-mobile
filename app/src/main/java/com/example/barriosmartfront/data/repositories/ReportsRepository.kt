package com.example.barriosmartfront.data.repositories

import com.example.barriosmartfront.data.auth.ITokenStore
import com.example.barriosmartfront.data.dto.community.CommunityResponse
import com.example.barriosmartfront.data.dto.report.Report
import com.example.barriosmartfront.data.dto.report.ReportCreate
import com.example.barriosmartfront.data.dto.report.ReportResponse
import com.example.barriosmartfront.data.dto.report.ReportUpdate
import com.example.barriosmartfront.data.remote.ApiClient
import com.example.barriosmartfront.data.remote.ReportApi
import retrofit2.Response





class ReportsRepository(
    private val tokenStore: ITokenStore // si necesitas token para autenticación
) {

        private val api: ReportApi = ApiClient
            .create("http://10.0.2.2:8000/", tokenStore)
            .create(ReportApi::class.java)

        /** Obtener todos los reportes */


    suspend fun getAllReports(): List<ReportResponse>? {
        val res = api.listReports()
        return if (res.isSuccessful) res.body() else null
    }


    /** Obtener un reporte por ID */
    suspend fun getReportById(reportId: Int): Report? {
        return try {
            api.getReportById(reportId)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /** Crear un nuevo reporte */
    suspend fun createReport(reportCreate: Report): Report? {
        return try {
            api.createReport(reportCreate)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /** Actualizar parcialmente un reporte */
    suspend fun updateReport(reportId: Int, updates: ReportUpdate): Report? {
        return try {
            api.updateReport(reportId, updates)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /*/** Filtrar reportes por comunidad o tipo (si quieres funciones extra) */
    suspend fun getReportsByCommunity(communityId: Int): List<Report> {
        return getAllReports().filter { it.community_id == communityId }
    }

    suspend fun getReportsByType(typeId: Int): List<Report> {
        return getAllReports().filter { it.type_id == typeId }
    }*/
}