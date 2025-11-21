package com.example.matrixrtc_compose_app.ui.screens

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


@Composable
fun HomeScreen (modifier: Modifier = Modifier) {
    val enabled: Boolean = true;
    Button(
       onClick = {}
    ) {
        if(enabled) {
            Text("Iniciar sessão")
        } else {
            Text("Aguardando segundo peer")
        }
    }
}