package com.hulusimsek.smartcard.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import com.hulusimsek.smartcard.domain.repository.IntentRepository
import javax.inject.Inject

class IntentRepositoryImpl @Inject constructor(
    private val context: Context
) : IntentRepository {

    override suspend fun openIntent(uri: String, packageName: String?, action: String?): Boolean {
        return try {
            val intent = Intent(action ?: Intent.ACTION_VIEW, Uri.parse(uri)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                packageName?.let { setPackage(it) }
            }

            val resolveInfo = intent.resolveActivity(context.packageManager)
            if (resolveInfo != null) {
                context.startActivity(intent)
                true
            } else {
                // Try without package if specified
                if (packageName != null) {
                    val genericIntent = Intent(action ?: Intent.ACTION_VIEW, Uri.parse(uri)).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    }
                    if (genericIntent.resolveActivity(context.packageManager) != null) {
                        context.startActivity(genericIntent)
                        return true
                    }
                }
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun isAppInstalled(packageName: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }
}