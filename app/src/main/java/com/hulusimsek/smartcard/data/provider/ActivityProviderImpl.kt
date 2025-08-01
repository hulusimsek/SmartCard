package com.hulusimsek.smartcard.data.provider

import androidx.activity.ComponentActivity
import com.hulusimsek.smartcard.domain.provider.ActivityProvider
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActivityProviderImpl @Inject constructor() : ActivityProvider {
    private var currentActivity: ComponentActivity? = null

    override fun getCurrentActivity(): ComponentActivity? = currentActivity

    override fun setCurrentActivity(activity: Any) {
        if (activity is ComponentActivity) {
            currentActivity = activity as ComponentActivity
        }
    }
}