package com.example.barriosmartfront.data.repositories


import android.util.Log
import com.example.barriosmartfront.data.auth.ITokenStore
import com.example.barriosmartfront.data.dto.community.Community
import com.example.barriosmartfront.data.dto.community.CommunityCreate
import com.example.barriosmartfront.data.dto.community.CommunityResponse
import com.example.barriosmartfront.data.dto.community.CommunityUpdate
import com.example.barriosmartfront.data.dto.member.Member
import com.example.barriosmartfront.data.dto.report.Report
import com.example.barriosmartfront.data.dto.report.ReportResponse
import com.example.barriosmartfront.data.remote.ApiClient
import com.example.barriosmartfront.data.remote.CommunityApi

class CommunityRepository(
    private val tokenStore: ITokenStore
) {
    private val api: CommunityApi = ApiClient
        .create("http://10.0.2.2:8000/", tokenStore)
        .create(CommunityApi::class.java)

    suspend fun getAll(): List<CommunityResponse>? {
        val res = api.getCommunities()
        println("Response code: ${res.code()}, body: ${res.body()}")

        return if (res.isSuccessful) res.body() else null
    }


    suspend fun getById(id: Int): CommunityResponse? {
        val response = api.getCommunity(id)
        return if (response.isSuccessful) {
            response.body()?.let { community ->
                CommunityResponse(
                    id = community.id.toInt(),
                    name = community.name,
                    description = community.description ?: "",
                   // O lo que venga del backend si existe
                    is_active = community.is_active,
                )
            }
        } else null
    }


    suspend fun create(community: CommunityCreate): Community? {
        val res = api.createCommunity(community)
        return if (res.isSuccessful) res.body() else null
    }

    suspend fun update(id: Int, update: CommunityUpdate): Community? {
        val res = api.updateCommunity(id, update)
        return if (res.isSuccessful) res.body() else null
    }

    suspend fun getMembers(communityId: Int): List<Member> {
        val res = api.getMembers(communityId)
        return if (res.isSuccessful) {
            val body = res.body() ?: emptyList()
            println(">>> miembros cargados correctamente: ${body.size}")
            body
        } else {
            println("Error al cargar miembros: ${res.code()} ${res.message()}")
            emptyList()
        }
    }

    suspend fun getReports(communityId: Int): List<ReportResponse> {
        val res = api.getReports(communityId)

        if (res.isSuccessful) {
            val reports = res.body() ?: emptyList()
            // Imprimir en log
            Log.d("ReportRepository", "Reports for community $communityId: $reports")
            return reports
        } else {
            Log.e("ReportRepository", "Failed to get reports: ${res.code()} - ${res.message()}")
            return emptyList()
        }
    }


    suspend fun getCommunityDetails(id: Int): CommunityResponse? {
        return try {
            // Obtener la comunidad base
            val communityResponse = api.getCommunity(id)
            if (!communityResponse.isSuccessful) return null

            val community = communityResponse.body() ?: return null

            // Obtener miembros y reportes (de forma secuencial)
            val members = getMembers(id)
            val reports = getReports(id)

            // Combinar todo en un solo objeto
            CommunityResponse(
                id = community.id,
                name = community.name,
                description = community.description,
                is_active = community.is_active,
                members = members,
                reports = reports
            )
        } catch (e: Exception) {
            Log.e("CommunityRepository", "Error al obtener detalles de comunidad", e)
            null
        }
    }
}