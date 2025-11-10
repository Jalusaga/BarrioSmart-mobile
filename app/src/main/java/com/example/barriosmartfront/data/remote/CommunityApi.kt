package com.example.barriosmartfront.data.remote


import com.example.barriosmartfront.data.dto.community.Community
import com.example.barriosmartfront.data.dto.community.CommunityCreate
import com.example.barriosmartfront.data.dto.community.CommunityResponse
import com.example.barriosmartfront.data.dto.community.CommunityUpdate
import com.example.barriosmartfront.data.dto.member.Member
import com.example.barriosmartfront.data.dto.report.Report
import com.example.barriosmartfront.data.dto.report.ReportResponse
import retrofit2.Response
import retrofit2.http.*

interface CommunityApi {

    @GET("communities/")
    suspend fun getCommunities(): Response<List<CommunityResponse>>

    @GET("communities/{id}")
    suspend fun getCommunity(@Path("id") id: Int): Response<Community>

    @POST("communities/")
    suspend fun createCommunity(@Body community: CommunityCreate): Response<Community>

    @PATCH("communities/{id}")
    suspend fun updateCommunity(
        @Path("id") id: Int,
        @Body updates: CommunityUpdate
    ): Response<Community>

    @GET("community-members/{community_id}")
    suspend fun getMembers(@Path("community_id") communityId: Int): Response<List<Member>>

    @GET("reports/community/{community_id}")
    suspend fun getReports(@Path("community_id") communityId: Int): Response<List<ReportResponse>>

}