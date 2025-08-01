package com.hulusimsek.smartcard.presentation.main

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hulusimsek.smartcard.presentation.all_user.AllUsersScreen
import com.hulusimsek.smartcard.presentation.edit.EditScreen
import com.hulusimsek.smartcard.presentation.friend.FriendScreen
import com.hulusimsek.smartcard.presentation.home.HomeScreen
import com.hulusimsek.smartcard.presentation.home.HomeViewModel
import com.hulusimsek.smartcard.presentation.home.NfcUiEvent
import com.hulusimsek.smartcard.presentation.social_media_account.SocialMediaAccountScreen
import com.hulusimsek.smartcard.presentation.theme.SmartCardTheme
import dagger.hilt.android.AndroidEntryPoint
import java.net.URLDecoder

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: HomeViewModel by viewModels()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.setCurrentActivity(this)
        viewModel.checkNfcStatus()

        val uri = intent?.data
        val dataParam = uri?.getQueryParameter("data")
        if (!dataParam.isNullOrEmpty()) {
            Log.e("applink", "Gelen veri: $dataParam")

            // Compose henüz yüklenmeden önce navController olmadığı için geçici çözüm:
            // ViewModel'e aktar → LaunchedEffect içinde yakala
            viewModel.handleAppLink(dataParam)
        }

        // Jetpack Compose UI
        setContent {
            SmartCardTheme {

                LaunchedEffect(key1 = true) {
                    Log.e("nfcdeneme", "MainActivity LaunchedEffect")
                    viewModel.uiEvent.collect { event ->
                        Log.e("nfcdeneme", "MainActivity event $event")
                        when (event) {
                            is NfcUiEvent.ShowNfcUnavailable -> {
                                Toast.makeText(this@MainActivity, "Bu cihazda NFC yok", Toast.LENGTH_LONG).show()
                            }

                            is NfcUiEvent.ShowNfcSettingsDialog -> {
                                showNfcSettingsDialog()
                            }

                            is NfcUiEvent.NfcReady -> {
                                // NFC açık, işlem yapılabilir
                            }
                        }

                    }
                }
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "home") {
                    composable("home") {
                        HomeScreen(
                            viewModel = viewModel,
                            navController = navController
                        )
                    }
                    composable("nfc/{data}") { backStackEntry ->
                        val data = backStackEntry.arguments?.getString("data") ?: ""
                        FriendScreen(
                            jsonData = data,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                    composable("user_detail/{id}") { backStackEntry ->
                        val userId = backStackEntry.arguments?.getString("id") ?: ""
                        FriendScreen(
                            userId = userId,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                    composable("edit") {
                        EditScreen(
                            navController = navController,
                            onBack = { navController.popBackStack() }
                        )
                    }
                    composable("social_media_account") {
                        SocialMediaAccountScreen(
                            navController = navController,
                            onBack = { navController.popBackStack() }
                        )
                    }
                    composable("all_users") {
                        AllUsersScreen(
                            navController = navController,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                }

                LaunchedEffect(key1 = true) {
                    viewModel.deepLinkData.collect { data ->
                        if (!data.isNullOrEmpty()) {
                            Log.e("applink", "NavController ile yönlendir: $data")
                            navController.navigate("nfc/${data}") {
                                launchSingleTop = true
                            }
                            viewModel.clearDeepLinkData()
                        }
                    }
                }
            }
        }

    }

    override fun onPause() {
        super.onPause()
        viewModel.disableNfcAll()
    }

    override fun onResume() {
        super.onResume()
        viewModel.resumeNfc()
        viewModel.checkNfcStatus()
    }

    private fun showNfcSettingsDialog() {
        Log.e("nfcdeneme", "MainActivity showNfcSettingsDialog")
        AlertDialog.Builder(this)
            .setTitle("NFC Kapalı")
            .setMessage("NFC özelliğini kullanmak için NFC'yi açmanız gerekiyor. Ayarlara gitmek ister misiniz?")
            .setPositiveButton("Ayarlara Git") { _, _ ->
                openNfcSettings()
            }
            .setNegativeButton("İptal", null)
            .show()
    }

    private fun openNfcSettings() {
        try {
            val intent = Intent(Settings.ACTION_NFC_SETTINGS)
            startActivity(intent)
        } catch (e: Exception) {
            val intent = Intent(Settings.ACTION_SETTINGS)
            startActivity(intent)
            Toast.makeText(this, "NFC ayarlarını bulup açınız", Toast.LENGTH_LONG).show()
        }
    }
}