package com.hulusimsek.smartcard.presentation.all_user

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hulusimsek.smartcard.core.util.Constants
import com.hulusimsek.smartcard.domain.model.User
import com.hulusimsek.smartcard.domain.usecase.UserUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllUsersViewModel @Inject constructor(
    private val userUseCases: UserUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(AllUsersUiState())
    val uiState: StateFlow<AllUsersUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        observeUser()
        // Arama sorgusu değiştiğinde filtreleme yap
        viewModelScope.launch {
            searchQuery.collect { query ->
                filterUsers(query)
            }
        }
    }

    fun deleteUser(user: User) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            userUseCases.deleteUser(user)
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun clearSearch() {
        _searchQuery.value = ""
    }

    fun toggleSearchMode() {
        _uiState.update {
            it.copy(isSearchMode = !it.isSearchMode)
        }
        // Arama modu kapatılırken arama sorgusunu temizle
        if (!_uiState.value.isSearchMode) {
            clearSearch()
        }
    }

    fun closeSearchMode() {
        _uiState.update {
            it.copy(isSearchMode = false)
        }
        // Arama modu kapatılırken arama sorgusunu temizle
        if (!_uiState.value.isSearchMode) {
            clearSearch()
        }
    }

    private fun filterUsers(query: String) {
        val currentUsers = _uiState.value.users
        val filteredUsers = if (query.isBlank()) {
            currentUsers
        } else {
            currentUsers.filter { user ->
                val fullName = "${user.firstName ?: ""} ${user.lastName ?: ""}".trim()

                fullName.contains(query, ignoreCase = true) ||
                        (user.firstName?.contains(query, ignoreCase = true) == true) ||
                        (user.lastName?.contains(query, ignoreCase = true) == true)
            }
        }

        _uiState.update {
            it.copy(filteredUsers = filteredUsers)
        }
    }

    private fun observeUser() {
        Log.e("AllUserDeneme", "deneme")
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            userUseCases.getAllUserStream().collect { userList ->
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        users = userList.filter { it.id !=  Constants.DEFAULT_USER_ID}
                    )
                }
                filterUsers(_searchQuery.value)
            }
        }
    }
}