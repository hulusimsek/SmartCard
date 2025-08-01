package com.hulusimsek.smartcard.presentation.common

import android.content.Context
import com.hulusimsek.smartcard.core.util.ResourceProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ResourceProviderImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : ResourceProvider {
    override fun getString(resId: Int): String = context.getString(resId)

    override fun getString(resId: Int, vararg args: Any): String {
        return context.getString(resId, *args)
    }
}