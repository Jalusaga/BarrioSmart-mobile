package com.example.barriosmartfront.data.repositories


import com.example.barriosmartfront.data.auth.ITokenStore
import com.example.barriosmartfront.data.dto.community.Community
import com.example.barriosmartfront.data.dto.community.CommunityCreate
import com.example.barriosmartfront.data.dto.community.CommunityResponse
import com.example.barriosmartfront.data.dto.community.CommunityUpdate
import com.example.barriosmartfront.data.dto.member.Member
import com.example.barriosmartfront.data.dto.report.Report
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
                    isActive = community.is_active,
                    isJoined = false
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
        return if (res.isSuccessful) res.body() ?: emptyList() else emptyList()
    }

    suspend fun getReports(communityId: Int): List<Report> {
        val res = api.getReports(communityId)
        return if (res.isSuccessful) res.body() ?: emptyList() else emptyList()
    }
}