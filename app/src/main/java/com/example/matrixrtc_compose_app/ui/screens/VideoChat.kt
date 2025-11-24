package com.example.matrixrtc_compose_app.ui.screens
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.IconButton
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color

@Composable
fun VideoChat(modifier: Modifier) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom
    ){
        Box (modifier = Modifier.background(Color.Gray)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(modifier = Modifier
                    .background(color = Color.Red, CircleShape)
                    .clip(CircleShape), onClick = {}) {
                    Icon(
                        imageVector = Icons.Filled.Call,
                        tint = Color.White,
                        contentDescription = "Desligar"
                    )
                }
                IconButton(modifier = Modifier
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
                    .clip(CircleShape), onClick = {}) {
                    Icon(
                        imageVector = Icons.Filled.Mic,
                        tint = Color.White,
                        contentDescription = "Microfone"
                    )
                }
                IconButton(modifier = Modifier
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
                    .clip(CircleShape), onClick = {}) {
                    Icon(
                        imageVector = Icons.Filled.FlipCameraAndroid,
                        tint = Color.White,
                        contentDescription = "Virar câmera"
                    )
                }
            }
        }
    }
}