package com.hulusimsek.smartcard.presentation.home.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hulusimsek.smartcard.R
import com.hulusimsek.smartcard.presentation.common.extensions.getFloatDimen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareOptionBottomSheet(
    onDismiss: () -> Unit,
    onStartNfc: () -> Unit,
    onShareViaNfc: () -> Unit,
    onOpenQrCamera: () -> Unit,
    onShareViaQr: () -> Unit,
    isGeneratingQrCode: Boolean = false,
    nfcReader: Boolean,
    nfcEnabled: Boolean,
    onToggleNfc: (Boolean) -> Unit
) {

    val context = LocalContext.current

    if (isGeneratingQrCode) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth(),
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = RoundedCornerShape(topStart = dimensionResource(id = R.dimen.corner_radius_medium), topEnd = dimensionResource(id = R.dimen.corner_radius_medium))
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = dimensionResource(id = R.dimen.padding_medium), horizontal = dimensionResource(id = R.dimen.padding_large))
                .fillMaxWidth()
        ) {
            // NFC
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(checked = nfcEnabled && nfcReader, onCheckedChange = onToggleNfc)
                    Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.padding_small)))
                    Text(stringResource(id = R.string.nfc_listen))
                }

                Button(
                    onClick = { onShareViaNfc() },
                    enabled = true, // her zaman tıklanabilir
                    modifier = Modifier.alpha(if (!nfcEnabled) context.getFloatDimen(R.dimen.disabled_alpha) else context.getFloatDimen(R.dimen.activated_alpha)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (!nfcEnabled) Color.Gray else MaterialTheme.colorScheme.primary,
                        contentColor = if (!nfcEnabled) Color.LightGray else MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(stringResource(id = R.string.share_with_nfc))
                }
            }

            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_large)))

            // QR
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = onOpenQrCamera) {
                    Text(stringResource(id = R.string.qr_camera))
                }

                Button(onClick = onShareViaQr) {
                    Text(stringResource(id = R.string.share_via_qr))
                }
            }
        }
    }
}