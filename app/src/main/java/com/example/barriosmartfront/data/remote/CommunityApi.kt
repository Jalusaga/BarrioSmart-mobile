package com.example.barriosmartfront.data.remote


import com.example.barriosmartfront.data.dto.community.Community
import com.example.barriosmartfront.data.dto.community.CommunityCreate
import com.example.barriosmartfront.data.dto.community.CommunityResponse
import com.example.barriosmartfront.data.dto.community.CommunityUpdate
import com.example.barriosmartfront.data.dto.member.Member
import com.example.barriosmartfront.data.dto.report.Report
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

    @GET("communities/{id}/members")
    suspend fun getMembers(@Path("id") communityId: Int): Response<List<Member>>

    @GET("communities/{id}/reports")
    suspend fun getReports(@Path("id") communityId: Int): Response<List<Report>>


}