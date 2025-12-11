package com.example.matrixrtc_compose_app

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.matrixrtc_compose_app.rtc.WebRtcManager
import com.example.matrixrtc_compose_app.ui.screens.HomeScreen
import com.example.matrixrtc_compose_app.ui.screens.VideoChat
import com.example.matrixrtc_compose_app.ui.theme.Matrixrtc_compose_appTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // --- TESTE WEBRTC LOOPBACK ---
        val rtc = WebRtcManager(this)
        rtc.createPeerConnection()
        rtc.addDummyAudioTrack()
        loopbackTest(rtc)

        setContent {
            Matrixrtc_compose_appTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavigation(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }

    private fun loopbackTest(rtc: WebRtcManager) {



        rtc.onLocalSdp = { type, sdp ->
            Log.i("LOOPBACK", "LOCAL SDP ($type)")
            Log.i("SDP_DEBUG", "\n===== LOCAL SDP ($type) =====\n$sdp\n====================")

            rtc.setRemoteDescription(type, sdp) {
                // só aqui, quando remoteDescription for aplicada com sucesso:
                if (type == "offer") {
                    rtc.createAnswer()
                }
            }
        }

        rtc.onLocalIceCandidate = { candidate ->
            Log.i("LOOPBACK", "LOCAL ICE: $candidate")

            rtc.addIceCandidate(
                candidate.sdpMid,
                candidate.sdpMLineIndex,
                candidate.sdp
            )
        }

        Log.i("LOOPBACK", "=== INICIANDO OFERTA ===")
        rtc.createOffer()
    }
}




// composable para coordenar a navegação entre rotas
@Composable
fun AppNavigation(modifier: Modifier) {
    // controller do navigator responsável por realizar operações entre as telas
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        // rota inicial
        startDestination = "home"
    ) {
        // rotas do app com o controller passado por parâmetro
        composable("home") { HomeScreen(navController) }
        composable("video") { VideoChat(navController) }
    }
}