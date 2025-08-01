package com.hulusimsek.smartcard.presentation.all_user

import com.hulusimsek.smartcard.domain.model.User

data class AllUsersUiState(
    val isLoading: Boolean = false,
    val users: List<User> = emptyList(),
    val filteredUsers: List<User> = emptyList(),
    val error: String? = null,
    val isSearchMode: Boolean = false
)