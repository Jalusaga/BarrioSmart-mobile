package com.example.barriosmartfront.data.repositories

import com.example.barriosmartfront.data.auth.ITokenStore
import com.example.barriosmartfront.data.dto.auth.RegisterResponse
import com.example.barriosmartfront.data.dto.community.CommunityResponse
import com.example.barriosmartfront.data.dto.report.Report
import com.example.barriosmartfront.data.dto.report.ReportResponse
import com.example.barriosmartfront.data.remote.ApiClient
import com.example.barriosmartfront.data.remote.ReportApi


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

    suspend fun updateReport(reportId: Int, updates: ReportResponse): Report? {
        return try {
            api.updateReport(reportId, updates)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getUsers(): List<RegisterResponse> {
        val res = api.getUsers()
        return if (res.isSuccessful) res.body() ?: emptyList() else emptyList()
    }

    suspend fun getCommunities(): List<CommunityResponse> {
        val res = api.getCommunities()
        return if (res.isSuccessful) res.body() ?: emptyList() else emptyList()
    }



    /*/** Filtrar reportes por comunidad o tipo (si quieres funciones extra) */
    suspend fun getReportsByCommunity(communityId: Int): List<Report> {
        return getAllReports().filter { it.community_id == communityId }
    }

    suspend fun getReportsByType(typeId: Int): List<Report> {
        return getAllReports().filter { it.type_id == typeId }
    }*/
}