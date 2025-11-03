package com.example.barriosmartfront.ui.report



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.barriosmartfront.data.dto.auth.RegisterResponse
import com.example.barriosmartfront.data.dto.community.CommunityResponse
import com.example.barriosmartfront.data.dto.report.Report
import com.example.barriosmartfront.data.dto.report.ReportResponse
import com.example.barriosmartfront.data.repositories.ReportsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReportViewModel(private val repo: ReportsRepository) : ViewModel() {

    private val _communities = MutableStateFlow<List<CommunityResponse>>(emptyList())
    val communities: StateFlow<List<CommunityResponse>> = _communities

    private val _members = MutableStateFlow<List<RegisterResponse>>(emptyList())
    val members: StateFlow<List<RegisterResponse>> = _members

    private val _reports = MutableStateFlow<List<ReportResponse>>(emptyList())
    val reports: StateFlow<List<ReportResponse>> = _reports

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    fun fetchReports() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val communityData = repo.getCommunities()
                _communities.value = communityData

                val membersData = repo.getUsers()
                _members.value = membersData

                val response = repo.getAllReports()
                _reports.value = response ?: emptyList()

            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Error desconocido"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createReport(report: Report) {
        viewModelScope.launch {
            try {
                repo.createReport(report)
                fetchReports()
            } catch (e: Exception) {
                _error.value = "Error al crear reporte: ${e.message}"
            }
        }
    }

    fun updateReport(report: ReportResponse) {
        viewModelScope.launch {
            try {

                repo.updateReport(report.id, report )
                fetchReports()
            } catch (e: Exception) {
                _error.value = "Error al actualizar reporte: ${e.message}"
            }
        }
    }


}