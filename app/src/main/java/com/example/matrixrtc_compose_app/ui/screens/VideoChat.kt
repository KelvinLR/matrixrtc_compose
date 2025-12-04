package com.example.matrixrtc_compose_app.ui.screens

import android.Manifest
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun VideoChat(nav: NavController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Permissões de câmera e áudio
    val permissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )
    )

    var cameraExecutor: ExecutorService? by remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor?.shutdown()
        }
    }

    // Se não tiver permissão, pede
    LaunchedEffect(permissionState.allPermissionsGranted) {
        if (!permissionState.allPermissionsGranted) {
            permissionState.launchMultiplePermissionRequest()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        if (permissionState.allPermissionsGranted) {
            // ===================== CÂMERA PRINCIPAL (PEER 1 - você) =====================
            Box(modifier = Modifier.fillMaxSize()) {
                CameraPreview(
                    modifier = Modifier.fillMaxSize(),
                    cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA // ou BACK_CAMERA
                )

                // Mini câmera do outro peer (simulada)
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .size(width = 120.dp, height = 180.dp)
                        .background(Color.DarkGray),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Peer 2", color = Color.White)
                }

                // Barra de controles inferior
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Desligar chamada
                    IconButton(
                        onClick = { nav.popBackStack() },
                        modifier = Modifier
                            .background(Color.Red, CircleShape)
                            .size(56.dp)
                    ) {
                        Icon(Icons.Filled.CallEnd, contentDescription = "Desligar", tint = Color.White)
                    }

                    // Vídeo off/on
                    IconButton(onClick = { /* toggle vídeo */ }) {
                        Icon(Icons.Filled.VideocamOff, tint = Color.White, contentDescription = "Vídeo")
                    }

                    // Microfone
                    IconButton(onClick = { /* toggle mic */ }) {
                        Icon(Icons.Filled.Mic, tint = Color.White, contentDescription = "Microfone")
                    }

                    // Virar câmera
                    IconButton(onClick = { /* trocar frente/traseira */ }) {
                        Icon(Icons.Filled.FlipCameraAndroid, tint = Color.White, contentDescription = "Virar")
                    }
                }
            }
        } else {
            // Tela de permissão negada ou ainda pedindo
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Precisamos da câmera e microfone para a chamada")
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { permissionState.launchMultiplePermissionRequest() }) {
                        Text("Permitir acesso")
                    }
                }
            }
        }
    }
}

// Componente que mostra a câmera usando CameraX + AndroidView
@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, ContextCompat.getMainExecutor(ctx))

            previewView
        }
    )
}