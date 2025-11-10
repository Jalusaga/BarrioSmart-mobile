package com.example.barriosmartfront.ui.community

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.barriosmartfront.data.dto.community.CommunityResponse
import com.example.barriosmartfront.data.dto.member.Member
import com.example.barriosmartfront.data.dto.report.Report
import com.example.barriosmartfront.data.dto.report.ReportResponse
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

    private val _reports = MutableStateFlow<List<ReportResponse>>(emptyList())
    val reports: StateFlow<List<ReportResponse>> = _reports

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // --- Cargar detalles de una comunidad ---
    fun loadCommunityDetails(id: Int) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val result = repository.getCommunityDetails(id)
                if (result != null) {
                    _community.value = result
                } else {
                    _error.value = "No se encontró la comunidad"
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    // --- Cargar miembros de una comunidad específica ---
    fun loadMembers(communityId: Int) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val result = repository.getMembers(communityId)
                _members.value = result ?: emptyList()
            } catch (e: Exception) {
                _error.value = "Error al obtener miembros: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }


    fun loadReports(id: Int) {
        viewModelScope.launch {
            try {
                val data = repository.getReports(id)
                _reports.value = data ?: emptyList()
            } catch (e: Exception) {
                _error.value = "Error al cargar reportes: ${e.message}"
            }
        }
    }
}
