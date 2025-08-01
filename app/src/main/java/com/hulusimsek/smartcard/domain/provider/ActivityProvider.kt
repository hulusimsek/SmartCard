package com.hulusimsek.smartcard.domain.provider

interface ActivityProvider {
    fun getCurrentActivity(): Any? // Generic type to avoid Android dependency
    fun setCurrentActivity(activity: Any)
}