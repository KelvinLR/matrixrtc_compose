package com.example.matrixrtc_compose_app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier


@Composable
fun HomeScreen (modifier: Modifier = Modifier) {
    var enabled by remember { mutableStateOf(true) }
    Column (
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center){
        Button(
            enabled = enabled,
            onClick = {
                enabled = !enabled
            }
        ) {
            Text(
                if (enabled) "Iniciar sessão"
                else "Aguardando segundo peer"
            )
        }
    }

}