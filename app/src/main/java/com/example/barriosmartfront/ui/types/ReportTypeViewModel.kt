package com.example.barriosmartfront.ui.types

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.barriosmartfront.data.dto.report.ReportType
import com.example.barriosmartfront.data.repositories.ReportTypeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response


class ReportTypeViewModel(private val repo: ReportTypeRepository) : ViewModel() {

    private val _reportTypes = MutableStateFlow<List<ReportType>>(emptyList())
    val reportTypes: StateFlow<List<ReportType>> get() = _reportTypes

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    fun fetchReportTypes() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response: Response<List<ReportType>> = repo.getReportTypes()
                if (response.isSuccessful) {
                    _reportTypes.value = response.body() ?: emptyList()
                } else {
                    _error.value = "Error: ${response.code()} ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Error desconocido"
            } finally {
                _isLoading.value = false
            }
        }
    }
}