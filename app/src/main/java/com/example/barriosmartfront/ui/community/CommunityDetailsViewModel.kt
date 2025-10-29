package com.example.barriosmartfront.ui.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.barriosmartfront.data.dto.community.CommunityResponse
import com.example.barriosmartfront.data.dto.member.Member
import com.example.barriosmartfront.data.dto.report.Report
import com.example.barriosmartfront.data.repositories.CommunityRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CommunityDetailsViewModel(
    private val repository: CommunityRepository
) : ViewModel() {

    private val _community = MutableStateFlow<CommunityResponse?>(null)
    val community: StateFlow<CommunityResponse?> = _community

    private val _members = MutableStateFlow<List<Member>>(emptyList())
    val members: StateFlow<List<Member>> = _members

    private val _reports = MutableStateFlow<List<Report>>(emptyList())
    val reports: StateFlow<List<Report>> = _reports

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadCommunityDetails(communityId: Int) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val communityData = repository.getById(communityId)
                _community.value = communityData

                val membersData = repository.getMembers(communityId)
                _members.value = membersData

                val reportsData = repository.getReports(communityId)
                _reports.value = reportsData

            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }
}
