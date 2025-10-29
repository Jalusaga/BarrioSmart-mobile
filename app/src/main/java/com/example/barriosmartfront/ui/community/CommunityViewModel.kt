package com.example.barriosmartfront.ui.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.barriosmartfront.data.dto.community.CommunityCreate
import com.example.barriosmartfront.data.dto.community.CommunityUpdate
import com.example.barriosmartfront.data.dto.community.CommunityResponse
import com.example.barriosmartfront.data.repositories.CommunityRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CommunityViewModel(
    private val repository: CommunityRepository
) : ViewModel() {

    private val _communities = MutableStateFlow<List<CommunityResponse>>(emptyList())
    val communities: StateFlow<List<CommunityResponse>> = _communities

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadCommunities() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val data = repository.getAll()
                _communities.value = data ?: emptyList()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun createCommunity(name: String, description: String) {
        viewModelScope.launch {
            try {
                repository.create(CommunityCreate(name, description))
                loadCommunities()
            } catch (e: Exception) {
                _error.value = "Error al crear comunidad: ${e.message}"
            }
        }
    }

    fun updateCommunity(id: Int, name: String?, description: String?) {
        viewModelScope.launch {
            try {
                repository.update(id, CommunityUpdate(name, description))
                loadCommunities()
            } catch (e: Exception) {
                _error.value = "Error al actualizar comunidad: ${e.message}"
            }
        }
    }
}
