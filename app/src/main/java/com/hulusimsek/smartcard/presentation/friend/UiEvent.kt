package com.hulusimsek.smartcard.presentation.friend

sealed class SocialMediaUiEvent {
    data class ShowSnackbar(val message: String) : SocialMediaUiEvent()
    data class ShowError(val message: String) : SocialMediaUiEvent()
    data class ShowSuccess(val message: String) : SocialMediaUiEvent()
    // Başka event'ler de olabilir
}