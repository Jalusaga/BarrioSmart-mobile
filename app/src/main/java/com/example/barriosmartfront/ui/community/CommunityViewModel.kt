package com.example.barriosmartfront.ui.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.barriosmartfront.data.dto.community.Community
import com.example.barriosmartfront.data.dto.community.CommunityCreate
import com.example.barriosmartfront.data.dto.community.CommunityUpdate
import com.example.barriosmartfront.data.dto.community.CommunityResponse
import com.example.barriosmartfront.data.dto.member.Member
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

    private val _community = MutableStateFlow<CommunityResponse?>(null)
    val community: StateFlow<CommunityResponse?> = _community

    // 👇 NUEVO: ids de comunidades a las que el usuario YA está unido
    private val _joinedCommunities = MutableStateFlow<Set<Int>>(emptySet())
    val joinedCommunities: StateFlow<Set<Int>> = _joinedCommunities
    private val _members = MutableStateFlow<List<Member>>(emptyList())
    val members: StateFlow<List<Member>> = _members

    fun loadCommunities() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                // 1. cargar comunidades
                val list = repository.getAll() ?: emptyList()
                _communities.value = list

                // 2. cargar memberships del usuario actual
                val joinedIds = repository.getJoinedCommunityIdsForCurrentUser()
                _joinedCommunities.value = joinedIds
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
    fun joinCommunity(communityId: Int) {
        viewModelScope.launch {
            try {
                val ok = repository.joinCommunity(communityId)
                if (ok) {
                    // Agregamos el ID a la lista de unidas para deshabilitar el botón
                    _joinedCommunities.value = _joinedCommunities.value + communityId
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}
